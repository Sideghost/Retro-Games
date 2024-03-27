package arkanoid

import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas
import pt.isel.canvas.loadSounds
import pt.isel.canvas.onFinish
import pt.isel.canvas.onStart

// Update delay in milliseconds
const val UPDATE_DELAY = 10

/**
 * width and height of the area,
 * we also made sure the other files are not dependent on this one,
 * therefore, we pass an Area object instead of accessing these variables
 * directly from this file
 */
const val WIDTH = BRICK_WIDTH * 13
const val HEIGHT = 600

/**
 * Starting point for the game
 */
fun main() {
	onStart {
		loadSounds(
			"arkanoidResources\\hitWallOrBrick", "arkanoidResources\\hitRacket",
			"arkanoidResources\\gameOver"
		)
		val arena = Canvas(WIDTH, HEIGHT, BLACK) //Spawns a canvas
		val area = Area(arena.width, arena.height)

		//Starts a game with no balls and a racket
		var game = Game(area, listOf(INIT_BALL), INIT_RACKET, getLevel(0), 0, Extras(flags = Flags()))

		//Updates the game every 10 milliseconds
		arena.onTimeProgress(UPDATE_DELAY) {
			game = game.update() //Moves the balls based on a certain delay
			arena.render(game) //renders the game onto the canvas
		}

		arena.onMouseMove { me ->
			//Moves the racket based on mouse coordinates
			if (!game.extras.flags.finished)
				game = game.moveRacket(me)
		}

		arena.onMouseDown {
			game = game.moveStoppedBalls()
		}

		arena.onKeyPressed { ke ->
			game = game.options(ke.char)

		}

	}
	onFinish {
		println("Adios")
	}
}




