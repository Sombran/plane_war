package cn.plane

/**
 * @author youbo
 * 2018/1/25
 */
class Hero: FlyObject() {
    /**
     * 火力值
     */
    private var fire = 1
    private val images = arrayOf(Game.hero0, Game.hero1)
    private var index = 0

    override var image = images[index]
    override var x = 0
    override var y = 0

    var life = 0

    init {
        init()
    }

    fun init() {
        life = 3
        x = Game.WIDTH / 2 - width / 2
        y = Game.HEIGHT - height - 50
    }

    override fun step() {
        if (images.isNotEmpty()) {
            image = images[index++ % images.size]
        }
    }

    fun moveTo(x: Int?, y: Int?) {
        x?.let { this.x = it - width / 2 }
        y?.let { this.y = it - height / 2 }
    }

    fun shoot(): Array<Bullet> {
        val xStep = width / 4
        val yStep = 20
        return when (fire) {
            2 -> arrayOf(Bullet(x + xStep, y - yStep), Bullet(x + 3 * xStep, y - yStep))
            3 -> arrayOf(Bullet(x + 2 * xStep, y - yStep), Bullet(x, y - yStep), Bullet(x + 4 * xStep, y - yStep))
            else -> arrayOf(Bullet(x + 2 * xStep, y - yStep))
        }
    }

    override fun outOfBounds() = false

    /**
     * 碰撞检测
     */
    fun bang(other: FlyObject): Boolean {
        // 获得other四个角坐标
        val x1 = other.x
        val y1 = other.y

        val x2 = other.x
        val y2 = other.y + other.height

        val x3 = other.x + other.width
        val y3 = other.y

        val x4 = other.x + other.width
        val y4 = other.y + other.height

        return pointIn(x1, y1) || pointIn(x2, y2) || pointIn(x3, y3) || pointIn(x4, y4)
    }

    /**
     * 某个点是否在英雄机范围内
     */
    private fun pointIn(x1: Int, y1: Int) = x1 >= x && x1 <= x + width && y1 >= y && y1 <= y + height

    fun subtractLife() {
        life--
    }

    fun addLife() {
        life++
    }

    fun addFire() {
        if (fire < 3) {
            fire++
        }
    }

    fun subtractFire() {
        if (fire > 1) {
            fire--
        }
    }
}