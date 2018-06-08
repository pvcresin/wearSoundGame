package com.pvcresin.wearsoundgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.concurrent.timer

class MySurfaceView : SurfaceView, SurfaceHolder.Callback {

    constructor(context: Context) : super(context) {
        holder.addCallback(this)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        holder.addCallback(this)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        animate = false
    }

    var soundNum = 12
    var animate = true
    var frameCount = 0
    var sectionDegree = 360f / soundNum
    internal var width = 400
    internal var height = 400
    var maxR = width / 2f
    var cx = width / 2f
    var cy = height / 2f
    var lineR = maxR / 2
    var buttonR = 60f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun color(color: Int) {
        paint.color = color
    }
    fun fill() {
        paint.style = Paint.Style.FILL
    }
    fun stroke() {
        paint.style = Paint.Style.STROKE
    }
    fun fillAndStroke() {
        paint.style = Paint.Style.FILL_AND_STROKE
    }
    fun strokeWidth(width: Float) {
        paint.strokeWidth = width
    }
    fun Canvas.drawNote(sectionNum: Int, r: Float) {
        val area = RectF(width/2 - r, height/2 - r, width/2 + r, height/2 + r)
        drawArc(area, -90f + sectionNum * sectionDegree, sectionDegree, false, paint)
    }

    var audioAttributes: AudioAttributes =
            AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_GAME).
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

    var soundPool: SoundPool =
            SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(soundNum)// ストリーム数に応じて
                    .build()

    var se = soundPool.load(context, R.raw.bell, 1)
    var sound0 = soundPool.load(context, R.raw.c4, 1)
    var sound1 = soundPool.load(context, R.raw.cs4, 1)
    var sound2 = soundPool.load(context, R.raw.d4, 1)
    var sound3 = soundPool.load(context, R.raw.ds4, 1)
    var sound4 = soundPool.load(context, R.raw.e4, 1)
    var sound5 = soundPool.load(context, R.raw.f4, 1)
    var sound6 = soundPool.load(context, R.raw.fs4, 1)
    var sound7 = soundPool.load(context, R.raw.g4, 1)
    var sound8 = soundPool.load(context, R.raw.gs4, 1)
    var sound9 = soundPool.load(context, R.raw.a4, 1)
    var sound10 = soundPool.load(context, R.raw.as4, 1)
    var sound11 = soundPool.load(context, R.raw.b4, 1)

    override fun surfaceCreated(holder: SurfaceHolder) {

        val notes = mutableListOf<Note>()

        for (i in 0..11) {
            notes.add(Note(i, maxR + i * 50))
        }

        timer(initialDelay = 1000, period = 10) {
            if (animate) {
                if (frameCount >= 1000) animate = false

                val canvas = holder.lockCanvas()

                canvas.drawColor(Color.BLACK)

                color(Color.RED)
                strokeWidth(1f)
                stroke()
                for (i in 0..soundNum) {
                    canvas.drawLine(cx, cy,
                            cx + (maxR * Math.cos(Math.toRadians(-90.0 + i * sectionDegree))).toFloat(),
                            cy + (maxR * Math.sin(Math.toRadians(-90.0 + i * sectionDegree))).toFloat(),
                            paint)
                }

                canvas.drawCircle(cx, cy, buttonR, paint)

                color(Color.WHITE)
                strokeWidth(5f)
                canvas.drawCircle(cx, cy, lineR, paint)

                paint.textSize = 35f
                fill()
                val text = "$frameCount"
                canvas.drawText(text, cx, cy, paint)

                notes.forEach {
                    it.draw(canvas)
                }

                frameCount++

                holder.unlockCanvasAndPost(canvas)
            }
        }

    }

    fun play(sectionNum: Int) {
        val sound = when(sectionNum) {
            -1 -> se
            0 -> sound0
            1 -> sound1
            2 -> sound2
            3 -> sound3
            4 -> sound4
            5 -> sound5
            6 -> sound6
            7 -> sound7
            8 -> sound8
            9 -> sound9
            10 -> sound10
            11 -> sound11
            else -> 100     // rest
        }
        if (sound != 100) {
            // play(ロードしたID, 左音量, 右音量, 優先度, ループ, 再生速度)
            soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1f)
        }
    }

    val offset = 0 // for delay

    inner class Note(val section: Int, var r: Float) {

        fun draw(canvas: Canvas) {
            if (maxR < r) r--
            else if (buttonR <= r && r <= maxR) {
                color(Color.YELLOW)
                strokeWidth(9f)
                stroke()
                canvas.drawNote(section, r)
                r--
                if (r == lineR + offset) play(section)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            soundPool.play(se, 1.0f, 1.0f, 0, 0, 1f)
        }
        return super.dispatchTouchEvent(event)
    }

}
