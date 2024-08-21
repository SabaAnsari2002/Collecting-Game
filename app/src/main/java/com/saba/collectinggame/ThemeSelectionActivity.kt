package com.saba.collectinggame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ThemeSelectionActivity : AppCompatActivity() {

    private val themePrices = mapOf(
        "coffee" to 10,
        "donut" to 20,
        "fast_food" to 30,
        "fruit" to 40,
        "gun" to 50
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_selection)

        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val totalCoins = prefs.getInt("total_coins", 0)
        val savedCoins = prefs.getInt("coins", 0)

        // Update total coins if there are saved coins
        if (savedCoins > 0) {
            val newTotal = totalCoins + savedCoins
            prefs.edit().putInt("total_coins", newTotal).putInt("coins", 0).apply()
        }

        // Display total coins
        val totalCoinsTextView: TextView = findViewById(R.id.total_coins_text_view)
        totalCoinsTextView.text = "Total Coins: ${prefs.getInt("total_coins", 0)}"

        // Initialize lock icon visibility
        initializeLockIcons()

        // Set click listeners for themes
        findViewById<LinearLayout>(R.id.ice_cream_theme).setOnClickListener { setTheme("ice_cream") }
        findViewById<LinearLayout>(R.id.coffee_theme).setOnClickListener { setTheme("coffee") }
        findViewById<LinearLayout>(R.id.donut_theme).setOnClickListener { setTheme("donut") }
        findViewById<LinearLayout>(R.id.fast_food_theme).setOnClickListener { setTheme("fast_food") }
        findViewById<LinearLayout>(R.id.fruit_theme).setOnClickListener { setTheme("fruit") }
        findViewById<LinearLayout>(R.id.gun_theme).setOnClickListener { setTheme("gun") }
    }

    private fun setTheme(theme: String) {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val totalCoins = prefs.getInt("total_coins", 0)

        // Check if the theme is already unlocked
        if (theme == "ice_cream" || isThemePurchased(theme)) {
            activateTheme(theme)
            return
        }

        val price = themePrices[theme] ?: 0
        if (totalCoins >= price) {
            AlertDialog.Builder(this).apply {
                setMessage("Do you want to buy the $theme theme for $price coins?")
                setPositiveButton("Yes") { _, _ ->
                    if (totalCoins >= price) {
                        // Deduct coins and unlock theme
                        prefs.edit().putInt("total_coins", totalCoins - price).apply()
                        prefs.edit().putBoolean("${theme}_purchased", true).apply()
                        activateTheme(theme)
                        updateLockIconVisibility(theme)
                    } else {
                        showInsufficientCoinsMessage()
                    }
                }
                setNegativeButton("No", null)
            }.show()
        } else {
            showInsufficientCoinsMessage()
        }
    }

    private fun updateLockIconVisibility(theme: String) {
        val lockIconId = when (theme) {
            "coffee" -> R.id.lock_icon_coffee
            "donut" -> R.id.lock_icon_donut
            "fast_food" -> R.id.lock_icon_fast_food
            "fruit" -> R.id.lock_icon_fruit
            "gun" -> R.id.lock_icon_gun
            else -> return
        }

        val priceTextId = when (theme) {
            "coffee" -> R.id.coffee_price
            "donut" -> R.id.donut_price
            "fast_food" -> R.id.fast_food_price
            "fruit" -> R.id.fruit_price
            "gun" -> R.id.gun_price
            else -> return
        }

        findViewById<ImageView>(lockIconId).visibility = View.GONE
        findViewById<TextView>(priceTextId).visibility = View.GONE  // پنهان کردن قیمت پس از خرید
    }

    private fun isThemePurchased(theme: String): Boolean {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        return prefs.getBoolean("${theme}_purchased", false)
    }

    private fun activateTheme(theme: String) {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putString("selected_theme", theme).apply()

        val intent = when (theme) {
            "fruit" -> Intent(this, FruitHomeActivity::class.java)
            "coffee" -> Intent(this, CoffeeHomeActivity::class.java)
            "donut" -> Intent(this, DonutHomeActivity::class.java)
            "fast_food" -> Intent(this, FastFoodHomeActivity::class.java)
            "gun" -> Intent(this, GunHomeActivity::class.java)
            else -> Intent(this, IceCreamHomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun showInsufficientCoinsMessage() {
        AlertDialog.Builder(this).apply {
            setMessage("You do not have enough coins to buy this theme.")
            setPositiveButton("OK", null)
        }.show()
    }

    private fun initializeLockIcons() {
        setLockIconVisibility("coffee", R.id.lock_icon_coffee, R.id.coffee_price)
        setLockIconVisibility("donut", R.id.lock_icon_donut, R.id.donut_price)
        setLockIconVisibility("fast_food", R.id.lock_icon_fast_food, R.id.fast_food_price)
        setLockIconVisibility("fruit", R.id.lock_icon_fruit, R.id.fruit_price)
        setLockIconVisibility("gun", R.id.lock_icon_gun, R.id.gun_price)
    }

    private fun setLockIconVisibility(theme: String, lockIconId: Int, priceTextId: Int) {
        val lockIcon = findViewById<ImageView>(lockIconId)
        val priceText = findViewById<TextView>(priceTextId)

        if (isThemePurchased(theme)) {
            lockIcon.visibility = View.GONE
            priceText.visibility = View.GONE  // پنهان کردن قیمت در صورت خریداری شدن تم
        } else {
            lockIcon.visibility = View.VISIBLE
            priceText.visibility = View.VISIBLE  // نمایش قیمت در صورت عدم خریداری
        }
    }
}
