package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PlayerSettings
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(
    application: Application,
    private val repository: PlayerRepository
) : AndroidViewModel(application) {

    // Tag for logging
    private val TAG = "PlayerViewModel"

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for Players from Repository
    val players: LiveData<List<Player>> = repository.playersLiveData

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

    // Initialize loading of players and team
    init {
        loadPlayers()
        loadTeam()
    }

    /**
     * Set the selected player
     */
    fun setSelectedPlayer(player: Player) {
        _selectedPlayer.value = player
    }

    /**
     * Load players from repository
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        _loading.value = true // Start loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getPlayers() // Ensure repository loads data if necessary
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
                repository.updatePlayer(player)
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
     * Remove a player by ID and update the repository
     */
    fun removePlayerById(playerId: String) {
        Log.d(TAG, "Removing player with ID: $playerId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.removePlayerById(playerId)
                Log.d(TAG, "Player removed with ID: $playerId")
                _statusMessage.postValue("Player removed.")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing player: ${e.message}", e)
                _errorMessage.postValue("Error removing player: ${e.message}")
            }
        }
    }

    /**
     * Update player name and save to repository
     */
    fun updatePlayerName(playerId: String, newName: String) {
        Log.d(TAG, "Updating player name to: $newName")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updatePlayerName(playerId, newName)
                Log.d(TAG, "Player name updated to $newName")
                _statusMessage.postValue("Player name updated to $newName")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating player name: ${e.message}", e)
                _errorMessage.postValue("Error updating player name: ${e.message}")
            }
        }
    }

    /**
     * Get player by ID
     */
    fun getPlayerById(id: String): LiveData<Player?> {
        val playerLiveData = MutableLiveData<Player?>()
        playerLiveData.value = players.value?.find { it.id == id }
        return playerLiveData
    }

    /**
     * Load player-specific settings from repository
     */
    private fun loadPlayerSettings() {
        Log.d(TAG, "Loading player settings.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val settingsMap = mutableMapOf<String, PlayerSettings>()
                players.value?.forEach { player ->
                    val playerSettings = repository.getPlayerSettings(player.id)
                    settingsMap[player.id] = playerSettings
                }
                _playerSettings.postValue(settingsMap) // Update LiveData safely
                Log.d(TAG, "Player settings loaded successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading player settings: ${e.message}", e)
                _errorMessage.postValue("Error loading player settings: ${e.message}")
            }
        }
    }

    /**
     * Update player settings and save to repository
     */
    fun updatePlayerSettings(player: Player, settings: PlayerSettings) {
        Log.d(TAG, "Updating settings for player: ${player.name}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.savePlayerSettings(player.id, settings)
                withContext(Dispatchers.Main) {
                    val updatedSettingsMap = _playerSettings.value?.toMutableMap() ?: mutableMapOf()
                    updatedSettingsMap[player.id] = settings
                    _playerSettings.value = updatedSettingsMap
                    _statusMessage.postValue("${player.name}'s settings updated.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating player settings: ${e.message}", e)
                _errorMessage.postValue("Error updating player settings: ${e.message}")
            }
        }
    }

    /**
     * Load team from repository
     */
    private fun loadTeam() {
        Log.d(TAG, "Loading team from repository.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedTeam = repository.getTeam()
                _teamPlayers.postValue(savedTeam) // Update LiveData safely
                Log.d(TAG, "Team loaded successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading team: ${e.message}", e)
                _errorMessage.postValue("Error loading team: ${e.message}")
            }
        }
    }

    /**
     * Save team to repository
     */
    fun saveTeam() {
        Log.d(TAG, "Saving team to repository.")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _teamPlayers.value?.let { teamPlayers ->
                    repository.saveTeam(teamPlayers)
                    Log.d(TAG, "Team saved successfully.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving team: ${e.message}", e)
                _errorMessage.postValue("Error saving team: ${e.message}")
            }
        }
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
     * Check if player is in the team
     */
    fun isPlayerInTeam(player: Player): Boolean {
        return teamPlayers.value?.contains(player) ?: false
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

    /**
     * Clear the current error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
