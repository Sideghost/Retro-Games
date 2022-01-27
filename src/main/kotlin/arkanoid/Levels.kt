package arkanoid

const val MAP_WIDTH = 13
const val MAP_HEIGHT = 13

val levels = listOf(

    """xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       x777x090x777x
       x666x111x666x
       x555x222x555x
       x444x333x444x
       x333x444x333x
       x222x555x222x
       x111x666x111x
       x000x888x000x
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
    """,
    """xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       x777x090x777x
       x696x111x696x
       x595x222x595x
       x494x333x494x
       x393x949x393x
       x292x999x292x
       x191x666x191x
       x000x888x000x
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
    """,
    """xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
       x777x090x777x
       x696x111x696x
       x595x222x595x
       x494x333x494x
       x393x949x393x
       x292x999x292x
       x191x666x191x
       x999x999x999x
       xxxxxxxxxxxxx
       xxxxxxxxxxxxx
    """,
)


fun getLevel(level: Int): List<Brick> {
    return levels[level]
        .filter { char -> char != ' ' && char != '\n' }
        .mapIndexed { index, c ->
            val x = index % MAP_WIDTH
            val y = index / MAP_HEIGHT
            if (c != 'x') {
                Brick(Position(x, y), 0, BrickType.values()[c.code - '0'.code], EffectType.values().random())
            } else
                Brick(Position(-1, -1), 0, BrickType.GOLD, EffectType.values().random())
        }.filter { it.pos.x != -1 }
}