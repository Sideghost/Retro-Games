package snake

import pt.isel.canvas.Canvas
import pt.isel.canvas.GREEN
import pt.isel.canvas.WHITE
import pt.isel.canvas.YELLOW

// Menu Features
private const val CORRECTION = 10                                   // Correction pixels for Menu
private const val MENU_Y_ORIGIN = CELL_SIDE * 4                     // Menu y origin
private const val MENU_X_ORIGIN = CELL_SIDE * 3                     // Menu x origin
private const val MENU_HEIGHT = CELL_SIDE * 8                       // Menu Height
private const val MENU_WIDTH = CELL_SIDE * 14                       // Menu Width
private const val MENU_COLOR = 404040                               // Menu color
private const val MENU_FONT_SIZE = 20                               // Font size on Menu
private const val LOGO_SIDE = 150                                   // Logo size
private const val MENU_TEXT_X_ORIGIN = MENU_Y_ORIGIN - CORRECTION   // X origin of a text in a menu
private const val MENU_TEXT_Y_ORIGIN = 170                          // Y origin of a text in a menu
private const val TEXT_DIFFERENCE = 60                              // Text difference between two lines
private const val LOGO_Y_ORIGIN = 180                               // Logo y origin in menu
private const val LOGO_X_ORIGIN = CELL_SIDE * 11 - CORRECTION       // Logo x origin in menu

/**
 * Draws the whole game (Snake, Walls, Apple, Status)
 * @receiver where it will be drawn
 * @param game collection of the walls, apple and snake
 */
fun Canvas.drawGame(game: Game) {
	erase()
	if (game.hacking.grid) drawGrid()
	drawSnake(game.snake, "snakeResources\\snake.png")
	game.wall.forEach {
		drawBrick(it, "snakeResources\\bricks.png")
	}
	drawApple(game.apple, "snakeResources\\snake.png")
	drawStatus(game)
	drawHackedApple(
		game.hacking.hackedApple,
		if (game.hacking.level == 2) "snakeResources\\appleGolden.png" else "snakeResources\\applePoison.png"
	)
	if (game.hacking.menu) drawMenu()
}

/**
 * Draws the Snake in the canvas using a Sprite
 * @receiver where it will be drawn
 * @param snake object with movement in the game
 * @param pngFile input file that has the drawing of the snake
 */
private fun Canvas.drawSnake(snake: Snake, pngFile: String) {
	// Head
	drawHead(snake, pngFile)

	//Tail
	if (snake.body.size > 1)
		drawTail(snake, pngFile)
	//Body
	if (snake.body.size > 2)
		drawBody(snake, pngFile)
}

/**
 * Draws the Head of the Snake in the canvas using a Sprite
 * @receiver where it will be drawn
 * @param snake object with movement in the game
 * @param pngFile input file that has the drawing of the snake
 */
private fun Canvas.drawHead(snake: Snake, pngFile: String) {
	val bx = snake.body[0].x * CELL_SIDE - snake.direction.dx()
	val by = snake.body[0].y * CELL_SIDE - snake.direction.dy()
	val (hxImg, hyImg) = when (snake.direction) {
		Direction.LEFT -> 3 to 1
		Direction.RIGHT -> 4 to 0
		Direction.DOWN -> 4 to 1
		Direction.UP -> 3 to 0
	}
	drawImage(
		"$pngFile|${hxImg * SPRITE_DIV},${hyImg * SPRITE_DIV},$SPRITE_DIV,$SPRITE_DIV",
		bx, by, CELL_SIDE, CELL_SIDE
	)
}

/**
 * Draws the Tail of the Snake in the canvas using a Sprite
 * @receiver where it will be drawn
 * @param snake object with movement in the game
 * @param pngFile input file that has the drawing of the snake
 */
private fun Canvas.drawTail(snake: Snake, pngFile: String) {
	val position = if (snake.body.size > 1) snake.body[snake.body.size - 2] else snake.body.last()
	val next = (snake.body.last() - position).toDirection()

	val (txImg, tyImg) = when (next) {
		Direction.UP -> 4 to 3
		Direction.DOWN -> 3 to 2
		Direction.LEFT -> 4 to 2
		Direction.RIGHT -> 3 to 3
	}
	drawImage(
		"$pngFile|${txImg * SPRITE_DIV},${tyImg * SPRITE_DIV},$SPRITE_DIV,$SPRITE_DIV",
		snake.body.last().x * CELL_SIDE, snake.body.last().y * CELL_SIDE, CELL_SIDE, CELL_SIDE
	)
}

/**
 * Draws the Tail of the Snake in the canvas using a Sprite
 * @receiver where it will be drawn
 * @param snake object with movement in the game
 * @param pngFile input file that has the drawing of the snake
 */
private fun Canvas.drawBody(snake: Snake, pngFile: String) {
	snake.body.forEachIndexed { idx, body ->
		if (idx != 0 && idx != snake.body.size - 1) {

			val next = (body - snake.body[idx - 1]).toDirection()
			val previous = (snake.body[idx + 1] - body).toDirection()

			val (bxImg, byImg) = when {
				next.dx == -1 && previous.dy == -1 || next.dy == 1 && previous.dx == 1 -> 0 to 1 //LOWER_LEFT_CORNER
				next.dx == -1 && previous.dy == 1 || next.dy == -1 && previous.dx == 1 -> 0 to 0 //UPPER_LEFT_CORNER
				next.dy == 1 && previous.dx == -1 || next.dx == 1 && previous.dy == -1 -> 2 to 2 //LOWER_RIGHT_CORNER
				next.dy == -1 && previous.dx == -1 || next.dx == 1 && previous.dy == 1 -> 2 to 0 //UPPER_RIGHT_CORNER
				next.dy == 0 && previous.dy == 0 -> 1 to 0 //Horizontal
				next.dx == 0 && previous.dx == 0 -> 2 to 1 //Vertical
				else -> 0 to 2
			}
			drawImage(
				"$pngFile|${bxImg * SPRITE_DIV},${byImg * SPRITE_DIV},$SPRITE_DIV,$SPRITE_DIV",
				body.x * CELL_SIDE, body.y * CELL_SIDE, CELL_SIDE, CELL_SIDE
			)
		}
	}
}

