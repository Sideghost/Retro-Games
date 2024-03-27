package snake

/**
 * Class that defines all the important proprieties of a snake.Snake
 * @property body List of positions(x,y) of the Snake
 * @property direction Direction that the snake faces in the game
 * @property toGrow number of pieces that the snake has to grow
 */
data class Snake(val body: List<Position>, val direction: Direction, val toGrow: Int)

/**
 * Moves the Snake
 * @receiver Game to move
 * @return updated Game pass by snake
 */
fun Game.move(): Game {
	val headToPosition = listOf((snake.body[0] + snake.direction).normalize())
	val newSnake = if (snake.body.size < INIT_SIZE || snake.toGrow > 0) snake.body else snake.body.dropLast(1)
	return if (hasCollision(headToPosition[0], wall, snake.body)) this
	else copy(
		snake = snake.copy(
			body = headToPosition + newSnake,
			toGrow = if (snake.toGrow > 0) snake.toGrow - 1 else snake.toGrow
		)
	)
}
