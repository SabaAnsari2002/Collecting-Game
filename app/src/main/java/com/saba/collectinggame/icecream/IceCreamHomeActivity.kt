package com.saba.collectinggame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog

class IceCreamHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_ice_cream)

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
            finishAffinity() // Close the app
        }
        setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    }.create().show()
}
}