/**
 * Draws a brick from the pngFile
 * @receiver where it will be drawn
 * @param position position of each brick
 * @param pngFile input file that has the drawing of the snake
 */
private fun Canvas.drawBrick(position: Position, pngFile: String) {
	drawImage(pngFile, position.x * CELL_SIDE, position.y * CELL_SIDE, CELL_SIDE, CELL_SIDE)
}

/**
 * Draws an apple from the pngFile
 * @receiver where it will be drawn
 * @param position to be drawn if possible
 * @param pngFile input file that has the drawing of the apple
 */
private fun Canvas.drawApple(position: Position?, pngFile: String) {
	if (position != null)
		drawImage(
			"$pngFile|${APPLE_X_FILE * SPRITE_DIV},${APPLE_Y_FILE * SPRITE_DIV},$SPRITE_DIV,$SPRITE_DIV",
			position.x * CELL_SIDE, position.y * CELL_SIDE, CELL_SIDE, CELL_SIDE
		)
}

/**
 * Draw the Game Menu
 * @receiver where it will be drawn
 */
private fun Canvas.drawMenu() {
	drawRect(MENU_X_ORIGIN, MENU_Y_ORIGIN, MENU_WIDTH, MENU_HEIGHT, MENU_COLOR)
	drawText(MENU_TEXT_X_ORIGIN, MENU_TEXT_Y_ORIGIN, "G - Activate Grid", WHITE, MENU_FONT_SIZE)
	drawText(MENU_TEXT_X_ORIGIN, MENU_TEXT_Y_ORIGIN + TEXT_DIFFERENCE, "N - Next Level", WHITE, MENU_FONT_SIZE)
	drawText(MENU_TEXT_X_ORIGIN, MENU_TEXT_Y_ORIGIN + 2 * TEXT_DIFFERENCE, "S - Activate Sound", WHITE, MENU_FONT_SIZE)
	drawText(MENU_TEXT_X_ORIGIN, MENU_TEXT_Y_ORIGIN + 3 * TEXT_DIFFERENCE, "J - Jump Level", WHITE, MENU_FONT_SIZE)
	drawImage("snakeResources\\logo.png", LOGO_X_ORIGIN, LOGO_Y_ORIGIN, LOGO_SIDE, LOGO_SIDE)
}

/**
 * Draw the background grid
 * @receiver where it will be drawn
 */
private fun Canvas.drawGrid() {
	(CELL_SIDE..height step CELL_SIDE).forEach {
		drawLine(0, it, width, it, WHITE, 1) // horizontal
	}
	(CELL_SIDE..width step CELL_SIDE).forEach {
		drawLine(it, 0, it, height, WHITE, 1) // vertical
	}
}

/**
 * Draws a hacked apple from the pngFile
 * @receiver where it will be drawn
 * @param position to be drawn if possible
 * @param pngFile input file that has the drawing of the apple
 */
private fun Canvas.drawHackedApple(position: Position?, pngFile: String) {
	if (position != null)
		drawImage(pngFile, position.x * CELL_SIDE, position.y * CELL_SIDE, CELL_SIDE, CELL_SIDE)
}

/**
 * Displays game information in the status bar
 * @receiver where it will be drawn
 * @param game information to be written
 */
private fun Canvas.drawStatus(game: Game) {
	val impPos = game.snake.body + game.wall
	val snakeLocked = game.snake.isLocked(impPos)

	drawRect(0, height - STATUS_BAR, width, STATUS_BAR, 0x333333)
	if (game.snake.body.size < Level_WIN)
		drawText(CELL_SIDE / 2, height - TEXT_BASE, "Size:${(game.snake.body.size)}", WHITE, FONT_SIZE)
	else
		drawText(CELL_SIDE / 2, height - TEXT_BASE, "Size:${(game.snake.body.size)}", GREEN, FONT_SIZE)
	drawText(FIVE_CELLS, height - TEXT_BASE, "Score:${game.score}", WHITE, FONT_SIZE)

	if (game.status == Status.WIN)
		drawText(
			width - FIVE_CELLS, height - TEXT_BASE,
			"You Win", YELLOW
		)
	if (game.status == Status.LOSE)
		drawText(
			width - FIVE_CELLS, height - TEXT_BASE,
			"You Lose", YELLOW
		)
	if (game.status == Status.PAUSE) when {
		(game.snake.body.size >= Level_WIN && snakeLocked) -> drawText(
			width - FIVE_CELLS, height - TEXT_BASE,
			"You Win", YELLOW
		)

		(game.snake.body.size <= Level_WIN && snakeLocked) -> drawText(
			width - FIVE_CELLS, height - TEXT_BASE,
			"You Lose", YELLOW
		)

		else -> drawText(
			width - SIX_CELLS, height - TEXT_BASE,
			"Paused", YELLOW
		)
	}
}
