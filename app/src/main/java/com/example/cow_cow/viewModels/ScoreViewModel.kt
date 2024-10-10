package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreViewModel(application: Application, private val playerRepository: PlayerRepository) : AndroidViewModel(application) {

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
     * Load player scores from the repository or local storage
     */
    private fun loadPlayerScores() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val players = withContext(Dispatchers.IO) {
                    playerRepository.getPlayers(context)
                }
                val scores = players.associateWith { it.calculateTotalPoints() }
                _playerScores.value = scores
                _statusMessage.value = "Player scores loaded successfully"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to load player scores: ${e.message}"
            }
        }
    }

    /**
     * Calculate team score based on all team members' scores
     */
    fun calculateTeamScore() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val team = withContext(Dispatchers.IO) {
                    playerRepository.getTeam(context)
                }
                val totalTeamScore = team.sumOf { it.calculateTotalPoints() }
                _teamScore.value = totalTeamScore
                _statusMessage.value = "Team score calculated successfully"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to calculate team score: ${e.message}"
            }
        }
    }

    /**
     * Update an individual player's score and refresh LiveData
     */
    fun updatePlayerScore(player: Player, additionalPoints: Int) {
        val updatedPlayers = _playerScores.value?.toMutableMap() ?: mutableMapOf()
        updatedPlayers[player] = (updatedPlayers[player] ?: 0) + additionalPoints

        _playerScores.value = updatedPlayers
        savePlayers(updatedPlayers.keys.toList())  // Save the updated players to the repository

        _statusMessage.value = "${player.name}'s score updated by $additionalPoints points"
    }

    /**
     * Calculate and store high scores (top players)
     */
    fun loadHighScores() {
        _highScores.value = _playerScores.value?.keys
            ?.sortedByDescending { it.calculateTotalPoints() }
            ?.take(5) // Top 5 players with highest scores
    }

    /**
     * Save players to repository after updating scores
     */
    private fun savePlayers(players: List<Player>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                playerRepository.savePlayers(players, context)
                _statusMessage.postValue("Player scores saved successfully")
            } catch (e: Exception) {
                _statusMessage.postValue("Failed to save player scores: ${e.message}")
            }
        }
    }

    /**
     * Reset scores for all players and team (useful for restarting the game)
     */
    fun resetScores() {
        _playerScores.value?.keys?.forEach { player ->
            player.basePoints = 0
            player.penaltyPoints = 0
            player.bonusPoints = 0
        }
        _teamScore.value = 0
        _statusMessage.value = "All player and team scores reset"
    }
}
