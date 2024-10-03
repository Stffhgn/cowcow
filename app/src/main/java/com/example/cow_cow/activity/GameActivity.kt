package com.example.cow_cow.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.controllers.MainGameController
import com.example.cow_cow.databinding.ActivityGameScreenBinding
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.gameFragments.CowCowFragment
import com.example.cow_cow.managers.GameManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.viewModels.GameViewModel

class GameActivity : AppCompatActivity() {

    // View binding for UI elements
    private lateinit var binding: ActivityGameScreenBinding
    private lateinit var gameViewModel: GameViewModel

    // DrawerLayout for left and right drawers
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val players = PlayerManager.getAllPlayers()
        GameManager.applyCustomRulesForGame(this, GameMode.CLASSIC)
        val mainGameController = MainGameController(gameViewModel) // 'this' refers to the Activity context


        // Inflate the layout using View Binding
        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize ViewModel
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Observe game news updates
        gameViewModel.gameNews.observe(this) { message ->
            binding.rotatingTextView.text = message
        }

        // Setup the TextView to rotate messages when clicked
        binding.rotatingTextView.setOnClickListener {
            gameViewModel.rotateGameNews()
        }

        // Example of adding new game updates dynamically
        gameViewModel.addGameNewsMessage("Player 1 scored 10 points!")
        gameViewModel.addGameNewsMessage("Player 2 called Cow!")
        gameViewModel.addGameNewsMessage("Timer: 5 minutes remaining")

        // Initialize drawer layout
        drawerLayout = binding.drawerLayout

        // Load CowCowFragment into the gameFragmentContainer when the activity is created
        if (savedInstanceState == null) {
            loadFragment(CowCowFragment())
        }

        // Setup the buttons for the left and right drawers
        setupDrawerButtons()

        // Setup the rotating TextView for updates
        setupRotatingTextView()
    }

    // Function to load the CowCowFragment into the container
    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.gameFragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    // Setup the left and right drawer buttons to open/close the respective drawers
    private fun setupDrawerButtons() {
        binding.leftDrawerButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(binding.leftDrawer)) {
                drawerLayout.closeDrawer(binding.leftDrawer)
            } else {
                drawerLayout.openDrawer(binding.leftDrawer)
            }
        }

        binding.rightDrawerButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(binding.rightDrawer)) {
                drawerLayout.closeDrawer(binding.rightDrawer)
            } else {
                drawerLayout.openDrawer(binding.rightDrawer)
            }
        }
    }

    // Setup rotating TextView to display updates or game-related news
    private fun setupRotatingTextView() {
        val messages = arrayOf("Latest News: Welcome to Cow Cow!", "Tip: Tap 'Menu' for player stats", "Timer: 10:00 remaining")

        var currentIndex = 0
        binding.rotatingTextView.text = messages[currentIndex]

        // Handle click events on the rotating TextView to show different messages
        binding.rotatingTextView.setOnClickListener {
            currentIndex = (currentIndex + 1) % messages.size
            binding.rotatingTextView.text = messages[currentIndex]

            // Show a toast for example purposes
            Toast.makeText(this, "Clicked: ${messages[currentIndex]}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        // Close the drawers when the back button is pressed if they're open
        if (drawerLayout.isDrawerOpen(binding.leftDrawer)) {
            drawerLayout.closeDrawer(binding.leftDrawer)
        } else if (drawerLayout.isDrawerOpen(binding.rightDrawer)) {
            drawerLayout.closeDrawer(binding.rightDrawer)
        } else {
            super.onBackPressed()
        }
    }
}
