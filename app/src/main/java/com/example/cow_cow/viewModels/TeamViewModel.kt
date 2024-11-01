package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.managers.TeamManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.google.android.material.color.utilities.Score
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "TeamViewModel"

    // Managers
    private lateinit var playerManager: PlayerManager
    private lateinit var teamManager: TeamManager
    private lateinit var scoreManager: ScoreManager

    // LiveData for the list of all players
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    // LiveData for the team score
    private val _teamScore = MutableLiveData<Int>()
    val teamScore: LiveData<Int> get() = _teamScore

    // LiveData for error handling
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // LiveData for success messages (useful for UI notifications)
    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> get() = _success

    init {
        // Initialize managers
        val context = getApplication<Application>().applicationContext
        val playerRepository = PlayerRepository(context)
        playerManager = PlayerManager(playerRepository)
        loadPlayers()
    }

    /**
     * Load all players from the PlayerManager.
     */
    private fun loadPlayers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading players from PlayerManager.")
                val allPlayers = playerManager.getAllPlayers()
                // Post the list of players to LiveData
                _players.postValue(allPlayers)
                // Update the team score
                updateTeamScore()
            } catch (e: Exception) {
                _error.postValue("Error loading players: ${e.message}")
                Log.e(TAG, "Error loading players: ${e.message}")
            }
        }
    }

    /**
     * Add a player to the team using the TeamManager.
     */
    fun addPlayerToTeam(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding player ${player.name} to the team.")
                teamManager.addPlayerToTeam(player)
                // Refresh the players list
                loadPlayers()
                withContext(Dispatchers.Main) {
                    _success.value = "${player.name} has been added to the team."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Error adding player to team: ${e.message}"
                }
                Log.e(TAG, "Error adding player to team: ${e.message}")
            }
        }
    }

    /**
     * Remove a player from the team using the TeamManager.
     */
    fun removePlayerFromTeam(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Removing player ${player.name} from the team.")
                teamManager.removePlayerFromTeam(player)
                // Refresh the players list
                loadPlayers()
                withContext(Dispatchers.Main) {
                    _success.value = "${player.name} has been removed from the team."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Error removing player from team: ${e.message}"
                }
                Log.e(TAG, "Error removing player from team: ${e.message}")
            }
        }
    }

    /**
     * Update the total score of the team by summing up the scores of all players on the team.
     */
    private fun updateTeamScore() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val team = teamManager.createOrGetTeam() // Get the Team object
                val teamPlayers = team.members           // Access the list of players
                val score = teamPlayers.sumOf { scoreManager.calculatePlayerScore(it) }
                _teamScore.postValue(score)
                Log.d(TAG, "Updated team score: $score")
            } catch (e: Exception) {
                _error.postValue("Error updating team score: ${e.message}")
                Log.e(TAG, "Error updating team score: ${e.message}")
            }
        }
    }


    /**
     * Clear any errors from the ViewModel.
     */
    fun clearError() {
        _error.value = null
        Log.d(TAG, "Cleared error messages.")
    }

    /**
     * Clear success messages.
     */
    fun clearSuccessMessage() {
        _success.value = null
        Log.d(TAG, "Cleared success messages.")
    }

    /**
     * Reset the entire team using the TeamManager.
     */
    fun resetTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Resetting the team.")
                teamManager.resetTeam()
                // Refresh the players list
                loadPlayers()
                withContext(Dispatchers.Main) {
                    _success.value = "The team has been reset."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Error resetting team: ${e.message}"
                }
                Log.e(TAG, "Error resetting team: ${e.message}")
            }
        }
    }
}
