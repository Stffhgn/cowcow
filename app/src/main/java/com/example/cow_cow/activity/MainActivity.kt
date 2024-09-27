package com.example.cow_cow.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityMainBinding
import com.example.cow_cow.gameFragments.*

class MainActivity : AppCompatActivity() {

    // View binding for the main activity
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initially load the start screen fragment or main fragment
        loadFragment(StartFragment())

        // Set up button click listeners for fragment navigation
        binding.startGameButton.setOnClickListener {
            loadFragment(StartFragment())
        }

        binding.settingsButton.setOnClickListener {
            loadFragment(SettingsFragment())
        }

        binding.addPlayerButton.setOnClickListener {
            loadFragment(AddPlayerFragment())
        }

        binding.storeButton.setOnClickListener {
            loadFragment(StoreFragment())
        }

        binding.howToPlayButton.setOnClickListener {
            loadFragment(HowToPlayFragment())
        }
    }

    // Function to load the fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}