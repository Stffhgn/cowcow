package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.utils.TeamUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.lifecycle.viewModelScope

class TeamViewModel(application: Application, private val repository: TeamRepository) :
    AndroidViewModel(application) {

    private val TAG = "TeamViewModel"

    // LiveData for the current team
    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team> get() = _team

    // LiveData for the team score
    private val _teamScore = MutableLiveData<Int>()
    val teamScore: LiveData<Int> get() = _teamScore

    // LiveData for error handling
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // LiveData for success messages (useful for UI notifications)
    private val _success = MutableLiveData<String>()
    val success: LiveData<String> get() = _success

    init {
        loadTeam()
    }

    /**
     * Load the current team from the repository.
     */
    private fun loadTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading team from repository.")
                val loadedTeam = repository.getTeam()
                _team.postValue(loadedTeam)
                updateTeamScore()
            } catch (e: Exception) {
                _error.postValue("Error loading team: ${e.message}")
                Log.e(TAG, "Error loading team: ${e.message}")
            }
        }
    }

    /**
     * Add a player to the team and update the team score.
     */
    fun addPlayerToTeam(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Adding player ${player.name} to the team.")
                val currentTeam = _team.value ?: Team(id = 1, name = "Team", members = mutableListOf())
                if (!currentTeam.members.contains(player)) {
                    // Use TeamUtils to handle the logic
                    TeamUtils.addPlayerToTeam(player, currentTeam)
                    repository.saveTeam(currentTeam) // This should save a single team
                    _team.postValue(currentTeam)
                    updateTeamScore()
                    _success.postValue("${player.name} has been added to the team.")
                } else {
                    _error.postValue("${player.name} is already in the team.")
                    Log.w(TAG, "Player ${player.name} is already in the team.")
                }
            } catch (e: Exception) {
                _error.postValue("Error adding player to team: ${e.message}")
                Log.e(TAG, "Error adding player to team: ${e.message}")
            }
        }
    }

    /**
     * Remove a player from the team and update the team score.
     */
    fun removePlayerFromTeam(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Removing player ${player.name} from the team.")
                val currentTeam = _team.value
                if (currentTeam != null && currentTeam.members.contains(player)) {
                    // Use TeamUtils to handle the logic
                    TeamUtils.removePlayerFromTeam(player, currentTeam)
                    repository.saveTeam(currentTeam) // This should save a single team
                    _team.postValue(currentTeam)
                    updateTeamScore()
                    _success.postValue("${player.name} has been removed from the team.")
                } else {
                    _error.postValue("${player.name} is not in the team.")
                    Log.w(TAG, "Player ${player.name} is not in the team.")
                }
            } catch (e: Exception) {
                _error.postValue("Error removing player from team: ${e.message}")
                Log.e(TAG, "Error removing player from team: ${e.message}")
            }
        }
    }

    /**
     * Update the total score of the team by summing up the individual player scores.
     */
    private fun updateTeamScore() {
        _team.value?.let { team ->
            val score = team.members.sumOf { it.calculateTotalPoints() }
            _teamScore.postValue(score)
            Log.d(TAG, "Updated team score: $score")
        }
    }

    /**
     * Clear any errors from the ViewModel (e.g., after showing an error to the user).
     */
    fun clearError() {
        _error.value = null
        Log.d(TAG, "Cleared error messages.")
    }

    /**
     * Clear success messages (to avoid repeated notifications).
     */
    fun clearSuccessMessage() {
        _success.value = null
        Log.d(TAG, "Cleared success messages.")
    }

    /**
     * Reset the entire team by removing all players.
     */
    fun resetTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Resetting the team.")
                val newTeam = Team(id = 1, name = "Team", members = mutableListOf())
                repository.saveTeam(newTeam) // This should save a single team
                _team.postValue(newTeam)
                updateTeamScore()
                _success.postValue("The team has been reset.")
            } catch (e: Exception) {
                _error.postValue("Error resetting team: ${e.message}")
                Log.e(TAG, "Error resetting team: ${e.message}")
            }
        }
    }
}
