package com.example.cow_cow.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityMainBinding
import com.example.cow_cow.mainFragments.*  // Importing main fragments (adjust if required)

class MainActivity : AppCompatActivity() {

    // View binding for the main activity
    private lateinit var binding: ActivityMainBinding

    // NavController for managing navigation between fragments
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavController for handling fragment navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up button click listeners for fragment and activity navigation
        setupButtons()
    }

    // Function to load fragments dynamically into the container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Set up button click listeners for fragment navigation and launching activities
    private fun setupButtons() {
        binding.apply {

            // Navigate to GameActivity (Start Game)
            startGameButton.setOnClickListener {
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                startActivity(intent)
            }

            // Navigate to App Settings Fragment
            settingsButton.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_appSettingsFragment)
            }

            // Load the Add Player Fragment (Who's Playing)
            addPlayerButton.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_whosPlayingFragment)
            }

            // Load the Store Fragment
            storeButton.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_storeFragment)
            }

            // Load the How To Play Fragment
            howToPlayButton.setOnClickListener {
                navController.navigate(R.id.action_startFragment_to_howToPlayFragment)
            }
        }
    }
}
