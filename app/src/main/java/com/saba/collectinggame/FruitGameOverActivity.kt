package com.saba.collectinggame

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FruitGameOverActivity : AppCompatActivity() {
    private lateinit var gameOverMusic: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over_fruit)

        gameOverMusic = MediaPlayer.create(this, R.raw.game_over_music)
        gameOverMusic.start()

        val score = intent.getIntExtra("score", 0)
        val highScore = intent.getIntExtra("high_score", 0)

        val scoreTextView: TextView = findViewById(R.id.score_text_view)
        scoreTextView.text = "Score: $score"

        val highScoreTextView: TextView = findViewById(R.id.high_score_text_view)
        highScoreTextView.text = "High Score: $highScore"

        val restartButton: Button = findViewById(R.id.restart_button_fruit)

        // نمایش دکمه restart بعد از چند ثانیه
        Handler().postDelayed({
            restartButton.isEnabled = true
            restartButton.setOnClickListener {
                gameOverMusic.stop()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // دکمه restart بعد از 3 ثانیه فعال می‌شود
    }

    override fun onPause() {
        super.onPause()
        if (gameOverMusic.isPlaying) {
            gameOverMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!gameOverMusic.isPlaying) {
            gameOverMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameOverMusic.release()
    }
}