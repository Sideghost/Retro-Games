package arkanoid

import pt.isel.canvas.Canvas
import pt.isel.canvas.MouseEvent
import pt.isel.canvas.WHITE


/** Type containing the state of the area
 * @property width area width
 * @property height area height
 */
data class Area(val width: Int, val height: Int)

data class Flags(
    val levels: Boolean = false,
    val finished: Boolean = false,
    val sound: Boolean = false,
    val gifts: Boolean = false
)

/**
 * Type containing all the necessary state for the game
 * @property area contains the state of the area
 * @property balls of the area
 * @property racket racket to bounce the balls off
 */
data class Game(
    val area: Area,
    val balls: List<Ball>,
    val racket: Racket,
    val bricks: List<Brick>,
    val score: Int,
    val extras: Extras
)

data class Extras(
    val effects: List<EffectType> = emptyList(),
    val gifts: List<Gift> = emptyList(),
    val ballsLeft: Int = 5,
    val currentLevel: Int = 0,
    val flags: Flags
)

/**
 * Renders an entire game frame
 * @receiver Canvas to draw to
 * @param game to be drawn
 */
fun Canvas.render(game: Game) {
    erase()
    game.bricks.forEach { brick ->
        draw(brick, game.extras.flags.gifts)
    }
    game.extras.gifts.forEach { gift ->
        draw(gift)
    }
    game.balls.forEach { ball ->
        draw(ball)
    }
    draw(game.racket)
    drawText(game.area.width / 2, game.area.height - 5, game.score.toString(), WHITE, 32)
    (0 until game.extras.ballsLeft).forEach { i ->
        draw(
            Ball(
                Position(BALL_RADIUS * 2 + i * (BALL_RADIUS * 3), game.area.height - BALL_RADIUS - 5),
                Velocity(0, 0),
                BALL_RADIUS
            )
        )
    }
    if (game.extras.flags.finished)
        drawText(
            60, height / 2,
            if (game.extras.flags.finished && game.extras.ballsLeft > 0 && game.balls.isNotEmpty())
                "You Won!"
            else
                "You lost!",
            WHITE, 64
        )
    if (game.extras.flags.levels) {
        drawText(width / 2 + 80, height - 5, "Level: " + (game.extras.currentLevel + 1).toString(), WHITE, 28)
    }
}

data class Collisions(val brickCollisions: List<BrickCollision>, val racketCollisions: List<Velocity>)

/**
 * Updates the game
 * @receiver game which contains the balls
 * @return updated game instance
 */
fun Game.update(): Game {
    if (extras.flags.finished) return this

    val (newBalls, collisions) = balls.update(bricks, racket, area, extras.flags, extras.effects)

    val (brickCollisions, racketCollisions) = collisions

    //Ending game or switching level
    if (newBalls.isEmpty() && extras.ballsLeft <= 0 ||
        bricks.size <= bricks.count { brick -> brick.type == BrickType.GOLD }
    ) {
        return if (extras.currentLevel == levels.size - 1 || !extras.flags.levels) {
            playSound("arkanoidResources\\gameOver")
            finishGame()
        } else {
            this.copy(
                area = Area(area.width, area.height),
                balls = emptyList(),
                bricks = getLevel(extras.currentLevel + 1),
                extras = extras.copy(currentLevel = extras.currentLevel + 1)
            )
        }
    }

    val (newBricks, destroyedBricks) = bricks.update(brickCollisions)

    val newGifts = if (!extras.flags.gifts) extras.gifts else destroyedBricks.fold(extras.gifts) { acc, brick ->
        acc.spawnGift(brick)
    }.mapNotNull { gift -> gift.update(racket, area) }

    val caughtEffects = racket.collidedEffects(newGifts)
    val newEffects = extras.effects.update(caughtEffects, racket.stickyLeft)

    val newRacket = racket.update(
        EffectType.GLUE in caughtEffects,
        EffectType.GLUE !in newEffects,
        EffectType.EXTENDED in extras.effects, racketCollisions.size
    )


    return this.copy(balls = newBalls, racket = newRacket, bricks = newBricks,
        score = score + destroyedBricks.fold(0) { acc, brick ->
            acc + brick.type.score
        }
    )
}


fun Game.finishGame(): Game = this.copy(
    score = score + (0..extras.ballsLeft).sum(),
    extras = extras.copy(flags = extras.flags.copy(finished = true))
)


fun Game.moveRacket(me: MouseEvent): Game {
    val newRacket = racket.move(me, area)
    val newBalls = balls.map { ball ->
        if (ball.vel.y == 0)
            ball.moveStoppedBall(racket, newRacket)
        else
            ball
    }
    return this.copy(balls = newBalls, racket = newRacket)

}

fun Game.switchSound(): Game = this.copy(extras = extras.copy(flags = extras.flags.copy(sound = !extras.flags.sound)))

fun Game.switchLevels(): Game =
    this.copy(extras = extras.copy(flags = extras.flags.copy(levels = !extras.flags.levels)))

fun Game.switchGifts(): Game = this.copy(extras = extras.copy(flags = extras.flags.copy(gifts = !extras.flags.gifts)))


fun Game.playSound(sound: String) {
    if (extras.flags.sound)
        pt.isel.canvas.playSound(sound)
}

fun Game.moveStoppedBalls(): Game {
    val newBalls =
        if (balls.isEmpty())
            listOf(INIT_BALL)
        else
            balls.map { ball ->
                if (ball.vel.y == 0)
                    Ball(ball.pos, Velocity(ball.vel.x, BALL_Y_START_VELOCITY), ball.radius)
                else ball
            }


    return this.copy(
        balls = newBalls,
        extras = extras.copy(ballsLeft = if (balls.isEmpty()) extras.ballsLeft - 1 else extras.ballsLeft)
    )
}

fun Game.options(letter: Char): Game {
    return when (letter) {
        's' -> {
            println(if (extras.flags.sound) "Sound Off" else "Sound On")
            switchSound()
        }
        'L' -> {
            println(if (extras.flags.sound) "Levels Off" else "Levels On")
            switchLevels()
        }
        'G' -> {
            println(if (extras.flags.gifts) "Gifts Off" else "Gifts On")
            switchGifts()
        }
        else -> this
    }
}
