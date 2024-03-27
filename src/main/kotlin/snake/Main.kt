package snake

import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas
import pt.isel.canvas.loadSounds
import pt.isel.canvas.onFinish
import pt.isel.canvas.onStart

/**
 * Game entry point
 */
fun main() {
	onStart {
		val cv = Canvas(CELL_SIDE * GRID_WIDTH, CELL_SIDE * GRID_HEIGHT + STATUS_BAR, BLACK)
		var game = createGame()

		cv.drawGame(game)
		loadSounds(
			"snakeResources\\eat.wav", "snakeResources\\Win.wav", "snakeResources\\Defeat.wav",
			"snakeResources\\poison_eat.wav", "snakeResources\\Victory.wav"
		)

		cv.onKeyPressed {
			game = if (!game.hacking.menu) game.copy(snake = snakeDirection(it.code, game))
			else game
			game = options(it.code, game)
			//game = game.snakeInput(it.code)
			//println(game.hacking.menu)
		}

		cv.onTimeProgress(BLOCK_SPAWN_TIMER) {
			game = game.createBrick()
		}

		cv.onTimeProgress(QUART_OF_A_SEC) {
			game = game.step()
			cv.drawGame(game)
		}
	}
	onFinish {
		println("FINALLY DONE!!! MINHA NOSSA...")
	}
}
