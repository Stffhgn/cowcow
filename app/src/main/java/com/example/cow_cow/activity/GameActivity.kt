package com.example.cow_cow.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.controllers.*
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.gameFragments.GameSettingsFragmentDialog
import com.example.cow_cow.gameFragments.TeamGamesFragmentDialog
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragment
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.managers.GameNewsManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.viewModels.*

class GameActivity : AppCompatActivity(), OnPlayerSelectedListener {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var scoreViewModel: ScoreViewModel
    private lateinit var scavengerHuntViewModel: ScavengerHuntViewModel

    private lateinit var gameNewsManager: GameNewsManager
    lateinit var cowCowController: CowCowController
    private lateinit var rainbowCarController: RainbowCarController
    private lateinit var scavengerHuntController: ScavengerHuntController
    lateinit var scoreManager: ScoreManager

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
        handler.postDelayed(newsRunnable, 15000)  // Start rotating news
        Log.d(TAG, "Initial game setup complete.")
    }

    private fun initializeComponents() {
        Log.d(TAG, "Initializing components")

        val gameRepository = GameRepository(this)
        val teamRepository = TeamRepository(this)

        gameViewModel = ViewModelProvider(
            this, GameViewModelFactory(application, gameRepository, teamRepository)
        ).get(GameViewModel::class.java)

        scoreViewModel = ViewModelProvider(this).get(ScoreViewModel::class.java)
        scoreManager = ScoreManager

        val scavengerHuntRepository = ScavengerHuntRepository(this) // Initialize the repository

        val scavengerHuntFactory = ScavengerHuntViewModelFactory(
            application = application,
            players = gameViewModel.players.value ?: emptyList(),
            repository = scavengerHuntRepository // Pass the repository here
        )

        scavengerHuntViewModel = ViewModelProvider(this, scavengerHuntFactory).get(ScavengerHuntViewModel::class.java)


        gameNewsManager = GameNewsManager()
        handler = Handler(mainLooper)

        Log.d(TAG, "Components initialized")
    }

    private fun setupObservers() {
        Log.d(TAG, "Setting up observers")

        gameViewModel.players.observe(this, Observer { updatedPlayers ->
            if (updatedPlayers.isNotEmpty()) {
                loadCowCowButtons()
                loadScavengerHuntButtons()
                updateTeamGamesButtonVisibility()
                Log.d(TAG, "Players loaded: ${updatedPlayers.size}")
            } else {
                Log.w(TAG, "Player list is empty after loading.")
            }
        })

        gameViewModel.team.observe(this, Observer { team ->
            if (team.members.isNotEmpty()) {
                updateTeamGamesButtonVisibility()
                Log.d(TAG, "Team data loaded with ${team.members.size} members.")
            } else {
                Log.w(TAG, "Team is empty.")
            }
        })

        Log.d(TAG, "Observers set up")
    }

    private fun setupControllers() {
        Log.d(TAG, "Setting up controllers")
        val gameButtonContainer: FlexboxContainer = binding.gameButtonContainer

        cowCowController = CowCowController(
            context = this,
            buttonContainer = gameButtonContainer,
            gameActivity = this,
            playerManager = gameViewModel.playerManager
        )
        Log.d(TAG, "CowCowController initialized")

        rainbowCarController = RainbowCarController(
            context = this,
            buttonContainer = gameButtonContainer,
            playerManager = gameViewModel.playerManager,
            scoreManager = scoreManager
        )
        Log.d(TAG, "RainbowCarController initialized")

        scavengerHuntController = ScavengerHuntController(
            activity = this,
            context = this,
            flexboxContainer = gameButtonContainer,
            playerManager = gameViewModel.playerManager,
            scoreManager = scoreManager,
            scavengerHuntViewModel = scavengerHuntViewModel
        )
        Log.d(TAG, "ScavengerHuntController initialized")

        Log.d(TAG, "Controllers set up: CowCow, RainbowCar, and ScavengerHunt.")
    }

    private fun setupButtons() {
        Log.d(TAG, "Setting up buttons")

        binding.settingsButton.setOnClickListener { showDialog("SETTINGS") }
        binding.teamGamesButton.setOnClickListener { showDialog("TEAM_GAMES") }
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
            "TEAM_GAMES" -> TeamGamesFragmentDialog()
            else -> {
                Log.e(TAG, "Unknown dialog type: $dialogType")
                null
            }
        }
        dialogFragment?.show(supportFragmentManager, dialogType)
        Log.d(TAG, "Dialog displayed: $dialogType")
    }

    fun loadRainbowCarButton() = logAndRun("Rainbow Car") { rainbowCarController.loadRainbowCarButtons() }

    fun loadScavengerHuntButtons() = logAndRun("Scavenger Hunt") { scavengerHuntController.loadScavengerHuntItems() }

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

    fun updateTeamGamesButtonVisibility() {
        binding.teamGamesButton.visibility = if (gameViewModel.team.value?.members?.isNotEmpty() == true) View.VISIBLE else View.GONE
        Log.d(TAG, "TeamGames button visibility updated based on team status")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(newsRunnable)
        Log.d(TAG, "GameActivity destroyed and handler callbacks removed")
    }

    private fun logAndRun(taskName: String, task: () -> Unit) {
        runOnUiThread {
            task()
            Log.d(TAG, "$taskName UI refreshed and buttons loaded.")
        }
    }
}
