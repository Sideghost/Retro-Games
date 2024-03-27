package snake

import pt.isel.canvas.DOWN_CODE
import pt.isel.canvas.LEFT_CODE
import pt.isel.canvas.RIGHT_CODE
import pt.isel.canvas.UP_CODE

/**
 * Class that defines all available directions (UP, DOWN, LEFT, RIGHT)
 * @property dx component in the x-axis
 * @property dy component in the y-axis
 */
enum class Direction(val dx: Int, val dy: Int) {
	LEFT(-1, 0),
	UP(0, -1),
	RIGHT(+1, 0),
	DOWN(0, +1)
}

/**
 * Gets the x component of a direction
 */
fun Direction.dx() = this.dx

/**
 * Gets the y component of a direction
 */
fun Direction.dy() = this.dy

/**
 * Function that receives the input arrows and turns into a direction
 * @param key input arrow
 * @param snake object of movement
 * @return translated arrow into a direction
 */
fun directionOf(key: Int, snake: Snake): Direction = when (key) {
	LEFT_CODE -> Direction.LEFT
	RIGHT_CODE -> Direction.RIGHT
	UP_CODE -> Direction.UP
	DOWN_CODE -> Direction.DOWN
	else -> snake.direction
}

/**
 * Changes the game in the face of a new key
 * @param key code of arrow input
 * @param game current state of the snake.Game to be affected by possible changes
 * @return Snake with a new valid position or the old position
 */
fun snakeDirection(key: Int, game: Game): Snake {
	val headToPosition = game.snake.headToPosition(key).normalize()

	return if (game.wall.any { it == headToPosition } || headToPosition in game.snake.body) game.snake
	else game.snake.copy(direction = directionOf(key, game.snake))
}
