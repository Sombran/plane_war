package cn.plane

/**
 * @author youbo
 * 2018/1/26
 */
class Bullet(override var x: Int, override var y: Int): FlyObject() {
    private val speed = 3
    override val image = Game.bullet

    override fun step() {
        this.y -= speed
    }

    override fun outOfBounds() = (y + width) < 0
}