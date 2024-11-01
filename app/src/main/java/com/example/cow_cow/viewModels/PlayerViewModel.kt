package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    application: Application,
    private val repository: PlayerRepository,
    private val playerManager: PlayerManager,
    private val penaltyManager: PenaltyManager

) : AndroidViewModel(application) {

    // Tag for logging
    private val TAG = "PlayerViewModel"

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for Players from Repository
    val players: LiveData<List<Player>> = repository.playersLiveData

    // Remove duplicate initialization here (as it’s already being injected via constructor)

    // LiveData for Team Players
    private val _teamPlayers = MutableLiveData<List<Player>>()
    val teamPlayers: LiveData<List<Player>> get() = _teamPlayers

    // LiveData for Player Settings
    private val _playerSettings = MutableLiveData<Map<String, PlayerSettings>>()
    val playerSettings: LiveData<Map<String, PlayerSettings>> get() = _playerSettings

    // LiveData for Status Message
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    // LiveData for Selected Player
    private val _selectedPlayer = MutableLiveData<Player>()
    val selectedPlayer: LiveData<Player> get() = _selectedPlayer

    // LiveData for Error Message
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData for Player Achievements
    private val _playerAchievements = MutableLiveData<List<Achievement>>()
    val playerAchievements: LiveData<List<Achievement>> get() = _playerAchievements

    // LiveData for Held Power-Ups
    private val _heldPowerUps = MutableLiveData<List<PowerUp>>()
    val heldPowerUps: LiveData<List<PowerUp>> get() = _heldPowerUps

    // LiveData for Active Penalties
    private val _activePenalties = MutableLiveData<List<Penalty>>()
    val activePenalties: LiveData<List<Penalty>> get() = _activePenalties

    // Initialize loading of players and team
    init {
        loadPlayers()
        loadTeam()
    }

    /**
     * Set the selected player and load their achievements, held power-ups, and active penalties.
     */
    fun setSelectedPlayer(player: Player) {
        _selectedPlayer.value = player
        loadPlayerAchievements(player)
        loadHeldPowerUps(player)
        loadActivePenalties(player)
    }

    /**
     * Load players from repository
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        _loading.value = true // Start loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getPlayers()
                Log.d(TAG, "Players loaded successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading players: ${e.message}", e)
                _errorMessage.postValue("Error loading players: ${e.message}")
            } finally {
                _loading.postValue(false) // Stop loading
            }
        }
    }

    /**
     * Add a new player and save to repository
     */
    fun addPlayer(player: Player) {
        Log.d(TAG, "Adding new player: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addPlayer(player)
                Log.d(TAG, "Player added: ${player.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding player: ${e.message}", e)
                _errorMessage.postValue("Error adding player: ${e.message}")
            }
        }
    }

    /**
     * Update player information in the repository
     */
    fun updatePlayer(player: Player) {
        Log.d(TAG, "Updating player: ${player.name}")
        _loading.value = true // Start loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updatePlayer(player) // Update the player in the repository
                Log.d(TAG, "Player updated successfully: ${player.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating player: ${e.message}", e)
                _errorMessage.postValue("Error updating player: ${e.message}")
            } finally {
                _loading.postValue(false) // Stop loading
            }
        }
    }


    /**
     * Load player-specific achievements from the repository
     */
    private fun loadPlayerAchievements(player: Player) {
        Log.d(TAG, "Loading achievements for player: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val achievements = repository.getPlayerAchievements(player.id)
                _playerAchievements.postValue(achievements)
                Log.d(TAG, "Loaded ${achievements.size} achievements for player: ${player.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading achievements: ${e.message}", e)
                _errorMessage.postValue("Error loading achievements: ${e.message}")
            }
        }
    }

    /**
     * Load held power-ups for the selected player
     */
    private fun loadHeldPowerUps(player: Player) {
        Log.d(TAG, "Loading held power-ups for player: ${player.name}")
        _heldPowerUps.value = player.heldPowerUps
    }

    /**
     * Load active penalties for the selected player
     */
    private fun loadActivePenalties(player: Player) {
        Log.d(TAG, "Loading active penalties for player: ${player.name}")
        _activePenalties.value = player.penalties.filter { it.isActive }
    }

    /**
     * Use a held power-up for the selected player and update the repository
     */
    fun useHeldPowerUp(player: Player, powerUp: PowerUp) {
        Log.d(TAG, "Using power-up '${powerUp.type}' for player: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.usePowerUp(player.id, powerUp)
                loadHeldPowerUps(player) // Refresh the list of held power-ups
                Log.d(TAG, "Power-up '${powerUp.type}' used successfully.")
                _statusMessage.postValue("Power-up '${powerUp.type}' used successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error using power-up: ${e.message}", e)
                _errorMessage.postValue("Error using power-up: ${e.message}")
            }
        }
    }

    /**
     * Apply a penalty to a player
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Requesting to apply penalty '${penalty.name}' to player: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Use PlayerController or PenaltyManager to apply the penalty
                penaltyManager.applyPenalty(player,penalty)
                repository.updatePlayer(player) // Update the player's state in the repository
                _statusMessage.postValue("Penalty '${penalty.name}' applied to ${player.name}.")
                Log.d(TAG, "Penalty '${penalty.name}' applied successfully to ${player.name}.")
            } catch (e: Exception) {
                Log.e(TAG, "Error applying penalty: ${e.message}", e)
                _errorMessage.postValue("Error applying penalty: ${e.message}")
            }
        }
    }

    /**
     * Clear the current error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Add a player to the team
     */
    fun addPlayerToTeam(player: Player) {
        Log.d(TAG, "Adding player to team: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addPlayerToTeam(player)
                withContext(Dispatchers.Main) {
                    _statusMessage.postValue("${player.name} added to the team.")
                    loadTeam() // Reload team after adding
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding player to team: ${e.message}", e)
                _errorMessage.postValue("Error adding player to team: ${e.message}")
            }
        }
    }

    /**
     * Load team players using the PlayerManager, filtering by those marked as being on the team.
     */
    private fun loadTeam() {
        Log.d(TAG, "Loading team players.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Retrieve players who are on the team using PlayerManager.
                val allPlayers = playerManager.getAllPlayers()
                val teamPlayers = allPlayers.filter { it.isOnTeam }

                // Update the LiveData with the filtered team players.
                _teamPlayers.postValue(teamPlayers)
                Log.d(TAG, "Team players loaded: ${teamPlayers.size} players.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading team players: ${e.message}", e)
                _errorMessage.postValue("Error loading team players: ${e.message}")
            }
        }
    }



    /**
     * Remove a player from the team
     */
    fun removePlayerFromTeam(player: Player) {
        Log.d(TAG, "Removing player from team: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removePlayerFromTeam(player)
                withContext(Dispatchers.Main) {
                    _statusMessage.postValue("${player.name} removed from the team.")
                    loadTeam() // Reload team after removal
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing player from team: ${e.message}", e)
                _errorMessage.postValue("Error removing player from team: ${e.message}")
            }
        }
    }

    /**
     * Remove a player by their name.
     */
    fun removePlayerByName(playerName: String) {
        Log.d(TAG, "Removing player with name: $playerName")
        _loading.value = true // Start loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val player = repository.getPlayers().find { it.name == playerName }
                if (player != null) {
                    repository.removePlayerById(player.name)
                    Log.d(TAG, "Player removed successfully: $playerName")
                    _statusMessage.postValue("Player $playerName removed successfully.")
                } else {
                    Log.w(TAG, "Player with name $playerName not found.")
                    _errorMessage.postValue("Player with name $playerName not found.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing player: ${e.message}", e)
                _errorMessage.postValue("Error removing player: ${e.message}")
            } finally {
                _loading.postValue(false) // Stop loading
            }
        }
    }

    /**
     * Resets all players
     */
    fun resetAllPlayers() {
        Log.d(TAG, "Resetting all players.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.resetPlayers()
                Log.d(TAG, "All players have been reset.")
                _statusMessage.postValue("All players have been reset.")
            } catch (e: Exception) {
                Log.e(TAG, "Error resetting players: ${e.message}")
                _errorMessage.postValue("Error resetting players: ${e.message}")
            }
        }
    }
}
