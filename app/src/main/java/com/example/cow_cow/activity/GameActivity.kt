package com.example.cow_cow.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.controllers.*
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.gameFragments.GameSettingsFragmentDialog
import com.example.cow_cow.dialogs.WhosPlayingDialogFragmentDirections
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragment
import com.example.cow_cow.handlers.WhoCalledItHandler
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.managers.GameNewsManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScavengerHuntManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.repositories.*
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.viewModels.GameViewModelFactory

class GameActivity : AppCompatActivity(), OnPlayerSelectedListener {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameViewModel: GameViewModel

    // Define managers
    private lateinit var whoCalledItHandler: WhoCalledItHandler
    private lateinit var gameNewsManager: GameNewsManager
    private lateinit var scoreManager: ScoreManager
    private lateinit var scavengerHuntManager: ScavengerHuntManager
    private lateinit var scavengerHuntRepository: ScavengerHuntRepository

    // Controllers
    private lateinit var cowCowController: CowCowController
    private lateinit var rainbowCarController: RainbowCarController
    private lateinit var scavengerHuntController: ScavengerHuntController

    private lateinit var handler: Handler
    private val newsRunnable = object : Runnable {
        override fun run() {
            updateGameNews()
            handler.postDelayed(this, 15000)
        }
    }

    companion object {
        private const val TAG = "GameActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "GameActivity started")
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeComponents()
        setupObservers()
        setupControllers()
        setupButtons()

        gameNewsManager.initializeGameNews()
        handler.postDelayed(newsRunnable, 15000)

        loadCowCowButtons()
        loadScavengerHuntButtons()
        loadRainbowCarButton()

        Log.d(TAG, "Initial game setup complete.")
    }

    private fun initializeComponents() {
        Log.d(TAG, "Initializing components")

        // Initialize repositories
        val playerRepository = PlayerRepository(this)
        val gameRepository = GameRepository(this)
        val teamRepository = TeamRepository(this)
        scavengerHuntRepository = ScavengerHuntRepository(this)

        // Initialize PlayerManager and ScoreManager first
        val playerManager = PlayerManager(playerRepository)
        scoreManager = ScoreManager(playerManager)

        // Now initialize ScavengerHuntManager, as it depends on ScoreManager
        scavengerHuntManager = ScavengerHuntManager(scavengerHuntRepository, scoreManager)
        scavengerHuntManager.initialize()

        // Initialize GameViewModel with the required repositories
        gameViewModel = ViewModelProvider(
            this, GameViewModelFactory(application, gameRepository, teamRepository)
        )[GameViewModel::class.java]

        whoCalledItHandler = WhoCalledItHandler(
            players = gameViewModel.players.value ?: emptyList(),
            scoreManager = scoreManager
        )

        gameNewsManager = GameNewsManager()
        handler = Handler(mainLooper)

        Log.d(TAG, "Components initialized")
    }

    private fun setupObservers() {
        Log.d(TAG, "Setting up observers")

        gameViewModel.players.observe(this, Observer { updatedPlayers ->
            Log.d(TAG, "Players loaded: ${updatedPlayers.size}")
            whoCalledItHandler.players = updatedPlayers
        })

        gameViewModel.team.observe(this, Observer { team ->
            Log.d(TAG, if (team?.members?.isNotEmpty() == true) "Team data loaded with ${team.members.size} members." else "Team is empty.")
        })

        Log.d(TAG, "Observers set up")
    }

    private fun setupControllers() {
        Log.d(TAG, "Setting up controllers")

        val buttonContainer: FlexboxContainer = binding.gameButtonContainer

        cowCowController = initializeCowCowController(buttonContainer)
        rainbowCarController = initializeRainbowCarController(buttonContainer)
        scavengerHuntController = initializeScavengerHuntController(buttonContainer)

        whoCalledItHandler.setScavengerHuntController(scavengerHuntController)

        Log.d(TAG, "Controllers set up: CowCow, RainbowCar, and ScavengerHunt.")
    }

    private fun initializeCowCowController(buttonContainer: FlexboxContainer): CowCowController {
        return CowCowController(
            context = this,
            buttonContainer = buttonContainer,
            gameActivity = this,
            playerManager = gameViewModel.playerManager,
            whoCalledItHandler = whoCalledItHandler,
            scoreManager = scoreManager
        ).also {
            Log.d(TAG, "CowCowController initialized")
        }
    }

    private fun initializeRainbowCarController(buttonContainer: FlexboxContainer): RainbowCarController {
        return RainbowCarController(
            context = this,
            buttonContainer = buttonContainer,
            whoCalledItHandler = whoCalledItHandler
        ).also {
            Log.d(TAG, "RainbowCarController initialized")
        }
    }

    private fun initializeScavengerHuntController(buttonContainer: FlexboxContainer): ScavengerHuntController {
        return ScavengerHuntController(
            context = this,
            flexboxContainer = buttonContainer,
            players = gameViewModel.players.value ?: emptyList(),
            whoCalledItHandler = whoCalledItHandler,
            scavengerHuntManager = scavengerHuntManager
        ).also {
            Log.d(TAG, "ScavengerHuntController initialized")
        }
    }

    private fun setupButtons() {
        Log.d(TAG, "Setting up buttons")
        binding.settingsButton.setOnClickListener { showDialog("SETTINGS") }
        binding.whoIsPlayingButton.setOnClickListener { showDialog("WHO_IS_PLAYING") }
        Log.d(TAG, "Buttons set up")
    }

    override fun onPlayerSelected(playerId: String) {
        Log.d(TAG, "Player selected with ID: $playerId")
    }

    private fun showDialog(dialogType: String) {
        val dialogFragment = when (dialogType) {
            "SETTINGS" -> GameSettingsFragmentDialog.newInstance()
            "WHO_IS_PLAYING" -> WhosPlayingDialogFragment.newInstance()
            else -> {
                Log.e(TAG, "Unknown dialog type: $dialogType")
                null
            }
        }
        dialogFragment?.show(supportFragmentManager, dialogType)
        Log.d(TAG, "Dialog displayed: $dialogType")
    }

    fun loadRainbowCarButton() = logAndRun("Rainbow Car") {
        rainbowCarController.loadRainbowCarButtons()
    }

    fun loadScavengerHuntButtons() = logAndRun("Scavenger Hunt") {
        scavengerHuntController.loadScavengerHuntButtons()
    }

    private fun loadCowCowButtons() {
        Log.d(TAG, "Loading CowCow buttons")
        cowCowController.loadCowCowButtons { objectSelected ->
            Log.d(TAG, "Object selected from CowCow game: $objectSelected")
        }
    }

    private fun updateGameNews() {
        val nextNews = gameNewsManager.getNextNewsMessage()
        binding.gameTextView.text = nextNews
        Log.d(TAG, "Displaying news: $nextNews")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(newsRunnable)
        Log.d(TAG, "GameActivity destroyed and handler callbacks removed")
    }

    private fun logAndRun(taskName: String, task: () -> Unit) {
        Log.d(TAG, "Starting task: $taskName")
        runOnUiThread {
            task()
            Log.d(TAG, "$taskName UI refreshed and buttons loaded.")
        }
    }
}
