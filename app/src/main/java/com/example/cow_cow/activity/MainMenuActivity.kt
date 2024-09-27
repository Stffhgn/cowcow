package com.example.cow_cow.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup buttons and their click listeners
         binding.apply {
                startGameButton.setOnClickListener {
                    // Navigate to the GameActivity with a transition animation
                    val intent = Intent(this@MainMenuActivity, GameActivity::class.java)
                    startActivity(intent)
                }
            }

            settingsButton.setOnClickListener {
                // Navigate to the Settings Activity
                startActivity(Intent(this@MainMenuActivity, SettingsActivity::class.java))
            }

            addPlayerButton.setOnClickListener {
                // Open the WhosPlayingFragment
                startActivity(Intent(this@MainMenuActivity, WhosPlayingActivity::class.java))
            }

            storeButton.setOnClickListener {
                // Navigate to the Store Fragment or Activity
                startActivity(Intent(this@MainMenuActivity, StoreActivity::class.java))
            }

            howToPlayButton.setOnClickListener {
                // Navigate to the How To Play Fragment
                startActivity(Intent(this@MainMenuActivity, HowToPlayActivity::class.java))
            }
        }
    }
}
