package com.saba.collectinggame

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set splash screen layout based on theme
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val selectedTheme = prefs.getString("selected_theme", "default")

        val splashLayout = when (selectedTheme) {
            "fruit" -> R.layout.activity_splash_fruit
            "coffee" -> R.layout.activity_splash_coffee
            "donut" -> R.layout.activity_splash_donut
            "fast_food" -> R.layout.activity_splash_fast_food
            "gun" -> R.layout.activity_splash_gun
            "bear" -> R.layout.activity_splash_bear
            "flower" -> R.layout.activity_splash_flower
            "car" -> R.layout.activity_splash_car

            else -> R.layout.activity_splash_ice_cream
        }

        setContentView(splashLayout)

        // Set up the back press callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

        // Delay for 2 seconds and then start the appropriate HomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = when (selectedTheme) {
                "fruit" -> Intent(this, FruitHomeActivity::class.java)
                "coffee" -> Intent(this, CoffeeHomeActivity::class.java)
                "donut" -> Intent(this, DonutHomeActivity::class.java)
                "fast_food" -> Intent(this, FastFoodHomeActivity::class.java)
                "gun" -> Intent(this, GunHomeActivity::class.java)
                "bear" -> Intent(this, BearHomeActivity::class.java)
                "flower" -> Intent(this, FlowerHomeActivity::class.java)
                "car" -> Intent(this, CarHomeActivity::class.java)

                else -> Intent(this, IceCreamHomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Do you want to exit the game?")
            setCancelable(true)
            setPositiveButton("Yes") { _, _ ->
                finishAffinity() // Close the app
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }
}
