package com.example.cow_cow.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityMainBinding
import com.example.cow_cow.interfaces.OnObjectSelectedListener
import com.example.cow_cow.interfaces.OnPlayerSelectedListener

class MainActivity : AppCompatActivity(), OnPlayerSelectedListener, OnObjectSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Implementation for the OnPlayerSelectedListener interface
    override fun onPlayerSelected(playerId: String) {
        navController.navigate(R.id.whosPlayingFragment)
    }

    // Implementation for the OnObjectSelectedListener interface
    override fun onObjectSelected(objectId: String) {
        // Handle the logic for starting the GameActivity instead of navigating to a fragment
        val intent = Intent(this, GameActivity::class.java).apply {
            // You can pass the objectId or other relevant data to the GameActivity here if needed
            putExtra("OBJECT_ID", objectId)
        }
        startActivity(intent)
    }
}
