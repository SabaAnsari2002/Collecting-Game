package com.saba.collectinggame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog

class FastFoodHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_fast_food)

        val startButton: Button = findViewById(R.id.start_button_fastfood)
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val themeButton: Button = findViewById(R.id.theme_button_fastfood)
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