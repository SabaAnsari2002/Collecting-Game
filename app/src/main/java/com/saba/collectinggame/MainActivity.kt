package com.saba.collectinggame

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameMusic: MediaPlayer
    private lateinit var prefs: SharedPreferences
    private var gameView: GameView? = null
    private lateinit var pauseButton: ImageButton
    private var isMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        isMuted = prefs.getBoolean("isMuted", false)

        gameView = findViewById(R.id.gameView)
        gameMusic = MediaPlayer.create(this, R.raw.music)
        gameMusic.isLooping = true
        if (!isMuted) {
            gameMusic.start()
        }

        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener {
            showPauseDialog()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun showExitConfirmationDialog() {
        gameView?.pauseGame()
        if (!isMuted) {
            gameMusic.pause()
        }
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
                gameView?.resumeGame()
                if (!isMuted) {
                    gameMusic.start()
                }
            }
        }.create().show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showPauseDialog() {
        gameView?.pauseGame()
        if (!isMuted) {
            gameMusic.pause()
        }

        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.pause_dialog, null)

        val resumeButton: Button = dialogView.findViewById(R.id.resumeButton)
        val restartButton: Button = dialogView.findViewById(R.id.restartButton)
        val muteButton: Button = dialogView.findViewById(R.id.muteButton)

        val alertDialog = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(false)
        }.create()

        resumeButton.setOnClickListener {
            gameView?.resumeGame()
            if (!isMuted) {
                gameMusic.start()
            }
            alertDialog.dismiss()
        }

        restartButton.setOnClickListener {
            restartGame()
            alertDialog.dismiss()
        }

        muteButton.text = if (isMuted) "Unmute" else "Mute"
        muteButton.setOnClickListener {
            toggleMute()
            muteButton.text = if (isMuted) "Unmute" else "Mute"
        }

        alertDialog.show()
    }

    private fun restartGame() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun toggleMute() {
        if (isMuted) {
            gameMusic.start()
        } else {
            gameMusic.pause()
        }
        isMuted = !isMuted

        val editor = prefs.edit()
        editor.putBoolean("isMuted", isMuted)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        if (gameMusic.isPlaying) {
            gameMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isMuted && !gameMusic.isPlaying) {
            gameMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameMusic.release()
    }
}
