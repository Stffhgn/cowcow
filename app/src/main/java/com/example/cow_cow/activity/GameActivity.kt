// GameActivity.kt

package com.example.cow_cow.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.cow_cow.PlayerFragment.PlayerListFragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ActivityGameScreenBinding
import com.example.cow_cow.gameFragments.CowCowFragment
import com.example.cow_cow.gameFragments.GameSettingsFragment
import com.example.cow_cow.gameFragments.WhoCalledItFragment
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.*
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.handlers.GameEventHandler
import com.example.cow_cow.viewModels.GameViewModelFactory

class GameActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityGameScreenBinding

    // Navigation
    private lateinit var navController: NavController

    // ViewModel
    private lateinit var gameViewModel: GameViewModel

    // Repositories
    private lateinit var playerRepository: PlayerRepository
    private lateinit var teamRepository: TeamRepository
    private lateinit var scavengerHuntRepository: ScavengerHuntRepository

    // Managers
    private lateinit var gameEventHandler: GameEventHandler
    private lateinit var playerManager: PlayerManager
    private lateinit var teamManager: TeamManager
    private lateinit var scoreManager: ScoreManager
    private lateinit var scavengerHuntManager: ScavengerHuntManager
    private lateinit var soundManager: SoundManager
    private lateinit var gameNewsManager: GameNewsManager
    private lateinit var powerUpManager: PowerUpManager
    private lateinit var penaltyManager: PenaltyManager

    // Hold game data
    private lateinit var players: List<Player>
    private var calledObjectType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GameActivity", "GameActivity started")

        // Set up view binding
        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        initializeComponents()

        // Set up NavController
        setupNavController()

        // Add initial game news
        initializeGameNews()

        // Fetch all players and set up data needed for the game
        initializeGameData()

        // Start rotating news
        startRotatingNews()

        // Setup drawer buttons
        setupDrawerButtons()

        // Load the initial fragment
        if (savedInstanceState == null) {
            loadFragment(CowCowFragment())
        }
    }

    // Function to initialize components
    private fun initializeComponents() {
        // Initialize GameRepository
        val gameRepository = GameRepository(this) // Replace with your actual repository initialization

        // Initialize GameViewModel with custom factory
        val factory = GameViewModelFactory(application, gameRepository)
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Repositories
        playerRepository = PlayerRepository(this)
        teamRepository = TeamRepository(this)
        scavengerHuntRepository = ScavengerHuntRepository(this)

        // Managers
        playerManager = PlayerManager
        teamManager = TeamManager
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
            teamManager = teamManager,
            scoreManager = scoreManager,
            scavengerHuntManager = scavengerHuntManager,
            soundManager = soundManager,
            conditionManager = ConditionManager,
            achievementManager = AchievementManager(this),
            customRuleManager = CustomRuleManager(this),
            gameManager = GameManager,
            gameNewsManager = gameNewsManager,
            penaltyManager = penaltyManager,
            powerUpManager = powerUpManager,
            triviaManager = TriviaManager(TriviaRepository()),
        )
    }

    // Set up NavController
    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
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
        players = playerRepository.getPlayers(this)
        Log.d("GameActivity", "Number of players initialized: \${players.size}")
    }

    // Function to handle rotating game news
    private fun startRotatingNews() {
        val handler = Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                updateGameNews()
                handler.postDelayed(this, 5000) // Update news every 5 seconds
            }
        }
        handler.post(runnable)
    }

    // Updates the game news on the screen
    private fun updateGameNews() {
        val nextNews = gameNewsManager.getNextNewsMessage()
        Log.d("GameActivity", "Displaying news: \$nextNews")
        binding.rotatingTextView.text = nextNews
    }

    // Replace the current fragment
    private fun loadFragment(fragment: Fragment) {
        Log.d("GameActivity", "Loading fragment: \${fragment::class.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Handle receiving an object from CowCowFragment
    fun receiveObject(objectType: String) {
        Log.d("GameActivity", "Received object: \$objectType")
        calledObjectType = objectType

        // Handle unclaimed object event using GameEventHandler
        gameEventHandler.handleUnclaimedObjectEvent(objectType)

        if (players.isNotEmpty()) {
            // Open WhoCalledItFragment for player selection
            openWhoCalledItFragment(players, objectType)
        } else {
            Log.d("GameActivity", "No players found, cannot open WhoCalledItFragment")
        }
    }

    // Open WhoCalledItFragment with the list of players and the object type
    private fun openWhoCalledItFragment(players: List<Player>, objectType: String) {
        val fragment = WhoCalledItFragment.newInstance(players, objectType)

        // Load the WhoCalledItFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack("WhoCalledItFragment")
            .commit()
        Log.d("GameActivity", "WhoCalledItFragment opened with player list and object type")
    }

    // Set up drawer buttons
    private fun setupDrawerButtons() {
        binding.leftDrawerButton.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(binding.leftDrawer)) {
                binding.drawerLayout.closeDrawer(binding.leftDrawer)
            } else {
                binding.drawerLayout.openDrawer(binding.leftDrawer)
                loadGameSettingsFragment()
            }
        }

        binding.rightDrawerButton.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(binding.rightDrawer)) {
                binding.drawerLayout.closeDrawer(binding.rightDrawer)
            } else {
                binding.drawerLayout.openDrawer(binding.rightDrawer)
                loadPlayerListFragment()
            }
        }
    }

    private fun loadPlayerListFragment() {
        val playerListFragment = PlayerListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.rightDrawer, playerListFragment)
            .commit()
    }

    private fun loadGameSettingsFragment() {
        val gameSettingsFragment = GameSettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.leftDrawer, gameSettingsFragment)
            .commit()
    }

    // Receive selected player from WhoCalledItFragment
    fun receiveSelectedPlayer(player: Player) {
        Log.d("GameActivity", "Player \${player.name} selected for object: \$calledObjectType")
        calledObjectType?.let {
            gameEventHandler.handlePlayerSelected(player, it)
        }
        calledObjectType = null
        loadFragment(CowCowFragment())
    }

    // Optional: Handle game over, reset, etc.
    fun resetGame() {
        gameEventHandler.handleGameReset(players)
        updateGameNews()
        loadFragment(CowCowFragment())
    }
}
