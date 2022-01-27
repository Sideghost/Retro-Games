package snake// Import that allows to make sounds during the game.
import pt.isel.canvas.playSound


// snake.Game Grid Features.
const val CELL_SIDE = 32                                    // Pixel dimension of each snake.grid square.
const val GRID_WIDTH = 20                                   // Number of cells horizontally.
const val GRID_HEIGHT = 16                                  // Number of cells vertically.
const val SPRITE_DIV = 64                                   // Sprite Division in snake file ("snake.png").

// snake.Game features.
const val INIT_SCORE = 0                                    // Initial score in game.
const val INIT_TO_GROW = 0                                  // Initial to grow factor.
val INIT_SNAKE = Snake(
    listOf(Position(GRID_WIDTH / 2, GRID_HEIGHT / 2)),
    Direction.RIGHT,
    INIT_TO_GROW
) // Initial snake.Snake of each level.

// snake.Status Bar Features
const val STATUS_BAR = 40                                   // snake.Status bar height.
const val FIVE_CELLS = CELL_SIDE * 5                        // Score text origin on snake.Status Bar and status lose or lose origin.
const val TEXT_BASE = 10                                    // Origin Point of the text on.
const val FONT_SIZE = 25                                    // Font size on snake.Status Bar.
const val SIX_CELLS = CELL_SIDE * 6                         // Pause snake.Status origin in snake.Status Bar.

// Apple Origins on snake.Snake File (snake.png).
const val APPLE_X_FILE = 0                                  // X origin of apple in snake file ("snake.png").
const val APPLE_Y_FILE = 3                                  // Y origin of apple in snake file ("snake.png").

// Spawn Rate and snake.Snake Velocity.
const val BLOCK_SPAWN_TIMER = 5000                          // Spawn Block Rate.
const val QUART_OF_A_SEC = 250                              // Velocity of the snake on each movement.

// Hacking Features
const val Level_WIN = 60                                    // Level score to Win.
const val INIT_SIZE = 5                                     // Initial max size of the snake.
const val LEVEL_ONE = 1                                     // Level one.
const val LEVEL_TWO = 2                                     // Level two.
const val LEVEL_THREE = 3                                   // level three.
private const val EASTER_EGG = 11                           // Easter egg.


/**
 * The four possible game states.
 */
enum class Status { RUN, PAUSE, WIN, LOSE }


/**
 * Class that defines the whole game.
 * @property snake object with movement in the game.
 * @property wall obstacles that stop movement of the [snake].
 * @property apple fruit that makes [snake] grow bigger.
 * @property score numbers of apples eaten.
 * @property status current state of the game.
 */
data class Game(val snake: Snake,
                val wall: List<Position>,
                val apple: Position?,
                val score: Int,
                val status: Status,
                val hacking: Hack
)


/**
 * Class that defines a few variables to the game.
 * @property hackedApple special apple presented in lv.2 and lv.3 .
 * @property grid game snake.grid.
 * @property sound sounds made by the game.
 * @property level current game level.
 */
data class Hack(
    val hackedApple: Position? = null,
    val grid: Boolean = false,
    val sound: Boolean = false,
    val menu: Boolean = false,
    val level: Int = 1
)


/**
 * Function that Creates the initial snake.Game.
 * @return Initial snake.Game.
 */
fun createGame() = Game(INIT_SNAKE, initBlocks(), initApple(initBlocks()), INIT_SCORE, Status.RUN, Hack())


/**
 * Function responsible for recognizing whether the game is over.
 * @receiver snake.Game to check.
 * @return snake.Game checked.
 */
fun Game.isPossible(): Game {
    val impPos = snake.body + wall
    val newStatus = if (snake.isLocked(impPos)) {
        if(snake.body.size >= Level_WIN) Status.WIN
        else Status.LOSE
    }
    else Status.RUN
    return this.copy(status = newStatus)
}


/**
 * Function responsible to see if the snake.Snake is Locked.
 * @receiver snake.Snake to check positions.
 * @return Boolean.
 */
fun Snake.isLocked(impossiblePositions:List<Position>) =listOf(
        (body[0] + Direction.RIGHT).normalize(),
        (body[0] + Direction.UP).normalize(),
        (body[0] + Direction.DOWN).normalize(),
        (body[0] + Direction.LEFT).normalize()
).all{ it in impossiblePositions }


/**
 * Function that updates the snake every quart of a second and moves the snake if it's possible.
 * @receiver snake.Game to update.
 * @return @return a new altered game or the same state of the game depending on GAME_STATUS.
 */
fun Game.step(): Game {
    if (status!= Status.RUN) return this
    val applesEaten = move().appleGetsEaten().hackedAppleGetsEaten(
        if (hacking.level == LEVEL_TWO) "snakeResources\\eat.wav" else "snakeResources\\poison_eat.wav", hacking.hackedApple , hacking.level)
    val possibleGame = applesEaten.isPossible()
    return possibleGame.copy(
        apple = possibleGame.createRandomApple(),
        hacking = possibleGame.hacking.copy(hackedApple =possibleGame.createRandomHackedApple(possibleGame.hacking.hackedApple))
    )
}


