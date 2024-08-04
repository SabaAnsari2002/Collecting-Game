package com.saba.collectinggame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class IceCreamHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_icecream)

        val startButton: Button = findViewById(R.id.start_button_icecream)
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val themeButton: Button = findViewById(R.id.theme_button_icecream)
        themeButton.setOnClickListener {
            val intent = Intent(this, ThemeSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}