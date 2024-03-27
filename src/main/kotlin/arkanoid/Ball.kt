package arkanoid

import kotlin.math.abs
import pt.isel.canvas.CYAN
import pt.isel.canvas.Canvas
import pt.isel.canvas.playSound

const val BALL_RADIUS = 7

val INIT_BALL = Ball(
	Position(INIT_RACKET.pos.x, INIT_RACKET.pos.y - BALL_RADIUS - RACKET_HEIGHT / 2),
	Velocity(0, 0),
	BALL_RADIUS
)

/**
 * Range of valid velocities for X Component
 */
val VALID_VELOCITIES = -6..6
const val NORMAL_VELOCITY = 4
const val SLOW_VELOCITY = 3
const val FAST_VELOCITY = 5
const val BALL_Y_START_VELOCITY = -NORMAL_VELOCITY

/**
 * A ball containing a certain velocity
 */
data class Ball(
	val pos: Position,
	val vel: Velocity,
	val radius: Int,
)

/**
 * Makes the ball move, also bounces the ball from the racket and walls
 *
 * @receiver Ball to be moved
 * @param racket racket to be bounced from
 * @param area area containing width and height constraints
 * @return moved ball
 */
fun Ball.update(
	racket: Racket,
	brickVelocity: Velocity?,
	racketBounce: Velocity?,
	area: Area,
	soundActivated: Boolean,
	fast: Boolean?
): Ball {
	val velSign = if (vel.y != 0) vel.y / abs(vel.y) else 0
	val newBall = when (fast) {
		null -> Ball(pos, Velocity(vel.x, velSign * NORMAL_VELOCITY), radius)
		true -> Ball(
			pos, Velocity(vel.x, velSign * FAST_VELOCITY),
			radius
		)

		else -> Ball(
			pos,
			Velocity(vel.x, velSign * SLOW_VELOCITY),
			radius
		)
	}
	return newBall.collide(racket, brickVelocity, racketBounce, area, soundActivated)
}

fun Ball.collide(
	racket: Racket,
	brickVelocity: Velocity?,
	racketBounce: Velocity?,
	area: Area,
	soundActivated: Boolean
): Ball = when {
	//Ball touching left or right wall case
	pos.x !in radius..area.width - radius -> {
		if (soundActivated)
			playSound("arkanoidResources\\hitWallOrBrick")
		Ball(Position(pos.x - vel.x, pos.y), Velocity(vel.x * -1, vel.y), radius)
	}
	//Ball touching the upper wall case
	pos.y < radius -> {
		if (soundActivated)
			playSound("arkanoidResources\\hitWallOrBrick")
		Ball(Position(pos.x, pos.y - vel.y), Velocity(vel.x, vel.y * -1), radius)
	}

	//Ball collided with brick
	brickVelocity != null -> {
		val newPos = when {
			brickVelocity.x == -1 && brickVelocity.y == -1 -> pos + (vel * brickVelocity)
			brickVelocity.x == -1 -> Position(pos.x - vel.x, pos.y) //Collided with left or right side
			brickVelocity.y == -1 -> Position(pos.x, pos.y - vel.y) //Collided with top or bottom side
			else -> Position(pos.x, pos.y) //Never occurs, needed due to compilation
		}
//        val newPos = pos + (vel * brickVelocity)
		Ball(newPos, vel * brickVelocity, radius)
	}

	//Ball moving Downwards
	vel.y > 0 -> {
		val vel = racketBounce ?: vel
		val velGluey = if (racketBounce != null && racket.stickyLeft > 0)
			Velocity(0, 0)
		else vel

		Ball(pos + velGluey, velGluey, radius)
	}

	//Ball moving normally inside arena or colliding with
	else -> Ball(pos + vel, vel, radius)
}

/**
 * Draws the ball onto a canvas
 *
 * @receiver canvas to draw
 * @param ball to be drawn
 */
fun Canvas.draw(ball: Ball) {
	drawCircle(ball.pos.x, ball.pos.y, ball.radius, CYAN)
}

/**
 * Generates a new ball at the center of the arena
 *
 * @param area area containing width and height constraints
 * @return generated ball
 */
fun newBall(area: Area) = Ball(
	Position(((0 + BALL_RADIUS)..(area.width - BALL_RADIUS)).random(), area.height - BALL_RADIUS),
	Velocity(
		0, BALL_Y_START_VELOCITY
	),
	BALL_RADIUS
)

/**
 * Checks if a ball is within certain
 *
 * @receiver ball to be checked
 * @param area area containing width and height constraints
 * @return true, if it balls bounds otherwise false
 */
fun Ball.withinBallBounds(area: Area): Boolean {
	return pos.y < area.height
}

fun List<Ball>.update(
	bricks: List<Brick>,
	racket: Racket,
	area: Area,
	flags: Flags,
	effects: List<EffectType>
): Pair<List<Ball>, Collisions> {

	var brickCollisions = emptyList<BrickCollision>()
	var racketCollisions = emptyList<Velocity>()

	var newBalls = this.filter { ball -> ball.withinBallBounds(area) }.map { ball ->
		val (collidedBrick, brickBounceVelocity) = bricks.calculateCollision(ball)
		val racketBounce = racket.calculateBounce(ball)

		if (racketBounce != null && ball.vel.y != 0 && flags.sound) {
			playSound("arkanoidResources\\hitRacket")
			racketCollisions = racketCollisions + racketBounce
		}

		if (collidedBrick != null) {
			if (flags.sound)
				playSound("arkanoidResources\\hitWallOrBrick")
			val alreadyCollided = brickCollisions.any { it.brick == collidedBrick }

			brickCollisions = if (alreadyCollided) brickCollisions.map { touchedBrick ->
				if (touchedBrick.brick == collidedBrick) BrickCollision(
					touchedBrick.brick,
					touchedBrick.numHits + 1
				) else touchedBrick
			} else brickCollisions + BrickCollision(collidedBrick, 1)
		}

		ball.update(
			racket, brickBounceVelocity, racketBounce, area, flags.sound,
			if (EffectType.FAST in effects) true else if (EffectType.SLOW in effects) false else null
		)
	}

	newBalls = if (EffectType.BALLS in effects) newBalls.indices.fold(newBalls) { acc, _ ->
		acc + newBall(area)
	} else newBalls

	return Pair(newBalls, Collisions(brickCollisions, racketCollisions))
}

fun Ball.moveStoppedBall(racket: Racket, newRacket: Racket): Ball = Ball(
	Position(pos.x - (racket.pos.x - newRacket.pos.x), racket.pos.y - BALL_RADIUS - RACKET_HEIGHT / 2),
	vel, radius
)
