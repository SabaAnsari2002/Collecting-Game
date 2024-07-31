package com.saba.collectinggame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ThemeSelectionActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_selection)

        val defaultThemeButton: Button = findViewById(R.id.default_theme_button)
        val donutThemeButton: Button = findViewById(R.id.donut_theme_button)
        val cappuccinoThemeButton: Button = findViewById(R.id.cappuccino_theme_button)

        defaultThemeButton.setOnClickListener {
            setTheme("default")
        }

        donutThemeButton.setOnClickListener {
            setTheme("donut")
        }

        cappuccinoThemeButton.setOnClickListener {
            setTheme("cappuccino")
        }
    }

    private fun setTheme(theme: String) {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putString("selected_theme", theme).apply()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
