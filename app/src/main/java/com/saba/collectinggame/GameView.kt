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
    private var bucketBitmap: Bitmap
    private var bucketX = 0f
    private var bucketY = 0f
    private val iceCreams = mutableListOf<IceCream>()
    private val random = Random()
    private var lastDropTime = System.currentTimeMillis()
    private var lastUpdateTime = System.currentTimeMillis()
    private var lastIntervalUpdateTime = System.currentTimeMillis()

    // Scaling factors for bucket and ice creams
    private val defaultBucketScaleFactor = 0.4f
    private val donutBucketScaleFactor = 0.19f
    private val coffeeBucketScaleFactor = 0.18f
    private val fruitBucketScaleFactor = 0.18f
    private val fastfoodBucketScaleFactor = 0.2f
    private val gunBucketScaleFactor = 0.2f

    private val iceCreamScaleFactor = 0.4f
    private val donutScaleFactor = 0.09f
    private val coffeeScaleFactor = 0.1f
    private val fruitScaleFactor = 0.1f
    private val fastfoodScaleFactor = 0.09f
    private val gunScaleFactor = 0.11f

    // Variables for score and missed ice creams
    private var score = 0
    private var missed = 0

    // Initial speed and speed increment
    private val initialSpeed = 10f
    private var currentSpeed = initialSpeed

    // Variables for spawn interval and minimum spawn interval
    private var spawnInterval = 500L // Initial spawn interval in milliseconds
    private val minSpawnInterval = 200L // Minimum spawn interval in milliseconds

    // SharedPreferences for high score and theme
    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private var highScore = prefs.getInt("high_score", 0)
    private val selectedTheme = prefs.getString("selected_theme", "default")

    // Variables for new record message
    private var newRecordFlag = false // Flag to indicate if new record is set
    private var showNewRecordMessage = false // Flag to show new record message
    private var newRecordShown = false // Ensure the message is shown only once

    // Bitmaps for ice creams
    private val iceCreamBitmaps: List<Bitmap>

    init {
        val displayMetrics = context.resources.displayMetrics
        bucketBitmap = getBucketBitmapForTheme(selectedTheme)
        bucketBitmap = Bitmap.createScaledBitmap(
            bucketBitmap,
            (bucketBitmap.width * getBucketScaleFactorForTheme(selectedTheme)).toInt(),
            (bucketBitmap.height * getBucketScaleFactorForTheme(selectedTheme)).toInt(),
            true
        )
        bucketX = (displayMetrics.widthPixels / 2 - bucketBitmap.width / 2).toFloat()
        bucketY = (displayMetrics.heightPixels - bucketBitmap.height - 50).toFloat()
        iceCreamBitmaps = getIceCreamBitmapsForTheme(selectedTheme)
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
                winGame() // فراخوانی تابع winGame برای نمایش صفحه پیروزی مربوط به تم انتخاب شده
            } else {
                endGame() // فراخوانی تابع endGame برای نمایش صفحه گیم اور مربوط به تم انتخاب شده
            }
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
                canvas.rotate(
                    iceCream.rotation,
                    iceCream.x + iceCream.bitmap.width / 2,
                    iceCream.y + iceCream.bitmap.height / 2
                )
                canvas.drawBitmap(iceCream.bitmap, iceCream.x, iceCream.y, paint)
                canvas.restore()
            }

            if (iceCream.x in bucketX..(bucketX + bucketBitmap.width) && iceCream.y in bucketY..(bucketY + bucketBitmap.height)) {
                iterator.remove()
                score++
            }
        }

        invalidate()
    }

    private fun winGame() {
        val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val selectedTheme = prefs.getString("selected_theme", "default")
        val intent = when (selectedTheme) {
            "fruit" -> Intent(context, FruitWinActivity::class.java)
            "donut" -> Intent(context, DonutWinActivity::class.java)
            "coffee" -> Intent(context, CoffeeWinActivity::class.java)
            "fast_food" -> Intent(context, FastFoodWinActivity::class.java)
            "gun" -> Intent(context, GunWinActivity::class.java)

            else -> Intent(context, IceCreamWinActivity::class.java) // اکتیویتی پیشفرض
        }
        intent.putExtra("score", score)
        intent.putExtra("high_score", highScore)
        context.startActivity(intent)
        (context as MainActivity).finish()
    }

    private fun endGame() {
        val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val selectedTheme = prefs.getString("selected_theme", "default")

        val intent = when (selectedTheme) {
            "fruit" -> Intent(context, FruitGameOverActivity::class.java)
            "donut" -> Intent(context, DonutGameOverActivity::class.java)
            "coffee" -> Intent(context, CoffeeGameOverActivity::class.java)
            "fast_food" -> Intent(context, FastFoodGameOverActivity::class.java)
            "gun" -> Intent(context, GunGameOverActivity::class.java)

            else -> Intent(context, IceCreamGameOverActivity::class.java) // اکتیویتی پیش‌فرض
        }
        intent.putExtra("score", score)
        intent.putExtra("high_score", highScore)
        context.startActivity(intent)
        (context as MainActivity).finish()
    }
    private fun spawnIceCream() {
        val bitmap = iceCreamBitmaps[random.nextInt(iceCreamBitmaps.size)]
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * getIceCreamScaleFactorForTheme(selectedTheme)).toInt(),
            (bitmap.height * getIceCreamScaleFactorForTheme(selectedTheme)).toInt(),
            true
        )
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

    private fun getBucketBitmapForTheme(theme: String?): Bitmap {
        return when (theme) {
            "donut" -> BitmapFactory.decodeResource(resources, R.drawable.donutbucket)
            "coffee" -> BitmapFactory.decodeResource(resources, R.drawable.coffeebucket)
            "fast_food" -> BitmapFactory.decodeResource(resources, R.drawable.fastfoodbucket)
            "fruit" -> BitmapFactory.decodeResource(resources, R.drawable.fruitbucket)
            "gun" -> BitmapFactory.decodeResource(resources, R.drawable.gunbucket)
            else -> BitmapFactory.decodeResource(resources, R.drawable.icecreambucket)
        }
    }

    private fun getBucketScaleFactorForTheme(theme: String?): Float {
        return when (theme) {
            "donut" -> donutBucketScaleFactor
            "coffee" -> coffeeBucketScaleFactor
            "fast_food" -> fastfoodBucketScaleFactor
            "fruit" -> fruitBucketScaleFactor
            "gun" -> gunBucketScaleFactor
            else -> defaultBucketScaleFactor
        }
    }

    private fun getIceCreamScaleFactorForTheme(theme: String?): Float {
        return when (theme) {
            "donut" -> donutScaleFactor
            "coffee" -> coffeeScaleFactor
            "fast_food" -> fastfoodScaleFactor
            "fruit" -> fruitScaleFactor
            "gun" -> gunScaleFactor
            else -> iceCreamScaleFactor
        }
    }

    private fun getIceCreamBitmapsForTheme(theme: String?): List<Bitmap> {
        return when (theme) {
            "donut" -> listOf(
                BitmapFactory.decodeResource(resources, R.drawable.donut1),
                BitmapFactory.decodeResource(resources, R.drawable.donut2),
                BitmapFactory.decodeResource(resources, R.drawable.donut3),
                BitmapFactory.decodeResource(resources, R.drawable.donut4),
                BitmapFactory.decodeResource(resources, R.drawable.donut5),
                BitmapFactory.decodeResource(resources, R.drawable.donut6)
            )
            "coffee" -> listOf(
                BitmapFactory.decodeResource(resources, R.drawable.coffee1),
                BitmapFactory.decodeResource(resources, R.drawable.coffee2),
                BitmapFactory.decodeResource(resources, R.drawable.coffee3),
                BitmapFactory.decodeResource(resources, R.drawable.coffee4),
                BitmapFactory.decodeResource(resources, R.drawable.coffee5),
                BitmapFactory.decodeResource(resources, R.drawable.coffee6),
                BitmapFactory.decodeResource(resources, R.drawable.coffee7),
                BitmapFactory.decodeResource(resources, R.drawable.coffee8),
                BitmapFactory.decodeResource(resources, R.drawable.coffee9),
                BitmapFactory.decodeResource(resources, R.drawable.coffee10),
                BitmapFactory.decodeResource(resources, R.drawable.coffee11)

            )
            "fast_food" -> listOf(
                BitmapFactory.decodeResource(resources, R.drawable.fastfood1),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood2),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood3),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood4),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood5),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood6),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood7),
                BitmapFactory.decodeResource(resources, R.drawable.fastfood8)

            )
            "fruit" -> listOf(
                BitmapFactory.decodeResource(resources, R.drawable.fruit1),
                BitmapFactory.decodeResource(resources, R.drawable.fruit2),
                BitmapFactory.decodeResource(resources, R.drawable.fruit3),
                BitmapFactory.decodeResource(resources, R.drawable.fruit4),
                BitmapFactory.decodeResource(resources, R.drawable.fruit5),
                BitmapFactory.decodeResource(resources, R.drawable.fruit6),
                BitmapFactory.decodeResource(resources, R.drawable.fruit7),
                BitmapFactory.decodeResource(resources, R.drawable.fruit8)


            )

            "gun" -> listOf(
                BitmapFactory.decodeResource(resources, R.drawable.gun1),
                BitmapFactory.decodeResource(resources, R.drawable.gun2),
                BitmapFactory.decodeResource(resources, R.drawable.gun3),
                BitmapFactory.decodeResource(resources, R.drawable.gun4),
                BitmapFactory.decodeResource(resources, R.drawable.gun5),
                BitmapFactory.decodeResource(resources, R.drawable.gun6),
                BitmapFactory.decodeResource(resources, R.drawable.gun7),
                BitmapFactory.decodeResource(resources, R.drawable.gun8),
                BitmapFactory.decodeResource(resources, R.drawable.gun9),
                BitmapFactory.decodeResource(resources, R.drawable.gun10),
                BitmapFactory.decodeResource(resources, R.drawable.gun11),
                BitmapFactory.decodeResource(resources, R.drawable.gun12),
                BitmapFactory.decodeResource(resources, R.drawable.gun13)

            )
            else -> listOf(
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
        }
    }

    data class IceCream(val x: Float, var y: Float, val bitmap: Bitmap, var rotation: Float = 0f)
}
