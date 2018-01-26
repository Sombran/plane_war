package cn.plane

/**
 * @author youbo
 * 2018/1/26
 */
class Airplane: FlyObject(), Enemy {
    override val image = Game.airplane
    override var x = (Math.random() * (Game.WIDTH - width)).toInt()
    override var y = -height

    private val speed = 3

    override fun step() {
        this.y += speed
    }

    override fun getScore() = 10

    override fun outOfBounds() = y > Game.HEIGHT
}