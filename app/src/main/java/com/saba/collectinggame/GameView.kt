package com.saba.collectinggame

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint()
    private val originalBucketBitmap = BitmapFactory.decodeResource(resources, R.drawable.bucket)
    private val iceCreamBitmaps = listOf(
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream1),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream2),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream3),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream4),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream5),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream6),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream7),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream8),
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream9)
    )
    private var bucketBitmap: Bitmap
    private var bucketX = 0f
    private var bucketY = 0f
    private val iceCreams = mutableListOf<IceCream>()
    private val random = Random()
    private var lastDropTime = System.currentTimeMillis()
    private var lastUpdateTime = System.currentTimeMillis()
    private var lastIntervalUpdateTime = System.currentTimeMillis()

    // Scaling factors for bucket and ice creams
    private val bucketScaleFactor = 0.4f
    private val iceCreamScaleFactor = 0.4f

    // Variables for score and missed ice creams
    private var score = 0
    private var missed = 0

    // Initial speed and speed increment
    private val initialSpeed = 10f
    private var currentSpeed = initialSpeed

    // Variables for spawn interval and minimum spawn interval
    private var spawnInterval = 500L // Initial spawn interval in milliseconds
    private val minSpawnInterval = 200L // Minimum spawn interval in milliseconds

    // SharedPreferences for high score
    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private var highScore = prefs.getInt("high_score", 0)

    // Variables for new record message
    private var newRecordFlag = false // Flag to indicate if new record is set
    private var showNewRecordMessage = false // Flag to show new record message
    private var newRecordShown = false // Ensure the message is shown only once

    init {
        val displayMetrics = context.resources.displayMetrics
        bucketBitmap = Bitmap.createScaledBitmap(
            originalBucketBitmap,
            (originalBucketBitmap.width * bucketScaleFactor).toInt(),
            (originalBucketBitmap.height * bucketScaleFactor).toInt(),
            true
        )
        bucketX = (displayMetrics.widthPixels / 2 - bucketBitmap.width / 2).toFloat()
        bucketY = (displayMetrics.heightPixels - bucketBitmap.height - 50).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw bucket
        canvas.drawBitmap(bucketBitmap, bucketX, bucketY, paint)

        // Draw score, missed and high score
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 100f, paint)
        canvas.drawText("Missed: $missed", 50f, 200f, paint)
        canvas.drawText("High Score: $highScore", 50f, 300f, paint)

        // Draw new record message if needed
        if (showNewRecordMessage) {
            paint.color = Color.RED
            canvas.drawText("New Record!", 50f, 400f, paint)
            paint.color = Color.BLACK // Reset color to default
        }

        // Check if game over
        if (missed >= 5) {
            if (score > highScore) {
                highScore = score
                prefs.edit().putInt("high_score", highScore).apply()
            }
            val intent = Intent(context, GameOverActivity::class.java).apply {
                putExtra("score", score)
                putExtra("high_score", highScore)
            }
            context.startActivity(intent)
            return
        }

        // Update high score and show new record message
        if (score > highScore && !newRecordFlag) {
            highScore = score
            prefs.edit().putInt("high_score", highScore).apply()
            newRecordFlag = true
            if (!newRecordShown) {
                showNewRecordMessage = true
                newRecordShown = true
                Handler().postDelayed({
                    showNewRecordMessage = false
                    invalidate() // Redraw to hide the message
                }, 1000) // Display message for 1 second
            }
        }

        // Update falling speed
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime >= 1000) {
            currentSpeed += 0.5f
            lastUpdateTime = currentTime
        }

        // Update spawn interval every 5 seconds
        if (currentTime - lastIntervalUpdateTime >= 5000) {
            if (spawnInterval > minSpawnInterval) {
                spawnInterval -= 10
            }
            lastIntervalUpdateTime = currentTime
        }

        // Spawn ice cream
        if (currentTime - lastDropTime > spawnInterval) {
            spawnIceCream()
            lastDropTime = currentTime
        }

        // Draw and update ice creams
        val iterator = iceCreams.iterator()
        while (iterator.hasNext()) {
            val iceCream = iterator.next()
            iceCream.y += currentSpeed
            iceCream.rotation += 2f // Adjust rotation speed here

            if (iceCream.y > height) {
                iterator.remove()
                missed++
            } else {
                canvas.save()
                canvas.rotate(iceCream.rotation, iceCream.x + iceCream.bitmap.width / 2, iceCream.y + iceCream.bitmap.height / 2)
                canvas.drawBitmap(iceCream.bitmap, iceCream.x, iceCream.y, paint)
                canvas.restore()

                if (iceCream.x in bucketX..(bucketX + bucketBitmap.width) && iceCream.y in bucketY..(bucketY + bucketBitmap.height)) {
                    iterator.remove()
                    score++
                }
            }
        }

        invalidate()
    }

    private fun spawnIceCream() {
        val bitmap = iceCreamBitmaps[random.nextInt(iceCreamBitmaps.size)]
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * iceCreamScaleFactor).toInt(), (bitmap.height * iceCreamScaleFactor).toInt(), true)
        val x = random.nextInt(width - scaledBitmap.width)
        val y = 0f
        iceCreams.add(IceCream(x.toFloat(), y, scaledBitmap))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            bucketX = event.x - bucketBitmap.width / 2
        }
        return true
    }

    data class IceCream(var x: Float, var y: Float, var bitmap: Bitmap, var rotation: Float = 0f)
}
