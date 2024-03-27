package arkanoid


/**
 * Represents a position in 2D space
 * @property x X Component of the position
 * @property y Y Component of the position
 */
data class Position(
	val x: Int,
	val y: Int
)

/**
 * Operator for adding a velocity to the position
 * @param vel Velocity to add
 * @receiver original position
 * @return new Position updated based on the given velocity
 */
operator fun Position.plus(vel: Velocity): Position {
	return Position(x + vel.x, y + vel.y)
}

/** Limits an integer to a certain range
 * @param range valid range
 * @receiver integer to be limited
 * @return limited integer
 */
fun Int.constrain(range: IntRange) = when {
	this < range.first -> range.first
	this > range.last -> range.last
	else -> this
}
