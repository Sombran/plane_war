package cn.plane

import java.awt.image.BufferedImage

/**
 * @author youbo
 * 2018/1/26
 */
abstract class FlyObject {
    abstract var x: Int
    abstract var y: Int
    abstract val image: BufferedImage
    val width
        get() = image.width
    val height
        get() = image.height

    abstract fun step()

    abstract fun outOfBounds(): Boolean

    fun shootBy(bullet: Bullet): Boolean {
        val x = bullet.x
        val y = bullet.y
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height
    }
}