package com.example.cow_cow.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerDrawerAdapter
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.databinding.ActivityGameScreenBinding
import com.example.cow_cow.fragments.CowCowFragment
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.viewModels.GameViewModelFactory

class GameActivity : AppCompatActivity() {

    // View binding for UI elements
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameScreenBinding: ActivityGameScreenBinding

    // ViewModel instance
    private lateinit var gameViewModel: GameViewModel

    // Adapter for the player drawer
    private lateinit var drawerAdapter: PlayerDrawerAdapter

    // Media player for sounds
    private var mediaPlayer: MediaPlayer? = null

    // Controller to handle game logic
    private lateinit var cowCowController: CowCowController

    // Left and Right drawers
    private lateinit var leftDrawer: DrawerLayout
    private lateinit var rightDrawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate Game Binding layout
        binding = ActivityGameBinding.inflate(layoutInflater)
        gameScreenBinding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val repository = GameRepository()
        val factory = GameViewModelFactory(application, repository)
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Initialize CowCow controller
        cowCowController = CowCowController()

        // Setup buttons, fragments, and drawers
        setupDrawerButtons()
        setupRotatingTextView(binding.rotatingTextView)

        // Load CowCow fragment into the gameFragmentContainer
        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.gameFragmentContainer, CowCowFragment())
            fragmentTransaction.commit()
        }

        // Initialize drawers
        leftDrawer = gameScreenBinding.drawerLayout
        rightDrawer = gameScreenBinding.drawerLayout

        // Setup navigation drawer
        setupDrawer()

        // Observe players LiveData to update UI
        gameViewModel.players.observe(this) { players ->
            val teamPlayers = players.filter { it.isOnTeam }
            val individualPlayers = players.filter { !it.isOnTeam }
            drawerAdapter.updateData(teamPlayers, individualPlayers)
        }

        // Setup other buttons and listeners
        setupButtons()
        updateTeamScore()
    }

    // Method to handle left and right drawer buttons
    private fun setupDrawerButtons() {
        binding.leftDrawerButton.setOnClickListener {
            if (leftDrawer.isDrawerOpen(binding.leftDrawerContent)) {
                leftDrawer.closeDrawer(binding.leftDrawerContent)
            } else {
                leftDrawer.openDrawer(binding.leftDrawerContent)
            }
        }

        binding.rightDrawerButton.setOnClickListener {
            if (rightDrawer.isDrawerOpen(binding.rightDrawerContent)) {
                rightDrawer.closeDrawer(binding.rightDrawerContent)
            } else {
                rightDrawer.openDrawer(binding.rightDrawerContent)
            }
        }
    }

    // Rotating TextView setup to display important game updates
    private fun setupRotatingTextView(textView: TextView) {
        val messages = arrayOf("Timer: 10:00", "Cow called by Player 1!", "Church called by Player 2!")

        // Set a rotating message
        var currentMessageIndex = 0
        textView.text = messages[currentMessageIndex]

        textView.setOnClickListener {
            when (currentMessageIndex) {
                0 -> openTimerFragment()
                1 -> openCowCallFragment()
                2 -> openChurchCallFragment()
            }

            currentMessageIndex = (currentMessageIndex + 1) % messages.size
            textView.text = messages[currentMessageIndex]
        }
    }

    // Open Timer Fragment
    private fun openTimerFragment() {
        // Add logic to open timer fragment
    }

    // Open Cow Call Fragment
    private fun openCowCallFragment() {
        // Add logic to open cow call fragment
    }

    // Open Church Call Fragment
    private fun openChurchCallFragment() {
        // Add logic to open church call fragment
    }

    // Setup the navigation drawer
    private fun setupDrawer() {
        binding.navigationView.findViewById<RecyclerView>(R.id.drawerRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            drawerAdapter = PlayerDrawerAdapter(gameViewModel.team, gameViewModel.getIndividualPlayers())
            adapter = drawerAdapter
        }
    }

    // Setup buttons and their actions
    private fun setupButtons() {
        binding.apply {
            cowButton.setOnClickListener {
                playSound(R.raw.cow_sound)
                showPlayerSelectionDialog("Cow") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Cow")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            churchButton.setOnClickListener {
                playSound(R.raw.church_sound)
                showPlayerSelectionDialog("Church") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Church")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            waterTowerButton.setOnClickListener {
                playSound(R.raw.water_tower_sound)
                showPlayerSelectionDialog("Water Tower") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Water Tower")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            whiteFenceButton.setOnClickListener {
                showPlayerTeamToggleDialog()
            }

            backButton.setOnClickListener { finish() }

            resetButton?.setOnClickListener {
                gameViewModel.resetCalledObjects()
                Toast.makeText(this@GameActivity, "Objects have been reset!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Play sound effects
    private fun playSound(soundResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundResId).apply {
            start()
            setOnCompletionListener { release() }
        }
    }

    // Update team score display
    private fun updateTeamScore() {
        val totalTeamScore = gameViewModel.calculateTeamScore()
        binding.teamScoreTextView?.text = "Team Score: $totalTeamScore"
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.loadPlayers()
        gameViewModel.loadTeam()
    }

    override fun onPause() {
        super.onPause()
        gameViewModel.savePlayers()
        gameViewModel.saveTeam()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        gameViewModel.scavengerHuntTimer?.cancel()
        super.onDestroy()
    }
}
