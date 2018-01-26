package cn.plane

import java.util.*

/**
 * @author youbo
 * 2018/1/26
 */
class Bee: FlyObject(), Award {
    private var xSpeed = 3
    private val ySpeed = 2

    override val image = Game.bee
    override var x = (Math.random() * (Game.WIDTH - width)).toInt()
    override var y = -height

    override fun step() {
        x += xSpeed
        y += ySpeed

        if (x + width > Game.WIDTH || x < 0) {
            xSpeed = -xSpeed
        }
    }

    override fun outOfBounds() = y > Game.HEIGHT

    override fun getType(): AwardEnum {
        val chance = Random().nextInt(100)
        return when (chance) {
            in 50..80 -> AwardEnum.FIRE
            in 81..100 -> AwardEnum.LIFE
            else -> AwardEnum.SCORE
        }
    }
}