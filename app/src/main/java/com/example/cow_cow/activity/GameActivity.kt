package com.example.cow_cow.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.gameFragments.CowCowFragment
import com.example.cow_cow.gameFragments.CowCowFragmentDirections
import com.example.cow_cow.gameFragments.GameSettingsFragmentDialog
import com.example.cow_cow.gameFragments.TeamManagementFragmentDialog
import com.example.cow_cow.gameFragments.WhoCalledItFragment
import com.example.cow_cow.gameFragments.WhoCalledItFragmentDirections
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.*
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.handlers.GameEventHandler
import com.example.cow_cow.interfaces.OnObjectSelectedListener
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.viewModels.GameViewModelFactory
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragment
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragmentDirections
import com.example.cow_cow.mainFragments.StartFragmentDirections
import com.example.cow_cow.playerFragment.PlayerListDialogFragment

class GameActivity : AppCompatActivity(), OnPlayerSelectedListener, OnPlayerAndObjectSelectedListener, OnObjectSelectedListener {

    private var unclaimedPlayerId: String? = null
    // View Binding
    private lateinit var binding: ActivityGameBinding

    // Navigation
    private lateinit var navController: NavController

    // ViewModel
    private lateinit var gameViewModel: GameViewModel

    // Repositories
    private lateinit var playerRepository: PlayerRepository
    private lateinit var teamRepository: TeamRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var scavengerHuntRepository: ScavengerHuntRepository

    // Managers
    private lateinit var gameEventHandler: GameEventHandler
    private lateinit var playerManager: PlayerManager
    private lateinit var scoreManager: ScoreManager
    private lateinit var scavengerHuntManager: ScavengerHuntManager
    private lateinit var soundManager: SoundManager
    private lateinit var gameNewsManager: GameNewsManager
    private lateinit var powerUpManager: PowerUpManager
    private lateinit var penaltyManager: PenaltyManager

    // Hold game data
    private lateinit var players: List<Player>
    private var calledObjectType: String? = null

    // Handler and Runnable for news rotation (moved inside onCreate)
    private lateinit var handler: Handler
    private val newsRunnable = object : Runnable {
        override fun run() {
            updateGameNews()
            handler.postDelayed(this, 5000) // Update news every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GameActivity", "GameActivity started")

        // Set up view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Log for starting NavController setup
        Log.d("GameActivity", "Setting up NavController...")

        // Initialize components
        initializeComponents()

        // Set up NavController
        setupNavController()

        // Initialize handler after context is available
        handler = Handler(mainLooper)

        // Add initial game news
        initializeGameNews()

        // Fetch all players and set up data needed for the game
        initializeGameData()

        // Start rotating news
        startRotatingNews()

        // Setup buttons
        setupButtons()

        // Replace StartFragment with CowCowFragment
        replaceWithCowCowFragment()
    }

    // Function to initialize components
    private fun initializeComponents() {
        // Initialize GameRepository
        gameRepository = GameRepository(this)

        // Initialize TeamRepository
        teamRepository = TeamRepository(this)

        // Initialize GameViewModel with custom factory
        val factory = GameViewModelFactory(application, gameRepository, teamRepository)
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Repositories
        playerRepository = PlayerRepository(this)
        scavengerHuntRepository = ScavengerHuntRepository(this)

        // Managers
        playerManager = PlayerManager(playerRepository)
        scoreManager = ScoreManager
        scavengerHuntManager = ScavengerHuntManager
        soundManager = SoundManager
        gameNewsManager = GameNewsManager()
        powerUpManager = PowerUpManager
        penaltyManager = PenaltyManager

        // Initialize GameEventHandler
        gameEventHandler = GameEventHandler(
            context = this,
            playerManager = playerManager,
            teamRepository = teamRepository,
            scoreManager = scoreManager,
            scavengerHuntManager = scavengerHuntManager,
            soundManager = soundManager,
            conditionManager = ConditionManager,
            achievementManager = AchievementManager(this),
            customRuleManager = CustomRuleManager(this),
            gameManager = GameManager(playerManager),
            gameNewsManager = gameNewsManager,
            penaltyManager = penaltyManager,
            powerUpManager = powerUpManager,
            triviaManager = TriviaManager(TriviaRepository()),
        )
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Log when NavHostFragment is found
        Log.d("GameActivity", "NavHostFragment found: $navHostFragment")

        navController = navHostFragment.navController

        // Log the NavController to check if it's properly initialized
        Log.d("GameActivity", "NavController initialized: $navController")
    }

    private fun replaceWithCowCowFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val cowCowFragment = CowCowFragment() // Create an instance of CowCowFragment
        transaction.replace(R.id.nav_host_fragment, cowCowFragment)
        transaction.commit()
        Log.d("GameActivity", "Replaced StartFragment with CowCowFragment")
    }

    fun togglePlayerTeamStatus(player: Player) {
        // Toggle the team status through the GameEventHandler
        gameEventHandler.togglePlayerTeamStatus(player)

        // Update repository and UI as necessary
        gameEventHandler.updateTeamScore()
    }

    fun openTeamManagementDialog() {
        // Use the already initialized players list
        if (::players.isInitialized && players.isNotEmpty()) {
            val dialog = TeamManagementFragmentDialog.newInstance(players)
            dialog.show(supportFragmentManager, TeamManagementFragmentDialog.TAG_DIALOG)
        } else {
            Log.e("GameActivity", "Players list is not initialized or is empty")
        }
    }

    // Receives the selected object type from CowCowFragment
    override fun onObjectSelected(objectType: String) {
        Log.d("GameActivity", "Object selected: $objectType")

        // Show PlayerListDialogFragment with the selected object type
        val dialogFragment = PlayerListDialogFragment.newInstance(objectType)
        dialogFragment.show(supportFragmentManager, PlayerListDialogFragment.TAG)
    }

