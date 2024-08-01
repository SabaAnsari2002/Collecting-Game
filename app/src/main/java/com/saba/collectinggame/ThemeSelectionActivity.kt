package com.saba.collectinggame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout

class ThemeSelectionActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_selection)

        val iceCreamTheme: LinearLayout = findViewById(R.id.ice_cream_theme)
        val coffeeTheme: LinearLayout = findViewById(R.id.coffee_theme)
        val donutTheme: LinearLayout = findViewById(R.id.donut_theme)
        val fastFoodTheme: LinearLayout = findViewById(R.id.fast_food_theme)
        val fruitTheme: LinearLayout = findViewById(R.id.fruit_theme)


        iceCreamTheme.setOnClickListener { setTheme("ice_cream") }
        coffeeTheme.setOnClickListener { setTheme("coffee") }
        donutTheme.setOnClickListener { setTheme("donut") }
        fastFoodTheme.setOnClickListener { setTheme("fast_food") }
        fruitTheme.setOnClickListener { setTheme("fruit") }

    }

    private fun setTheme(theme: String) {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putString("selected_theme", theme).apply()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
