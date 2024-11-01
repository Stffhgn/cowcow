package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize managers
    private val context: Context = getApplication<Application>().applicationContext
    private val playerRepository = PlayerRepository(context)

    private val playerManager = PlayerManager(playerRepository)
    private val scoreManager: ScoreManager = ScoreManager(playerManager)

    // LiveData to hold individual player scores
    private val _playerScores = MutableLiveData<Map<Player, Int>>()
    val playerScores: LiveData<Map<Player, Int>> get() = _playerScores

    // LiveData to hold team score (if in team mode)
    private val _teamScore = MutableLiveData<Int>()
    val teamScore: LiveData<Int> get() = _teamScore

    // LiveData for tracking high scores
    private val _highScores = MutableLiveData<List<Player>>()
    val highScores: LiveData<List<Player>> get() = _highScores

    // LiveData for tracking score updates or errors
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    /**
     * Initialize ViewModel by loading initial player scores and team score
     */
    init {
        loadPlayerScores()
        calculateTeamScore()
        loadHighScores()
    }

    /**
     * Load player scores using the ScoreManager
     */
    private fun loadPlayerScores() {
        viewModelScope.launch {
            try {
                val players = withContext(Dispatchers.IO) {
                    playerManager.getAllPlayers()
                }
                val scores = players.associateWith { scoreManager.calculatePlayerScore(it) }
                _playerScores.postValue(scores)
                _statusMessage.postValue("Player scores loaded successfully")
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to load player scores: ${e.message}")
            }
        }
    }

    /**
     * Calculate team score based on all team members' scores using the ScoreManager
     */
    fun calculateTeamScore() {
        viewModelScope.launch {
            try {
                val teamPlayers = withContext(Dispatchers.IO) {
                    playerManager.getTeamPlayers()
                }
                val totalTeamScore = teamPlayers.sumOf { scoreManager.calculatePlayerScore(it) }
                _teamScore.postValue(totalTeamScore)
                _statusMessage.postValue("Team score calculated successfully")
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to calculate team score: ${e.message}")
            }
        }
    }

    /**
     * Update an individual player's score and refresh LiveData using the ScoreManager
     */
    fun updatePlayerScore(player: Player, additionalPoints: Int) {
        viewModelScope.launch {
            try {
                // Update score and save player on IO dispatcher
                withContext(Dispatchers.IO) {
                    scoreManager.addPointsToPlayer(player, additionalPoints)
                    playerManager.savePlayer(player)  // Corrected method call
                }

                // Update LiveData on the main thread
                withContext(Dispatchers.Main) {
                    val updatedScores = _playerScores.value?.toMutableMap() ?: mutableMapOf()
                    val newScore = scoreManager.calculatePlayerScore(player)
                    updatedScores[player] = newScore  // Use player.id as key
                    _playerScores.value = updatedScores
                    _statusMessage.value = "${player.name}'s score updated by $additionalPoints points"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _statusMessage.value = "Failed to update player score: ${e.message}"
                }
            }
        }
    }

    /**
     * Calculate and store high scores (top players)
     */
    fun loadHighScores() {
        viewModelScope.launch {
            try {
                val players = withContext(Dispatchers.IO) {
                    playerManager.getAllPlayers()
                }
                val sortedPlayers =
                    players.sortedByDescending { scoreManager.calculatePlayerScore(it) }
                _highScores.postValue(sortedPlayers.take(5))
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to load high scores: ${e.message}")
            }
        }
    }

    /**
     * Reset scores for all players and the team using the ScoreManager.
     */
    /**
     * Reset scores for all players and the team using the ScoreManager.
     */
    fun resetScores() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Retrieve all players
                val players = playerManager.getAllPlayers()

                // Reset each player's score and save them individually
                players.forEach { player ->
                    scoreManager.resetPlayerScores(player) // Reset score for each player
                    playerManager.savePlayer(player) // Save the updated player
                }

                // Refresh the scores on the main thread
                withContext(Dispatchers.Main) {
                    loadPlayerScores()
                    calculateTeamScore()
                    _statusMessage.value = "All player and team scores reset"
                }
            } catch (e: Exception) {
                // Post error message if resetting fails
                withContext(Dispatchers.Main) {
                    _statusMessage.value = "Failed to reset scores: ${e.message}"
                }
            }
        }
    }
}


