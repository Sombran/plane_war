package cn.plane

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Font.BOLD


/**
 * @author youbo
 * 2018/1/25
 */
class Game : JPanel() {

    companion object {
        private val DELAY: Long = 1000 / 100

        const val WIDTH = 400
        const val HEIGHT = 654

        val background = ImageIO.read(Game::class.java.getResource("/background.png"))!!
        val hero0 = ImageIO.read(Game::class.java.getResource("/hero0.png"))!!
        val hero1 = ImageIO.read(Game::class.java.getResource("/hero1.png"))!!
        val start = ImageIO.read(Game::class.java.getResource("/start.png"))!!
        val pause = ImageIO.read(Game::class.java.getResource("/pause.png"))!!
        val gameover = ImageIO.read(Game::class.java.getResource("/gameover.png"))!!
        val bullet = ImageIO.read(Game::class.java.getResource("/bullet.png"))!!
        val airplane = ImageIO.read(Game::class.java.getResource("/airplane.png"))!!
        val bee = ImageIO.read(Game::class.java.getResource("/bee.png"))!!
    }

    /**
     * 游戏英雄
     */
    private val hero = Hero()

    /**
     * 游戏状态
     */
    private var state = GameState.START

    /**
     * 子弹集合
     */
    private val bullets = mutableListOf<Bullet>()

    /**
     * 分数
     */
    private var score = 0

    /**
     * 飞行物
     */
    private val flyings = mutableListOf<FlyObject>()

    override fun getPreferredSize() = Dimension(Game.WIDTH, Game.HEIGHT)

    override fun paint(g: Graphics) {
        drawBackground(g)
        drawHero(g)
        drawBullets(g)
        drawFlyings(g)
        drawScore(g)
        drawState(g)
    }

    private fun drawBackground(g: Graphics) = g.drawImage(Game.background, 0, 0, null)

    private fun drawHero(g: Graphics) = g.drawImage(hero.image, hero.x, hero.y, null)

    private fun drawState(g: Graphics) {
        when (state) {
            GameState.START -> g.drawImage(Game.start, 0, 0, null)
            GameState.PAUSE -> g.drawImage(Game.pause, 0, 0, null)
            GameState.GAME_OVER -> g.drawImage(Game.gameover, 0, 0, null)
            else -> {
            }
        }
    }

    private fun drawScore(g: Graphics) {
        val x = 10
        var y = 25
        val font = Font(Font.SANS_SERIF, BOLD, 22)
        g.color = Color(0xFF0000)
        g.font = font
        g.drawString("SCORE:" + score, x, y)
        y += 20
        g.drawString("LIFE:" + hero.life, x, y)
    }

    private fun drawBullets(g: Graphics) = bullets.forEach { g.drawImage(it.image, it.x, it.y, null) }

    private fun drawFlyings(g: Graphics) = flyings.forEach { g.drawImage(it.image, it.x, it.y, null) }

    fun start() {
        // 与输入有关的更新
        updateWithInput()

        // 与输入无关的更新
        updateWithoutInput()
    }

    private fun updateWithInput() {
        val mouseAdapter = object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent?) {
                if (state == GameState.RUNNING) {
                    hero.moveTo(e?.x, e?.y)
                }
            }

            override fun mouseExited(e: MouseEvent?) {
                if (state == GameState.RUNNING) {
                    state = GameState.PAUSE
                }
            }

            override fun mouseEntered(e: MouseEvent?) {
                if (state == GameState.PAUSE) {
                    state = GameState.RUNNING
                }
            }

            override fun mouseClicked(e: MouseEvent?) {
                when (state) {
                    GameState.START -> {
                        state = GameState.RUNNING
                        hero.moveTo(e?.x, e?.y)
                    }
                    GameState.GAME_OVER -> {
                        state = GameState.START
                        score = 0
                        hero.init()
                        bullets.clear()
                        flyings.clear()
                    }
                    else -> {
                    }
                }
            }
        }

        this.addMouseListener(mouseAdapter)
        this.addMouseMotionListener(mouseAdapter)
    }

    private fun updateWithoutInput() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                // 游戏进行中才调用的操作
                if (state == GameState.RUNNING) {
                    newEnemy()
                    shootAction()
                    stepAction()
                    bangAction()
                    hitAction()
                    outOfBoundsAction()
                    checkGameoverAction()
                }

                // 重画
                repaint()
            }
        }, Game.DELAY, Game.DELAY)
    }

    private var shootIndex = 0
    private fun shootAction() {
        if (shootIndex++ % 30 == 0) {
            bullets.addAll(hero.shoot())
        }
    }

    /**
     * 移动
     */
    private fun stepAction() {
        hero.step()
        bullets.forEach(Bullet::step)
        flyings.forEach(FlyObject::step)
    }

    private var newEnemyIndex = 0
    private fun newEnemy() {
        if (newEnemyIndex++ % 40 == 0) {
            if ((Math.random() * 30).toInt() > 25) {
                flyings.add(Bee())
            } else {
                flyings.add(Airplane())
            }
        }
    }

    private fun outOfBoundsAction() {
        // 边界检查，越界的都删除。这里可以在foreach中删除元素是因为先filter了，foreach的不是原集合
        bullets.filter(Bullet::outOfBounds).forEach { bullets.remove(it) }
        flyings.filter(FlyObject::outOfBounds).forEach { flyings.remove(it) }
    }

    private fun checkGameoverAction() {
        if (hero.life < 0) {
            state = GameState.GAME_OVER
        }
    }

    /**
     * 子弹攻击
     */
    private fun hitAction() {
        val flyingIter = flyings.iterator()
        while (flyingIter.hasNext()) {
            val flying = flyingIter.next()
            val bulletIter = bullets.iterator()
            while (bulletIter.hasNext()) {
                val bullet = bulletIter.next()
                if (flying.shootBy(bullet)) {
                    // 飞行物被子弹打中
                    if (flying is Enemy) {
                        score += flying.getScore()
                    } else if (flying is Award) {
                        when (flying.getType()) {
                            AwardEnum.LIFE -> hero.addLife()
                            AwardEnum.FIRE -> hero.addFire()
                            AwardEnum.SCORE -> score += 50
                        }
                    }
                    flyingIter.remove()
                    bulletIter.remove()
                    break
                }
            }
        }
    }

    /**
     * 碰撞,撞到后敌机消失，火力降级，生命减少
     */
    private fun bangAction() {
        flyings.filter(hero::bang).forEach {
            hero.subtractLife()
            hero.subtractFire()
            flyings.remove(it)
        }
    }
}

fun main(args: Array<String>) {
    val jFrame = JFrame()
    val game = Game()
    jFrame.contentPane = game
    jFrame.pack()
    jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jFrame.isResizable = false
    jFrame.isVisible = true
    game.start()
}