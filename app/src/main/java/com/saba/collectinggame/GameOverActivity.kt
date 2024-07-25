package com.saba.collectinggame

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra("score", 0)

        val scoreTextView: TextView = findViewById(R.id.score_text_view)
        scoreTextView.text = "Score: $score"

        val restartButton: Button = findViewById(R.id.restart_button)

        // نمایش دکمه restart بعد از چند ثانیه
        Handler().postDelayed({
            restartButton.isEnabled = true
            restartButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // دکمه restart بعد از 3 ثانیه فعال می‌شود
    }
}
