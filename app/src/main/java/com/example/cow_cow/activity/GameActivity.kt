package com.example.cow_cow.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.controllers.RainbowCarController
import com.example.cow_cow.controllers.TeamController
import com.example.cow_cow.controllers.ScavengerHuntController
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.gameFragments.GameSettingsFragmentDialog
import com.example.cow_cow.gameFragments.TeamGamesFragmentDialog
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragment
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.managers.AchievementManager
import com.example.cow_cow.managers.GameNewsManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.PowerUpManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.managers.TeamManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.viewModels.GameViewModelFactory
import com.example.cow_cow.viewModels.ScoreViewModel
import com.example.cow_cow.viewModels.ScavengerHuntViewModel
import com.example.cow_cow.viewModels.ScavengerHuntViewModelFactory

class GameActivity : AppCompatActivity(), OnPlayerSelectedListener {

    // View Binding
    private lateinit var binding: ActivityGameBinding

    // ViewModels
    private lateinit var gameViewModel: GameViewModel
    private lateinit var scoreViewModel: ScoreViewModel
    private lateinit var scavengerHuntViewModel: ScavengerHuntViewModel // Added

    // Managers and Controllers
    private lateinit var gameNewsManager: GameNewsManager
    private lateinit var teamController: TeamController
    lateinit var cowController: CowCowController
    private lateinit var rainbowCarController: RainbowCarController
    private lateinit var scavengerHuntController: ScavengerHuntController
    lateinit var scoreManager: ScoreManager
    private lateinit var playerManager: PlayerManager

    // Hold game data
    lateinit var players: List<Player>

    // Handler for news rotation
    private lateinit var handler: Handler
    private val newsRunnable = object : Runnable {
        override fun run() {
            updateGameNews()
            handler.postDelayed(this, 15000) // Update news every 15 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "GameActivity started")

        // Set up view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        initializeComponents()

        // Initialize controllers with FlexboxContainer
        setupControllers()

        // Setup game UI and start news rotation
        gameNewsManager.initializeGameNews()
        teamController.initializeGameData()
        gameNewsManager.startRotatingNews(handler)

        // Load initial game buttons
        loadCowCowButtons()
        loadScavengerHuntButtons()
        Log.d(TAG, "Initial game buttons loaded.")

        // Setup bottom buttons
        setupButtons()
    }

    private fun initializeComponents() {
        val playerRepository = PlayerRepository(this)
        players = playerRepository.getPlayers()
        val gameRepository = GameRepository(this)
        val teamRepository = TeamRepository(this)

        // Managers
        gameNewsManager = GameNewsManager()
        scoreManager = ScoreManager
        playerManager =PlayerManager(playerRepository)

        // ViewModels
        scoreViewModel = ViewModelProvider(this).get(ScoreViewModel::class.java)
        val factory = GameViewModelFactory(application, gameRepository, teamRepository)
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Initialize ScavengerHuntViewModel with players
        val scavengerHuntFactory = ScavengerHuntViewModelFactory(application, players)
        scavengerHuntViewModel = ViewModelProvider(this, scavengerHuntFactory).get(ScavengerHuntViewModel::class.java)

        // Controllers
        teamController = TeamController(gameViewModel, scoreViewModel, teamRepository,playerManager, TeamManager(playerRepository))

        // Initialize handler for rotating news
        handler = Handler(mainLooper)
    }

    private fun setupControllers() {
        val playerRepository = PlayerRepository(this)
        playerManager = PlayerManager(playerRepository)

        // Use a single FlexboxContainer for the game buttons
        val gameButtonContainer: FlexboxContainer = binding.gameButtonContainer

        // Create controllers, passing in the required dependencies
        cowController = CowCowController(this, gameButtonContainer, this, playerManager)
        rainbowCarController = RainbowCarController(this, gameButtonContainer, this, playerManager, scoreManager)
        teamController.initializeGameData()
        scavengerHuntController = ScavengerHuntController(
            activity = this,
            context = this,
            flexboxContainer = gameButtonContainer,
            gameActivity = this,
            playerManager = playerManager,
            scoreManager = scoreManager,
            scavengerHuntViewModel = scavengerHuntViewModel // Changed
        )

        Log.d(TAG, "Controllers set up: CowCow, RainbowCar, and ScavengerHunt.")
    }

    private fun setupButtons() {
        binding.settingsButton.setOnClickListener {
            showDialog("SETTINGS")
        }

        binding.teamGamesButton.setOnClickListener {
            Log.d(TAG, "Team Games button clicked")
            showDialog("TEAM_GAMES")
        }

        binding.whoIsPlayingButton.setOnClickListener {
            showDialog("WHO_IS_PLAYING")
        }
    }

    // Implement the onPlayerSelected method to handle player selection
    override fun onPlayerSelected(playerId: String) {
        Log.d("GameActivity", "Player selected with ID: $playerId")
        // Handle player selection, e.g., start a new game, show player info, etc.
    }

    private fun showDialog(dialogType: String) {
        val dialogFragment = when (dialogType) {
            "SETTINGS" -> GameSettingsFragmentDialog.newInstance()
            "WHO_IS_PLAYING" -> WhosPlayingDialogFragment.newInstance()
            "TEAM_GAMES" -> TeamGamesFragmentDialog()
            else -> null
        }
        dialogFragment?.show(supportFragmentManager, dialogType)
    }

    fun loadRainbowCarButton() {
        runOnUiThread {
            rainbowCarController.loadRainbowCarButtons()
            Log.d(TAG, "Rainbow Car UI refreshed and buttons loaded.")
        }
    }

    fun loadScavengerHuntButtons() {
        runOnUiThread {
            scavengerHuntController.loadScavengerHuntItems()
            Log.d(TAG, "Scavenger Hunt UI refreshed and buttons loaded.")
        }
    }

    private fun loadCowCowButtons() {
        cowController.loadCowCowButtons { objectSelected ->
            Log.d(TAG, "Object selected from CowCow game: $objectSelected")
            // Handle the action when an object is selected
        }
    }

    private fun updateGameNews() {
        val nextNews = gameNewsManager.getNextNewsMessage()
        Log.d(TAG, "Displaying news: $nextNews")
        binding.gameTextView.text = nextNews
    }

    fun updateUI() {
        Log.d(TAG, "UI updated after game completion")
    }

    fun updateTeamGamesButtonVisibility() {
        binding.teamGamesButton.visibility = if (teamController.areAllPlayersOnTeam()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(newsRunnable)
        Log.d(TAG, "GameActivity destroyed and handler callbacks removed")
    }
}
