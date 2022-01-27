package arkanoid

import pt.isel.canvas.*
import kotlin.math.max

const val EXTENDED_RACKET_WIDTH = 90
const val RACKET_WIDTH = 60
const val RACKET_HEIGHT = 10

/** Outermost racket zone width */
const val RED_RACKET_ZONE_WIDTH = 10

/** Intermediate racket zone width */
const val PINK_RACKET_ZONE_WIDTH = 15
const val RACKET_ZONES_HEIGHT = 5

/** Light Pink Color representation */
const val LIGHT_PINK = 0xe07777

val INIT_RACKET = Racket(
    Position(WIDTH / 2 + RACKET_WIDTH / 2, HEIGHT - 50 - RACKET_HEIGHT),
    RACKET_WIDTH, 0
)

/**
 * Racket class containing the racket position
 * @property pos position of the racket/
 */
data class Racket(
    val pos: Position,
    val width: Int,
    val stickyLeft: Int
)

/**
 * Moves a [Racket] based on a [MouseEvent]
 *
 * @param me Mouse Event that contains the coordinates
 * @param area area containing width and height constraints
 * @receiver racket for context, does not serve any purpose in the function operation
 * @return updated racket based on [me] coordinates
 */
fun Racket.move(me: MouseEvent, area: Area): Racket {
    val x = when {
        me.x + width / 2 > area.width -> area.width - width / 2
        me.x - width / 2 < 0 -> width / 2
        else -> me.x
    }
    return Racket(Position(x, pos.y), width, stickyLeft)
}

fun Racket.update(addGlue: Boolean, turnOffGlue: Boolean, extended: Boolean, racketCollisions: Int): Racket {
    val newStickyLeft = if (turnOffGlue) 0 else if (addGlue) 3 else max(0, stickyLeft - racketCollisions)

    return if (extended) Racket(pos, EXTENDED_RACKET_WIDTH, newStickyLeft)
    else Racket(
        pos,
        RACKET_WIDTH,
        newStickyLeft
    )
}

fun Racket.collidedEffects(
    gifts: List<Gift>
) = gifts.fold(emptyList<EffectType>()) { acc, gift ->
    if (collides(gift))
        acc + gift.type
    else acc
}

/**
 * Draws a [Racket] onto a [Canvas]
 * @receiver [Canvas] to draw the [racket]
 * @param racket racket containing the position where the racket will be drawn
 */
fun Canvas.draw(racket: Racket) {
    drawRect(
        racket.pos.x - racket.width / 2,
        racket.pos.y - RACKET_HEIGHT / 2,
        racket.width,
        RACKET_HEIGHT,
        if (racket.stickyLeft > 0) CYAN else WHITE
    )

    drawRacketZone(racket, outerMost = true, left = true)
    drawRacketZone(racket, outerMost = false, left = true)
    drawRacketZone(racket, outerMost = false, left = false)
    drawRacketZone(racket, outerMost = true, left = false)

    if (racket.stickyLeft > 0)
        drawText(
            racket.pos.x, racket.pos.y + 5,
            racket.stickyLeft.toString(),
            BLACK, 12
        )
}

/**
 * Draws a racket zone onto the canvas
 * @receiver canvas to draw
 * @param racket racket containing the coordinates for drawing the zones
 * @param outerMost if true draws the outermost zone
 * @param left if true draws the left zone otherwise right zone
 */
fun Canvas.drawRacketZone(racket: Racket, outerMost: Boolean, left: Boolean) {
    //X position of the zone
    val x = when {
        outerMost && left ->
            racket.pos.x - racket.width / 2
        !outerMost && left ->
            racket.pos.x - racket.width / 2 + RED_RACKET_ZONE_WIDTH
        !outerMost && !left ->
            racket.pos.x + racket.width / 2 - RED_RACKET_ZONE_WIDTH - PINK_RACKET_ZONE_WIDTH
        outerMost && !left ->
            racket.pos.x + racket.width / 2 - RED_RACKET_ZONE_WIDTH
        else -> racket.pos.x - racket.width / 2 //Invalid case, will never happen
    }

    drawRect(
        x, racket.pos.y - RACKET_HEIGHT / 2,
        if (outerMost) RED_RACKET_ZONE_WIDTH else PINK_RACKET_ZONE_WIDTH,
        RACKET_ZONES_HEIGHT, if (outerMost) RED else LIGHT_PINK
    )
}

