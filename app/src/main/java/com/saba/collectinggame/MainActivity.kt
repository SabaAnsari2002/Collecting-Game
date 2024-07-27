package com.saba.collectinggame

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameMusic: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))

        gameMusic = MediaPlayer.create(this, R.raw.music)
        gameMusic.isLooping = true
        gameMusic.start()

        // Set up the back press callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("آیا می‌خواهید از بازی خارج شوید؟")
            setCancelable(true)
            setPositiveButton("بله") { _, _ ->
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton("خیر") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    override fun onPause() {
        super.onPause()
        if (gameMusic.isPlaying) {
            gameMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!gameMusic.isPlaying) {
            gameMusic.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameMusic.release()
    }
}