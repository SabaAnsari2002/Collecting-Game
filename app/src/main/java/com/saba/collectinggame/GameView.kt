package com.saba.collectinggame

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import java.util.*

class GameView(context: Context) : View(context) {
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
        BitmapFactory.decodeResource(resources, R.drawable.ice_cream8)
    )
    private var bucketBitmap: Bitmap
    private var bucketX = 0f
    private var bucketY = 0f
    private val iceCreams = mutableListOf<IceCream>()
    private val random = Random()
    private var lastDropTime = System.currentTimeMillis()

    // Scaling factors for bucket and ice creams
    private val bucketScaleFactor = 0.4f // Increase size of the bucket
    private val iceCreamScaleFactor = 0.4f // Decrease size of the ice creams

    // Variables for score and missed ice creams
    private var score = 0
    private var missed = 0

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

        // Draw score and missed
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 100f, paint)
        canvas.drawText("Missed: $missed", 50f, 200f, paint)

        // Check if game over
        if (missed >= 5) {
            val intent = Intent(context, GameOverActivity::class.java).apply {
                putExtra("score", score)
            }
            context.startActivity(intent)
            return
        }

        // Spawn ice cream
        if (System.currentTimeMillis() - lastDropTime > 1000) {
            spawnIceCream()
            lastDropTime = System.currentTimeMillis()
        }

        // Draw and update ice creams
        val iterator = iceCreams.iterator()
        while (iterator.hasNext()) {
            val iceCream = iterator.next()
            iceCream.y += 10
            if (iceCream.y > height) {
                iterator.remove()
                missed++
            } else {
                canvas.drawBitmap(iceCream.bitmap, iceCream.x, iceCream.y, paint)
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

    data class IceCream(var x: Float, var y: Float, var bitmap: Bitmap)
}