/**
 * Calculates the range of values in the center zone
 * @receiver racket with its respective coordinates
 * @return range of center zone
 */
fun Racket.centerRange(): IntRange {
    val centralZoneWidth = width - PINK_RACKET_ZONE_WIDTH * 2 - RED_RACKET_ZONE_WIDTH * 2
    return pos.x - centralZoneWidth / 2..pos.x + centralZoneWidth / 2
}

/**
 * Calculates the range of values in the right outermost zone
 * @receiver racket with its respective coordinates
 * @return range of right outermost zone
 */
fun Racket.rightOutermostRange() =
    pos.x + width / 2 - RED_RACKET_ZONE_WIDTH..pos.x + width / 2


/**
 * Calculates the range of values in the right intermediate zone
 * @receiver racket with its respective coordinates
 * @return range of right intermediate zone
 */
fun Racket.rightIntermediateRange() =
    pos.x + width / 2 - (PINK_RACKET_ZONE_WIDTH + RED_RACKET_ZONE_WIDTH)..pos.x + width / 2 - RED_RACKET_ZONE_WIDTH


/**
 * Calculates the range of values in the left intermediate zone
 * @receiver racket with its respective coordinates
 * @return range of left intermediate zone
 */
fun Racket.leftIntermediateRange() =
    pos.x - width / 2..pos.x - width / 2 + RED_RACKET_ZONE_WIDTH + PINK_RACKET_ZONE_WIDTH


/**
 * Calculates the range of values in the left outermost zone
 * @receiver racket with its respective coordinates
 * @return range of left outermost zone
 */
fun Racket.leftOutermostRange() =
    pos.x - width / 2..pos.x - width / 2 + RED_RACKET_ZONE_WIDTH


/**
 * Calculates the range of values in the racket height
 * @receiver racket with its respective coordinates
 * @return range of racket height
 */
fun Racket.heightRange() = pos.y - RACKET_HEIGHT / 2..pos.y + RACKET_HEIGHT / 2


fun Racket.widthRange() =
    pos.x - width / 2..pos.x + width / 2


/**
 * Calculates the bounce(Velocity) based on racket position
 * It will only bounce if it's within racket bounds
 * If it is within racket bounds, it will bounce
 * with the following X component offset -3 -1 0 1 3, which corresponds to the respective zones of the
 * racket, and it will reverse the Y component
 *
 * @receiver racket to bounce from
 * @param ball ball to bounce
 * @return ball velocity
 */
fun Racket.calculateBounce(ball: Ball): Velocity? {
    return if (ball.pos.y + ball.radius in heightRange())
        when (ball.pos.x) {
            in leftOutermostRange() ->
                Velocity((ball.vel.x - 3).constrain(VALID_VELOCITIES), ball.vel.y * -1)

            in leftIntermediateRange() ->
                Velocity((ball.vel.x - 1).constrain(VALID_VELOCITIES), ball.vel.y * -1)

            in rightIntermediateRange() ->
                Velocity((ball.vel.x + 1).constrain(VALID_VELOCITIES), ball.vel.y * -1)

            in rightOutermostRange() ->
                Velocity((ball.vel.x + 3).constrain(VALID_VELOCITIES), ball.vel.y * -1)

            in centerRange() ->
                Velocity(ball.vel.x, ball.vel.y * -1)

            //Outside racket case
            else -> null
        }
    else null
}

fun Racket.collides(gift: Gift): Boolean {
    return if (gift.pos.y + GIFT_HEIGHT in heightRange())
        gift.pos.x in widthRange()
    else false
}
