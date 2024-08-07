package com.saba.collectinggame

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameMusic: MediaPlayer
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
        gameMusic = MediaPlayer.create(this, R.raw.music)
        gameMusic.isLooping = true
        gameMusic.start()

        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Set up the back press callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Do you want to exit the game?")
            setCancelable(true)
            setPositiveButton("Yes") { _, _ ->
                val selectedTheme = prefs.getString("selected_theme", "default")
                val intent = when (selectedTheme) {
                    "fruit" -> Intent(this@MainActivity, FruitHomeActivity::class.java)
                    "coffee" -> Intent(this@MainActivity, CoffeeHomeActivity::class.java)
                    "donut" -> Intent(this@MainActivity, DonutHomeActivity::class.java)
                    "fast_food" -> Intent(this@MainActivity, FastFoodHomeActivity::class.java)
                    "gun" -> Intent(this@MainActivity, GunHomeActivity::class.java)
                    else -> Intent(this@MainActivity, IceCreamHomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    override fun onPause() {
        super.onPause()
        if (gameMusic.isPlaying) {
            gameMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!gameMusic.isPlaying) {
            gameMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameMusic.release()
    }
}