///**
// * Function that updates the snake from the Input keys.
// * @receiver snake.Game to update.
// * @return snake.Game updated.
// */
//fun snake.Game.snakeInput(key:Int):snake.Game{
//    return if (!this.hacking.snake.menu) copy(snake = snake.snakeDirection(key, this))
//    else snake.options(key, this)
//}


//----------------------------------------------------EXTRA-------------------------------------------------------------
/**
 * Function responsible for the construction of any level.
 * @param game game to be altered.
 * @return a new game level.
 */
private fun gameLevel(game: Game) = game.copy(snake = INIT_SNAKE, status = Status.RUN)


/**
 * Function responsible for returning the next lv.
 */
private fun Game.nextLv() =
    when {
        status == Status.WIN && hacking.level == LEVEL_ONE -> {
            if (hacking.sound) playSound("snakeResources\\Win.wav")
            gameLevel(copy(apple = initApple(initBlocksTwo()), wall = initBlocksTwo(), hacking = hacking.copy(level = LEVEL_TWO)))
        }

        status == Status.WIN && hacking.level == LEVEL_TWO -> {
            if (hacking.sound) playSound("snakeResources\\Win.wav")
            gameLevel(copy(apple = initApple(initBlocksThree()), wall = initBlocksThree(), hacking = hacking.copy(level = LEVEL_THREE)))
        }

        status == Status.LOSE -> {
            if (hacking.sound) playSound("snakeResources\\Defeat.wav")
            this
        }

        status == Status.LOSE && hacking.level == LEVEL_THREE && score >= EASTER_EGG -> {
            if (hacking.sound) playSound("snakeResources\\Victory.wav")
            this
        }

        else -> this
    }


/**
 * Function that enables/disables the existence of a snake.grid.
 * @return a new game with changed fixture.
 */
private fun grid(game: Game): Game = game.copy(hacking = game.hacking.copy(grid = true))


/**
 * Function that enables/disables the existence of sounds.
 * @return a new game with changed fixture.
 */
private fun sound(game: Game): Game = game.copy(hacking = game.hacking.copy(sound = true))


/**
 * Function that enables/disables the existence of a snake.Game Menu.
 * @return a new game with changed fixture.
 */
private fun menu(game: Game): Game =
    if (!game.hacking.menu) game.copy(hacking = game.hacking.copy(menu = true), status = Status.PAUSE)
    else game.copy(hacking = game.hacking.copy(menu = false), status = Status.RUN)



///**
// * Function that resets the level for you to try again.
// * @receiver snake.Game to reset level.
// * @return new game restarted.
// * //TODO(NOT YET IMPLEMENTED CORRECTLY) make a list of list about the score.
// */
//private fun snake.Game.resetLevel(): snake.Game {
//    val scoreList = mutableListOf(snake.INIT_SCORE)
//    return when(status){
//        snake.Status.LOSE -> when(hacking.level){
//            1 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocks(), score = snake.INIT_SCORE)
//            2 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocksTwo(), score = if (scoreList.size == 1) scoreList[scoreList.size-1] else (scoreList.dropLast(1)[scoreList.size-1]))
//            3 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocksThree(), score = if (scoreList.size == 2) scoreList[scoreList.size-1] else (scoreList.dropLast(1)[scoreList.size-1]))
//            else -> this
//        }
//        snake.Status.WIN -> when(hacking.level){
//            1 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocks(), score = if(scoreList.size == 1) (scoreList + score)[scoreList.size-2] else (scoreList.dropLast(1) + score)[scoreList.size-2])
//            2 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocksTwo(), score = if(scoreList.size == 1) (scoreList + score)[scoreList.size-2] else (scoreList.dropLast(1) + score)[scoreList.size-2])
//            3 -> copy(status = snake.Status.RUN, snake = snake.getINIT_SNAKE, wall = snake.initBlocksThree(), score = if(scoreList.size == 2) (scoreList + score)[scoreList.size-2] else (scoreList.dropLast(1) + score)[scoreList.size-2])
//            else -> this
//        }
//
//        else -> this
//    }
//}
//

/**
 * Function responsible for activating the hacking characteristics of the game.
 */
fun options(key: Int, game: Game) = when (key) {
    'P'.code -> if (!game.hacking.menu) menu(game)
    else menu(game)
    'G'.code -> if (!game.hacking.grid) grid(game)
    else game.copy(hacking = game.hacking.copy(grid = false))

    'S'.code -> if (!game.hacking.sound) sound(game)
    else game.copy(hacking = game.hacking.copy(sound = false))

    'J'.code -> when (game.hacking.level) {
        1 -> gameLevel(game.copy(apple = initApple(initBlocksTwo()), wall = initBlocksTwo(), hacking = game.hacking.copy(level = LEVEL_TWO)))

        2 -> gameLevel(game.copy(apple = initApple(initBlocksThree()), wall = initBlocksThree(), hacking = game.hacking.copy(level = LEVEL_THREE)))

        else -> game
    }

    'N'.code -> game.nextLv()

    //'R'.code -> game.resetLevel()

    else -> game
}
//TODO (fazer uma lista que da apend ao score sempre que se muda de nivel)
