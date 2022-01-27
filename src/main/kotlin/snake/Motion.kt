package snake

/**
 * Class that defines the displacement between 2 positions.
 * @property dx displacement value on the x axis.
 * @property dy displacement value on the y axis.
 */
data class Motion(val dx:Int, val dy:Int)


/**
 * Calculates the difference between two positions.
 * @receiver Subtraction left argument.
 * @param position Subtraction right argument.
 * @return teh difference between positions.
 */
operator fun Position.minus(position: Position) = Motion(x - position.x,y - position.y)


/**
 * Function that checks the snake.Snake.sign of a number.
 * @param value number to check.
 * @return the snake.Snake.sign of that number.
 */
private fun sign(value :Int) = when {
    value == 0 -> 0
    value == -1 -> -1
    value < -1 -> +1
    value > +1 -> -1
    else -> +1
}


/**
 * Function transforms the motion in a direction.
 * @receiver snake.Snake.Motion to transform
 * @return a direction resulting from a movement.
 */
fun Motion.toDirection() : Direction {
    val dxu = sign(dx)
    val dyu = sign(dy)
    return Direction.values().first { it.dx==dxu && it.dy==dyu }
}
