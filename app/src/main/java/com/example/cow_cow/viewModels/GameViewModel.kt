package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.utils.GameUtils
import com.example.cow_cow.utils.TeamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val gameRepository: GameRepository,
    private val teamRepository: TeamRepository
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val playerRepository = PlayerRepository(context)
    private val TAG = "GameViewModel"

    // LiveData for players and team
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    private val _team = MutableLiveData<Team?>()
    val team: MutableLiveData<Team?> get() = _team

    // Manager and utility classes
    val playerManager = PlayerManager(playerRepository)
    private val scoreManager = ScoreManager(playerManager)
    private val penaltyManager = PenaltyManager(playerManager)
    private val customRuleManager = CustomRuleManager(context,scoreManager,penaltyManager)
    private val gameUtils = GameUtils(scoreManager)
    private val teamUtils = TeamUtils(scoreManager)
    private val gameManager = GameManager(playerManager, customRuleManager, gameUtils)
    private val teamManager = TeamManager(playerRepository, scoreManager)
    private val scavengerHuntRepository = ScavengerHuntRepository(context)
    private val scavengerHuntManager = ScavengerHuntManager(scavengerHuntRepository, scoreManager)
    private val powerUpManager = PowerUpManager
    private val gameNewsManager = GameNewsManager()

    // Game settings
    var quietGameEnabled: Boolean = false
    var falseCallPenaltyEnabled: Boolean = true
    var noRepeatRuleEnabled: Boolean = true

    // LiveData to hold news messages
    private val _gameNews = MutableLiveData<String>()
    val gameNews: LiveData<String> get() = _gameNews

    init {
        Log.d(TAG, "ViewModel initialized. Loading players and team data.")
        loadPlayers()
        loadTeam()
    }

    // Load players from repository
    private fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        _players.value = playerRepository.getPlayers()
        Log.d(TAG, "Players loaded: ${_players.value?.size ?: 0} players.")
    }

    // Load team asynchronously
    private fun loadTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading team from repository.")
                _team.postValue(teamRepository.getTeam())
                Log.d(TAG, "Team loaded.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading team: ${e.message}")
            }
        }
    }

    // Save players to repository
    fun savePlayers() {
        _players.value?.forEach { player ->
            playerRepository.savePlayer(player)
            Log.d(TAG, "Player ${player.name} saved to repository.")
        }
    }

    // Save team to repository
    fun saveTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            _team.value?.let { team ->
                teamRepository.saveTeam()
                Log.d(TAG, "Team saved successfully.")
            } ?: Log.e(TAG, "No team found to save.")
        }
    }

    // Start a game
    fun startGame(gameMode: GameMode, durationMillis: Long? = null) {
        gameManager.startGame(gameMode, durationMillis)
        Log.d(TAG, "Game started in $gameMode mode.")
    }

    // Stop the game
    fun stopGame() {
        gameManager.stopGame()
        Log.d(TAG, "Game stopped.")
    }

    // Reset counts of called objects
    fun resetCalledObjects() {
        _players.value = _players.value?.map { player ->
            player.copy(cowCount = 0, churchCount = 0, waterTowerCount = 0)
        }
        savePlayers()
        Log.d(TAG, "Reset called objects for all players.")
    }

    // Team-related methods
    fun addPlayerToTeam(player: Player) {
        player.isOnTeam = true
        val currentTeam = _team.value ?: teamRepository.getTeam()
        teamUtils.addPlayerToTeam(player, currentTeam)
        _team.value = currentTeam
        saveTeam()
        updatePlayer(player)
        Log.d(TAG, "Player ${player.name} added to team.")
    }

    fun removePlayerFromTeam(player: Player) {
        player.isOnTeam = false
        _team.value?.let {
            teamUtils.removePlayerFromTeam(player, it)
            _team.value = it
            saveTeam()
            updatePlayer(player)
            Log.d(TAG, "Player ${player.name} removed from team.")
        } ?: Log.e(TAG, "No team found to remove player from.")
    }

    fun addTeam(team: Team) {
        _team.value = team  // Set the LiveData value for the team
        saveTeam()          // Save the team to persist changes
        Log.d(TAG, "Team added: ${team.name} with ${team.members.size} members.")
    }


    fun removeTeam() {
        _team.value?.let {
            teamManager.resetTeam()
            _team.value = null
            saveTeam()
            Log.d(TAG, "Team removed successfully.")
        } ?: Log.e(TAG, "No team found to remove.")
    }

    // Update player and save
    fun updatePlayer(player: Player) {
        _players.value = _players.value?.map {
            if (it.id == player.id) player else it
        }
        savePlayers()
        Log.d(TAG, "Updated player: ${player.name}")
    }

    fun activatePowerUp(player: Player, type: PowerUpType, duration: Long) {
        powerUpManager.activatePowerUp(player, type, duration)
        updatePlayer(player)
        Log.d(TAG, "Power-up $type activated for ${player.name}")
    }

    fun applyPenalty(player: Player, penaltyType: PenaltyType, duration: Long) {
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
        Log.d(TAG, "Penalty $penaltyType applied to ${player.name}")
    }

    // Game news management
    fun addGameNewsMessage(message: String) {
        gameNewsManager.addNewsMessage(message)
        _gameNews.value = message
    }

    fun rotateGameNews() {
        _gameNews.value = gameNewsManager.getNextNewsMessage()
    }

    private fun calculatePenaltyPoints(penaltyType: PenaltyType): Int {
        return when (penaltyType) {
            PenaltyType.POINT_DEDUCTION -> 10
            PenaltyType.SILENCED -> 0
            PenaltyType.TEMPORARY_BAN -> 0
            PenaltyType.FALSE_CALL -> 5
            PenaltyType.TIME_PENALTY -> 0
            PenaltyType.OTHER -> 1
        }
    }
}
