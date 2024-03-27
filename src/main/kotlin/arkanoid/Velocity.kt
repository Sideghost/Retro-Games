package arkanoid

/**
 * Represents a velocity in 2D Space
 * @property x X component of the velocity
 * @property y Y component of the velocity
 */
data class Velocity(
	val x: Int,
	val y: Int
)

/**
 * Operator for mul a velocity to the position
 * @param vel Velocity to add
 * @receiver original position
 * @return new Position updated based on the given velocity
 */
operator fun Velocity.times(vel: Velocity): Velocity {
	return Velocity(x * vel.x, y * vel.y)
}