    // Implementation for OnPlayerSelectedListener (for WhosPlayingFragment)
    override fun onPlayerSelected(playerId: String) {
        Log.d("GameActivity", "Player selected with ID: $playerId")

        // Use the NavController to navigate to PlayerStatsFragment
        val navController = findNavController(R.id.nav_host_fragment) // Make sure this is the correct ID of your NavHostFragment

        // Create the navigation action to go to PlayerStatsFragment
        val action = WhosPlayingDialogFragmentDirections.actionWhosPlayingDialogFragmentToPlayerStatsFragment(playerId)
        navController.navigate(action)
    }
    // Implementation for OnPlayerAndObjectSelectedListener (for PlayerListDialogFragment)
    override fun onPlayerSelected(playerId: String, objectType: String) {
        Log.d("GameActivity", "Player selected with ID: $playerId and ObjectType: $objectType")

        // Find player by ID and call GameEventHandler to handle the logic
        val player = players.find { it.id == playerId }
        if (player != null) {
            // Pass the player and object type to GameEventHandler
            gameEventHandler.handlePlayerSelected(player, objectType)
        } else {
            Log.e("GameActivity", "Player with ID: $playerId was not found.")
        }
    }

    // Add this function to resolve the missing reference in WhoCalledItFragment
    fun receiveSelectedPlayer(player: Player, objectType: String) {
        Log.d("GameActivity", "Player received from PlayerListDialogFragment: ${player.name}")
        calledObjectType?.let {
            gameEventHandler.handlePlayerSelected(player, it)
        }
        calledObjectType = null
    }

    // Function to navigate to PlayerStatsFragment
    private fun navigateToPlayerStats(playerId: String) {
        Log.d("GameActivity", "Navigating to PlayerStatsFragment with playerId: $playerId")
        val action = WhosPlayingDialogFragmentDirections.actionWhosPlayingDialogFragmentToPlayerStatsFragment(playerId)
        navController.navigate(action)
    }

    // Initialize game news
    private fun initializeGameNews() {
        gameNewsManager.addNewsMessage("Dad is Awesome")
        gameNewsManager.addNewsMessage("Tanner is Amazing!")
        gameNewsManager.addNewsMessage("Solo thinks you stink")
        gameNewsManager.addNewsMessage("Zoey is a RockStar")
    }

    // Fetch all initial game data
    private fun initializeGameData() {
        players = playerRepository.getPlayers()
        Log.d("GameActivity", "Number of players initialized: ${players.size}")
    }

    // Start rotating news
    private fun startRotatingNews() {
        handler.post(newsRunnable)
    }

    // Updates the game news on the screen
    private fun updateGameNews() {
        val nextNews = gameNewsManager.getNextNewsMessage()
        Log.d("GameActivity", "Displaying news: $nextNews")
        binding.gameTextView.text = nextNews
    }

    // Set up buttons to switch fragments
    private fun setupButtons() {
        binding.leftDrawerButton.setOnClickListener {
            loadGameSettingsDialog()
        }

        binding.rightDrawerButton.setOnClickListener {
            openWhosPlayingDialog()
        }
    }

    private fun openWhosPlayingDialog() {
        val dialogFragment = WhosPlayingDialogFragment.newInstance()
        dialogFragment.show(supportFragmentManager, WhosPlayingDialogFragment.TAG_DIALOG)
    }

    private fun loadGameSettingsDialog() {
        Log.d("GameActivity", "Opening GameSettingsFragmentDialog")
        val dialogFragment = GameSettingsFragmentDialog.newInstance()
        dialogFragment.show(supportFragmentManager, GameSettingsFragmentDialog.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the news rotation when the activity is destroyed to avoid memory leaks
        handler.removeCallbacks(newsRunnable)
        Log.d("GameActivity", "GameActivity destroyed and handler callbacks removed")
    }

    // GameActivity: Receives the selected object type and handles showing the PlayerListDialogFragment
    private fun receiveObject(objectType: String) {
        Log.d("GameActivity", "Received object: $objectType")
        calledObjectType = objectType

        // Handle unclaimed object event using GameEventHandler
        gameEventHandler.handleUnclaimedObjectEvent(objectType)

        if (players.isNotEmpty()) {
            // Show PlayerListDialogFragment
            calledObjectType?.let {
                val dialogFragment = PlayerListDialogFragment.newInstance(it)
                dialogFragment.show(supportFragmentManager, PlayerListDialogFragment.TAG)
                Log.d("GameActivity", "Showing PlayerListDialogFragment with object type: $it")
            } ?: Log.e("GameActivity", "calledObjectType is null, cannot open PlayerListDialogFragment")
        } else {
            Log.d("GameActivity", "No players found, cannot open PlayerListDialogFragment")
        }
    }

    // GameActivity: Open WhoCalledItFragment with the object type as a dialog
    private fun openWhoCalledItFragment(objectType: String) {
        Log.d("GameActivity", "Opening WhoCalledItFragment with object type: $objectType")

        // Create a new instance of WhoCalledItFragment with player list and object type
        val whoCalledItFragment = WhoCalledItFragment.newInstance(players, objectType)

        // Show the fragment as a dialog
        whoCalledItFragment.show(supportFragmentManager, "WhoCalledItFragment")

        Log.d("GameActivity", "WhoCalledItFragment opened with object type: $objectType")
    }
    // Optional: Handle game over, reset, etc.
    fun resetGame() {
        gameEventHandler.handleGameReset(players)
        updateGameNews()
        Log.d("GameActivity", "Resetting game and navigating to CowCowFragment")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, CowCowFragment())
        transaction.commit()
        Log.d("GameActivity", "Replaced current fragment with CowCowFragment")
    }
}

