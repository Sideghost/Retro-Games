package snake

/**
 * Function that takes a random position inside all unused position.
 * @param game current state of the snake.Game to be affected by possible changes.
 * @return a snake.snake.Position to be given to a Brick.
 */
fun createRandomBrick(game: Game): Position = when (game.hacking.level) {
    1 -> if (game.apple == null)
        (ALL_POSITIONS - game.snake.body - game.wall).random()
    else
        (ALL_POSITIONS - game.snake.body - game.apple - game.wall).random()

    2 -> game.checkPosition(game.hacking.hackedApple)
    3 -> game.checkPosition(game.hacking.hackedApple)
    else -> game.wall[0] // smell code
}


/**
 * Function that creates a new brick in Canvas
 * @receiver snake.Game for create a new brick if the game is still running.
 * @return a new altered game or the same state of the game depending on GAME_STATUS.
 */
fun Game.createBrick() =
    if (status == Status.RUN) copy(wall = wall + createRandomBrick(this))
    else this


/**
 * Function that check is a brick call be placed on an empty position.
 * @receiver snake.Game Positions.
 * @param extraApple Hacked apple to check.
 * @return a valid position for a wall to spawn.
 */
private fun Game.checkPosition(extraApple: Position?) = when {
    apple != null && extraApple != null ->
        (ALL_POSITIONS - snake.body - wall - apple - extraApple).random()
    apple != null && extraApple == null ->
        (ALL_POSITIONS - snake.body - wall - apple).random()
    apple == null && extraApple != null ->
        (ALL_POSITIONS - snake.body - wall - extraApple).random()
    else -> (ALL_POSITIONS - snake.body - wall).random()
}


/**
 * Function that draws the initial walls.
 * @return List of the initial walls.
 */
fun initBlocks(): List<Position> {
    val topNDownRow = (0 until GRID_WIDTH).filter { it !in 3..16 }
    val sideRow = (1 until GRID_HEIGHT - 1).filter { it !in 4..11 }
    return topNDownRow.map { Position(it, 0) } + topNDownRow.map {
        Position(it, GRID_HEIGHT - 1)
    } + sideRow.map { Position(0, it) } + sideRow.map { Position(GRID_WIDTH - 1, it) }
}


/**
 * Function that draws the initial walls in level 2.
 * @return List of the initial walls.
 */
fun initBlocksTwo(): List<Position> {
    val topNDownRow = (0 until GRID_WIDTH).filter { it !in 2..8 && it !in 11..17}
    val sideRow = (1 until GRID_HEIGHT -1).filter { it !in 2..13 }
    val line2 = (0 until GRID_WIDTH).filter { it in 14..16 }
    val collum16 = (0 until GRID_HEIGHT - 1).filter { it in 2..4 }
    val collum3 = (0 until GRID_HEIGHT - 1).filter { it in 11..13 }
    val line13 = (0 until GRID_WIDTH).filter { it in 3..5 }
    val line4N5 = (0 until GRID_WIDTH).filter { it in 4..7 }
    val line10N11 = (0 until GRID_WIDTH).filter { it in 12..15 }
    val line3N6 = (0 until GRID_WIDTH).filter { it in 5..6 }
    val line9N12 = (0 until GRID_WIDTH).filter { it in 13..14 }
    return topNDownRow.map { Position(it, 0) } + topNDownRow.map {
        Position(it, GRID_HEIGHT - 1)
    } + sideRow.map { Position(0, it) } + sideRow.map {
        Position(GRID_WIDTH - 1, it)
    } + line2.map { Position(it, 2) } + collum16.map { Position(16, it) } + collum3.map {
        Position(3, it)
    } + line13.map { Position(it, 13) } + line4N5.map { Position(it, 4) } + line4N5.map {
        Position(it, 5)
    } + line10N11.map { Position(it, 10) } + line10N11.map { Position(it, 11) } + line3N6.map {
        Position(it, 3)
    } + line3N6.map { Position(it, 6) } + line9N12.map { Position(it, 9) } + line9N12.map { Position(it, 12) }
}


/**
 * Function that draws the initial walls in level 3.
 * @return List of the initial walls.
 */
fun initBlocksThree() :List<Position> {
    val topNDownRow = (0 until GRID_WIDTH).filter { it !in 3..7 && it !in 12..16}
    val sideRow = (0 until GRID_HEIGHT - 1).filter { it !in 3..6 && it !in 9..12}
    val line3N4N11N12 = (3 until GRID_WIDTH - 3).filter { it !in 5..14 }
    val line7N8 = (8 until GRID_WIDTH - 9).filter { it !in 8..11 }
    val collum9N10 = (4 until GRID_HEIGHT - 4).filter { it !in 6..9 }
    return topNDownRow.map { Position(it, 0) } + topNDownRow.map {
        Position(it, GRID_HEIGHT - 1)
    } + sideRow.map { Position(0, it) } + sideRow.map {
        Position(GRID_WIDTH - 1, it)
    } + line3N4N11N12.map { Position(it, 3) } + line3N4N11N12.map {
        Position(it, 4)
    } + line3N4N11N12.map { Position(it, 11) } + line3N4N11N12.map { Position(it, 12) } + line7N8.map {
        Position(it, 7)
    } + line7N8.map { Position(it, 8) } + collum9N10.map { Position(9, it) } + collum9N10.map { Position(10, it) }
}

