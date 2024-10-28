package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.utils.TeamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val gameRepository: GameRepository,
    private val teamRepository: TeamRepository
) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext
    private val playerRepository = PlayerRepository(context)
    private val TAG = "GameViewModel"

    // ---- LiveData for players and team ----
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team> get() = _team

    // Now you can instantiate PlayerManager
    val playerManager = PlayerManager(playerRepository)

    // Similarly, instantiate GameManager with required dependencies
    private val gameManager = GameManager(playerManager)
    private val teamManager = TeamManager(playerRepository)

    // ---- Managers ----
    private val scavengerHuntManager = ScavengerHuntManager
    private val powerUpManager = PowerUpManager
    private val penaltyManager = PenaltyManager


    // ---- Game settings ----
    var quietGameEnabled: Boolean = false
    var falseCallPenaltyEnabled: Boolean = true
    var noRepeatRuleEnabled: Boolean = true

    // LiveData to hold news messages for the rotating TextView
    private val _gameNews = MutableLiveData<String>()
    val gameNews: LiveData<String> get() = _gameNews

    // Instance of the GameNewsManager
    private val gameNewsManager = GameNewsManager()

    // ---- Initialization ----
    init {
        Log.d(TAG, "ViewModel initialized. Loading players and team data.")
        loadPlayers()
        loadTeam()
    }

    // ---- Game and Player Management ----

    /**
     * Load all players from the repository.
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        val context = getApplication<Application>().applicationContext
        val playerList = PlayerRepository(context).getPlayers()
        _players.value = playerList
        Log.d(TAG, "Players loaded: ${playerList.size} players.")
    }

    /**
     * Load the team from the repository and set it in the LiveData.
     */
    fun loadTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading team from repository.")
                val loadedTeam = teamRepository.getTeam()
                _team.postValue(loadedTeam)
                Log.d(TAG, "Team loaded: ${loadedTeam.members.size} players.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading team: ${e.message}")
            }
        }
    }

    /**
     * Save the players to the repository.
     */
    fun savePlayers() {
        Log.d(TAG, "Saving players to repository.")

        // Save each player individually using the existing playerRepository
        _players.value?.forEach { player ->
            playerRepository.savePlayer(player)
            Log.d(TAG, "Player ${player.name} saved to repository.")
        }
    }

    /**
     * Save the team to the repository.
     */
    fun saveTeam() {
        Log.d(TAG, "Saving team to repository.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _team.value?.let { team ->
                    teamRepository.saveTeam() // Pass the team to saveTeam()
                    Log.d(TAG, "Team saved successfully.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving team: ${e.message}")
            }
        }
    }

    /**
     * Start a game with the specified game mode.
     */
    fun startGame(gameMode: GameMode, durationMillis: Long? = null) {
        Log.d(TAG, "Starting game in $gameMode mode.")
        gameManager.startGame(gameMode, durationMillis)
    }

        /**
         * Adds a team to the ViewModel.
         *
         * @param team The team to add.
         */
        fun addTeam(team: Team) {
            _team.value = team
            Log.d("GameViewModel", "Team added to ViewModel: ${team.name} with ${team.members.size} members.")
        }


    /**
     * Stop the current game.
     */
    fun stopGame() {
        Log.d(TAG, "Stopping the game.")
        gameManager.stopGame()
    }

    fun resetCalledObjects() {
        Log.d(TAG, "Resetting called objects for all players.")
        _players.value = _players.value?.map { player ->
            player.copy(cowCount = 0, churchCount = 0, waterTowerCount = 0)
        }
        savePlayers()  // Save updated players to the repository
    }

    /**
     * Add a player to the team.
     */
    fun addPlayerToTeam(player: Player) {
        Log.d(TAG, "Adding player ${player.name} to the team.")
        player.isOnTeam = true
        val currentTeam = _team.value ?: teamRepository.getTeam()
        TeamUtils.addPlayerToTeam(player, currentTeam)
        _team.value = currentTeam
        saveTeam()
        updatePlayer(player)
        Log.d(TAG, "Player ${player.name} has been added to the team.")
    }

    /**
     * Remove a player from the team.
     */
    fun removePlayerFromTeam(player: Player) {
        Log.d(TAG, "Removing player ${player.name} from the team.")
        player.isOnTeam = false
        val currentTeam = _team.value
        currentTeam?.let {
            TeamUtils.removePlayerFromTeam(player, it)
            _team.value = it
            saveTeam()
            updatePlayer(player)
            Log.d(TAG, "Player ${player.name} has been removed from the team.")
        } ?: Log.e(TAG, "No team found to remove player from.")
    }

    /**
     * Remove the current team from the repository and reset its state.
     */
    fun removeTeam() {
        Log.d(TAG, "Removing Team")

        val currentTeam = _team.value
        currentTeam?.let {
            teamManager.resetTeam()  // Reset the team using the TeamManager
            _team.value = null  // Update the view model state to reflect that there is no team
            saveTeam()  // Update persistence to save changes

            Log.d(TAG, "Team ${it.name} has been removed successfully.")
        } ?: Log.e(TAG, "No team found to remove.")
    }

    /**
     * Update a player's data and save changes.
     */
    fun updatePlayer(player: Player) {
        Log.d(TAG, "Updating player: ${player.name}")
        _players.value = _players.value?.map {
            if (it.id == player.id) player else it
        }
        savePlayers()
    }

