package com.saba.collectinggame

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CoffeeWinActivity : AppCompatActivity() {
    private lateinit var winMusic: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win_coffee)

        winMusic = MediaPlayer.create(this, R.raw.win_music)
        winMusic.start()

        val score = intent.getIntExtra("score", 0)
        val highScore = intent.getIntExtra("high_score", 0)

        val scoreTextView: TextView = findViewById(R.id.score_text_view)
        scoreTextView.text = "Score: $score"

        val highScoreTextView: TextView = findViewById(R.id.high_score_text_view)
        highScoreTextView.text = "High Score: $highScore"

        val restartButton: Button = findViewById(R.id.restart_button)

        // نمایش دکمه restart بعد از چند ثانیه
        Handler().postDelayed({
            restartButton.isEnabled = true
            restartButton.setOnClickListener {
                winMusic.stop()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // دکمه restart بعد از 3 ثانیه فعال می‌شود

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
                val intent = Intent(this@CoffeeWinActivity, CoffeeHomeActivity::class.java)
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
        if (winMusic.isPlaying) {
            winMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!winMusic.isPlaying) {
            winMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        winMusic.release()
    }
}
