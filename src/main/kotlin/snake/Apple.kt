package snake

//IMPORT THAT ALLOWS TO HAVE SOUNDS ON THE GAME
import pt.isel.canvas.playSound


/**
 * Function that chooses randomly an initial apple position for any level.
 * @return snake.snake.Position of the initial apple.
 */
fun initApple(blocks:List<Position>) = (ALL_POSITIONS - Position(GRID_WIDTH / 2, GRID_HEIGHT / 2) - blocks).random()


/**
 * Function that checks if exists a Hacked apple and if it is possible to draw it.
 * @receiver snake.Game positions.
 * @param hackedApple Hacked apple.
 * @return a random position when exists any if not, returns nothing.
 */
fun Game.createRandomHackedApple(hackedApple: Position?) = when (hacking.level != LEVEL_ONE){
    hackedApple == null && ALL_POSITIONS.isNotEmpty() -> (ALL_POSITIONS - snake.body - wall - apple).random()
    hackedApple == null && ALL_POSITIONS.isEmpty() -> null
    hackedApple != null -> hackedApple
    else -> null
}


/**
 * Function that checks if exists an apple and if it is possible to draw it.
 * @receiver snake.Game positions.
 * @return Random position when exists at least one valid position.
 */
fun Game.createRandomApple() : Position? {
    if (apple != null) return apple
    val freePositions = ALL_POSITIONS - snake.body - wall - hacking.hackedApple - hacking.hackedApple
    return if (freePositions.isEmpty()) null else freePositions.random()
}


/**
 * Function that verify if an apple gets eaten and all stats related to that.
 * @receiver snake.Game proprieties.
 * @return a new game, if the apple gets eaten makes a characteristic Sound.
 */
fun Game.appleGetsEaten() =
    if (snake.body[0] == apple) {
        if (hacking.sound) playSound("snakeResources\\eat.wav")
        this.copy(snake = this.snake.copy(toGrow = snake.toGrow + 5), apple = null, score = score + 1)
    } else this


/**
 * Function that verify if a hacked apple gets eaten and all stats related to that.
 * @receiver snake.Game proprieties.
 * @param sound sound that the game makes if enable when apple gets eaten.
 * @param hackedApple position of the hacked apple.
 * @param level current level of the game.
 * @return a new game, if the apple gets eaten makes a characteristic Sound.
 */
fun Game.hackedAppleGetsEaten(sound:String, hackedApple: Position?, level:Int) =
    if (snake.body[0] == hackedApple) {
        if (hacking.sound) playSound(sound)
        when(level){
            2 -> this.copy(snake = this.snake.copy(toGrow = snake.toGrow + 10), score = score + 2,
                hacking = hacking.copy(hackedApple = null))

            3 -> if (snake.body.size > 5)
                this.copy(snake = this.snake.copy(body = snake.body.dropLast(3)), score = score - 2,
                    hacking = hacking.copy(hackedApple = null))

            else this.copy(score = score - 2, hacking = hacking.copy(hackedApple = null))

            else -> this
        }
    }
    else this

