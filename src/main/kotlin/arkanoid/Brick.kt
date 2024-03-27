package arkanoid

import kotlin.math.abs
import kotlin.math.sqrt
import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas

const val BRICK_WIDTH = 32
const val BRICK_HEIGHT = 15
const val BORDER = 1

enum class BrickType(val color: Int, val score: Int) {
	WHITE(pt.isel.canvas.WHITE, 1),
	ORANGE(0xffa500, 2),
	CYAN(pt.isel.canvas.CYAN, 3),
	GREEN(pt.isel.canvas.GREEN, 4),
	RED(pt.isel.canvas.RED, 6),
	BLUE(pt.isel.canvas.BLUE, 7),
	MAGENTA(pt.isel.canvas.MAGENTA, 8),
	YELLOW(pt.isel.canvas.YELLOW, 9),
	SILVER(0xc0c0c0, 0),
	GOLD(0xffd700, 0)
}

data class Brick(val pos: Position, val numberHits: Int, val type: BrickType, val effectType: EffectType?)

data class BrickCollision(val brick: Brick, val numHits: Int)

fun Canvas.draw(brick: Brick, giftsActivated: Boolean) {
	val x = brick.pos.x * BRICK_WIDTH
	val y = brick.pos.y * BRICK_HEIGHT
	drawRect(x, y, BRICK_WIDTH, BRICK_HEIGHT, brick.type.color)
	drawRect(
		x + BORDER / 2, y + BORDER / 2, BRICK_WIDTH - BORDER, BRICK_HEIGHT - BORDER,
		BLACK,
		BORDER
	)
	if (brick.type != BrickType.GOLD && brick.effectType != null && giftsActivated)
		drawText(x + BRICK_WIDTH / 2 - 5, y + BRICK_HEIGHT / 2 + 5, "G", BLACK, 10)
}

fun Brick.calculateBounce(ball: Ball): Velocity? {
	val x = pos.x * BRICK_WIDTH
	val y = pos.y * BRICK_HEIGHT

	//Selecting the closest point from the rectangle to the center of the ball
	val closestX = if (ball.pos.x < x) x else if (ball.pos.x > x + BRICK_WIDTH) x + BRICK_WIDTH else ball.pos.x
	val closestY = if (ball.pos.y < y) y else if (ball.pos.y > y + BRICK_HEIGHT) y + BRICK_HEIGHT else ball.pos.y

	//Distance between selected point in rectangle and the center of the ball using Pythagorean Theorem
	val distX = abs(ball.pos.x - closestX)
	val distY = abs(ball.pos.y - closestY)
	val distance = sqrt(((distX * distX) + (distY * distY)).toDouble())

	return when {
		distance <= BALL_RADIUS -> //If the distance is less than the ball radius than it has collided
//            when {
//                distX > distY -> Velocity(1, -1)
//                distX == distY -> Velocity(-1, -1)
//                else -> Velocity(-1, 1)
//            }
			Velocity(1, -1)

		else -> null
	}
}

fun List<Brick>.calculateCollision(ball: Ball): Pair<Brick?, Velocity?> {
	forEach { brick ->
		val brickBounceVelocity = brick.calculateBounce(ball)
		if (brickBounceVelocity != null) {
			return Pair(brick, brickBounceVelocity)
		}
	}
	return Pair(null, null)
}

fun List<Brick>.update(touchedBricks: List<BrickCollision>): Pair<List<Brick>, List<Brick>> {
	var destroyedBricks = emptyList<Brick>()
	val newBricks = touchedBricks.fold(this) { acc, touchedBrick ->
		val (brick, num_hits) = touchedBrick
		acc.mapNotNull {
			if (it == brick)
				when {
					brick.type == BrickType.GOLD -> it
					brick.type == BrickType.SILVER && brick.numberHits + num_hits < 2 ->
						Brick(it.pos, brick.numberHits + num_hits, it.type, it.effectType)

					else -> {
						destroyedBricks = destroyedBricks + brick
						null
					}
				}
			else it
		}
	}

	return Pair(newBricks, destroyedBricks)
}
