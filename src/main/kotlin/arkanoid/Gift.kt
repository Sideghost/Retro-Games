package arkanoid

import pt.isel.canvas.*

enum class EffectType(val color: Int) {
    EXTENDED(GREEN),
    BALLS(CYAN),
    SLOW(YELLOW),
    FAST(0xffa500),
    GLUE(BLUE),
    CANCEL(RED)
}

data class Gift(val pos: Position, val type: EffectType)

const val GIFT_WIDTH = BRICK_WIDTH / 2
const val GIFT_HEIGHT = BRICK_WIDTH / 2
const val GIFT_DOWN_VELOCITY = 2


fun Canvas.draw(gift: Gift) {
    drawRect(gift.pos.x, gift.pos.y, GIFT_WIDTH, GIFT_HEIGHT, gift.type.color)
    drawText(gift.pos.x + GIFT_WIDTH / 2 - 5, gift.pos.y + GIFT_HEIGHT / 2 + 5, gift.type.name[0].toString(), BLACK, 15)
}

fun List<EffectType>.update(caughtEffects: List<EffectType>, stickyLeft: Int): List<EffectType> {
    val newEffects = caughtEffects.filter { effect -> effect !in this }

    return if (EffectType.CANCEL in newEffects)
        emptyList()
    else this + newEffects - (if (EffectType.SLOW in this && EffectType.FAST in newEffects)
        EffectType.SLOW else EffectType.CANCEL) -
            (if (EffectType.FAST in this && EffectType.SLOW in newEffects)
                EffectType.FAST else EffectType.CANCEL) -
            (if (EffectType.BALLS in this) EffectType.BALLS else EffectType.CANCEL) -
            (if (EffectType.GLUE !in newEffects && stickyLeft <= 0) EffectType.GLUE else EffectType.CANCEL)
}


fun Gift.update(racket: Racket, area: Area): Gift? = if (!this.withinBounds(area) || racket.collides(this)) null
else this


fun Gift.withinBounds(area: Area): Boolean {
    return pos.y < area.height
}

fun List<Gift>.spawnGift(brick: Brick): List<Gift> = if (brick.effectType == null) this
else this + Gift(Position(brick.pos.x * BRICK_WIDTH, brick.pos.y * BRICK_HEIGHT), brick.effectType)