// ---- Scavenger Hunt Management ----

    // ---- Game Actions ----

    /**
     * Apply points to the player based on the action performed.
     */
    fun applyPointsForAction(player: Player, action: String) {
        Log.d(TAG, "Applying points for action: $action for player ${player.name}.")
        when (action) {
            "Cow" -> player.cowCount += 1
            "Church" -> player.churchCount += 1
            "Water Tower" -> player.waterTowerCount += 1
        }
        playerManager.calculatePlayerPoints(player)
        updatePlayer(player)
    }

    // ---- Power-Up and Penalty Management ----

    fun activatePowerUp(player: Player, type: PowerUpType, duration: Long) {
        Log.d(TAG, "Activating power-up $type for player ${player.name}.")
        powerUpManager.activatePowerUp(player, type, duration)
        updatePlayer(player)
    }

    fun applyPenalty(player: Player, penaltyType: PenaltyType, duration: Long) {
        Log.d(TAG, "Applying penalty of type $penaltyType to player ${player.name}.")
        val penalty = Penalty(
            id = Penalty.generatePenaltyId(player.id, penaltyType).toString(),
            name = penaltyType.name,
            pointsDeducted = calculatePenaltyPoints(penaltyType),
            penaltyType = penaltyType,
            isActive = true,
            duration = duration,
            startTime = System.currentTimeMillis(),
            stackable = true,
            multiplier = 1.0
        )
        penaltyManager.applyPenalty(player, penalty)
        updatePlayer(player)
        Log.d(TAG, "Penalty $penaltyType applied successfully to player ${player.name}.")
    }

    // ---- Game News Management ----

    fun addGameNewsMessage(message: String) {
        Log.d(TAG, "Adding game news message: $message")
        gameNewsManager.addNewsMessage(message)
        _gameNews.value = message
    }

    fun rotateGameNews() {
        val nextMessage = gameNewsManager.getNextNewsMessage()
        Log.d(TAG, "Rotating game news: $nextMessage")
        _gameNews.value = nextMessage
    }

    // Utility for calculating penalty points
    private fun calculatePenaltyPoints(penaltyType: PenaltyType): Int {
        return when (penaltyType) {
            PenaltyType.POINT_DEDUCTION -> 10
            PenaltyType.SILENCED -> 0 // Silencing doesn't typically involve point deduction
            PenaltyType.TEMPORARY_BAN -> 0 // Banning might not involve point deduction either
            PenaltyType.FALSE_CALL -> 5
            PenaltyType.TIME_PENALTY -> 0 // Time penalty is time-based rather than points-based
            PenaltyType.OTHER -> 1 // Provide a default value for custom penalties
        }
    }

}
