package com.game.starwars

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils


class StarWarsGame : ApplicationAdapter() {
    private var batch: SpriteBatch? = null

    private var camera: OrthographicCamera? = null
    private var pilotRect: Rectangle? = null
    private var pilotTexture: Texture? = null
    private var bulletTexture: Texture? = null
    private val bulletsArray = Array<Rectangle>()
    private var lastBulletTime: Long = 0

    private var laserSound: Sound? = null
    private var backgroundMusic: Music? = null

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()
        camera!!.setToOrtho(false, 800f, 480f)

        //Create a Rectangle to logically represent the pilot
        pilotRect = Rectangle()
        pilotRect!!.width = 256f
        pilotRect!!.height = 128f
        pilotRect!!.x = (800f / 2) - (256f / 2f)
        pilotRect!!.y = 20f

        pilotTexture = Texture(Gdx.files.internal("pilot.png"))
        bulletTexture = Texture(Gdx.files.internal("bullet.png"))

        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser.wav"))
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"))

        backgroundMusic!!.isLooping = true
        backgroundMusic!!.play()
    }

    override fun render() {
        ScreenUtils.clear(26 / 100f, 2 / 100f, 56 / 100f, 1f)
        batch!!.projectionMatrix = camera!!.combined
        batch!!.begin()
        batch!!.draw(pilotTexture, pilotRect!!.x, pilotRect!!.y)
        for (bullet in bulletsArray) {
            batch!!.draw(bulletTexture, bullet.x, bullet.y)
        }
        batch!!.end()

        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera!!.unproject(touchPos)
            pilotRect!!.x = touchPos.x
            pilotRect!!.y = touchPos.y
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) pilotRect!!.x -= 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) pilotRect!!.x += 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) pilotRect!!.y += 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) pilotRect!!.y -= 200 * Gdx.graphics.deltaTime


        if (TimeUtils.nanoTime() - lastBulletTime > 1000000000) {
            renderRandomBullet()
        }

        val iter: MutableIterator<Rectangle> = bulletsArray.iterator()
        while (iter.hasNext()) {
            val bullet = iter.next()
            bullet.x -= 200 * Gdx.graphics.deltaTime
            if (bullet.x < 0) iter.remove()
            if (bullet.overlaps(pilotRect)) {
                laserSound!!.play()
                iter.remove()
            }
        }
    }

    private fun renderRandomBullet() {
        val bullet = Rectangle()
        bullet.x = 400f
        bullet.y = MathUtils.random(0, 480).toFloat()
        bullet.width = 64f
        bullet.height = 64f
        lastBulletTime = TimeUtils.nanoTime()
        bulletsArray.add(bullet)
    }

    override fun dispose() {
        batch!!.dispose()
        bulletTexture!!.dispose()
        pilotTexture!!.dispose()
        laserSound!!.dispose()
        backgroundMusic!!.dispose()
    }
}