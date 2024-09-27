package com.example.cow_cow.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cow_cow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start Game Button
        binding.startGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Game Settings Button
        binding.gameSettingsButton?.setOnClickListener {
            val intent = Intent(this, GameSettingsActivity::class.java)
            startActivity(intent)
        }

        // Who is Playing Button
        binding.whoIsPlayingButton.setOnClickListener {
            val intent = Intent(this, WhoIsPlayingActivity::class.java)
            startActivity(intent)
        }

        binding.howToPlayButton.setOnClickListener {
            Log.d("MainActivity", "How to Play button clicked")
            showHowToPlayDialog()
        }
    }

    private lateinit var scavengerHuntTimer: CountDownTimer

    private fun showHowToPlayDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("How to Play")
        builder.setMessage(
            """
            1. Each player is assigned a name in the game.
            2. Press the 'Cow,' 'Church,' or 'Water Tower' buttons when you see one on your journey.
            3. Select the player who spotted it first to award points.
               - Cow: 1 point
               - Church: 2 points
               - Water Tower: 3 points
            4. Players can join or leave teams using the White Fence button.
            5. The goal is to spot as many as possible during the trip and score the highest points!
            """.trimIndent()
        )
        builder.setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}