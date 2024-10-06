package com.example.cow_cow.viewModels

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
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

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for Players
    private val _players = MutableLiveData<MutableList<Player>>(mutableListOf())
    val players: LiveData<MutableList<Player>> get() = _players

    // LiveData for Team Players
    private val _teamPlayers = MutableLiveData<MutableList<Player>>(mutableListOf())
    val teamPlayers: LiveData<MutableList<Player>> get() = _teamPlayers

    // LiveData for Player Settings
    private val _playerSettings = MutableLiveData<Map<Player, PlayerSettings>>()
    val playerSettings: LiveData<Map<Player, PlayerSettings>> get() = _playerSettings

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
     * Load players from repository asynchronously
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        _loading.value = true // Start loading
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val playerList = withContext(Dispatchers.IO) {
                    Log.d(TAG, "Coroutine started to fetch players from SharedPreferences")
                    repository.getPlayers(context)
                }
                _players.postValue(playerList.toMutableList()) // Update LiveData safely
                Log.d(TAG, "Players loaded successfully: ${playerList.size} players found.")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading players: ${e.message}", e)
            } finally {
                _loading.postValue(false) // Stop loading
                Log.d(TAG, "Coroutine completed or canceled for loading players")
            }
        }
    }

    /**
     * Save players to repository asynchronously
     */
    private fun savePlayers(context: Context) {
        Log.d(TAG, "Saving players to repository.")
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlayers(_players.value ?: emptyList(), context)
            Log.d(TAG, "Players saved successfully.")
        }
    }

    /**
     * Add a new player and save to repository
     */
    fun addPlayer(player: Player) {
        _players.value?.apply {
            add(player)
            savePlayers(getApplication()) // Save players after adding
            _statusMessage.postValue("${player.name} added successfully.") // Post status message
            Log.d(TAG, "Player added: ${player.name}")
        }
    }

    /**
     * Update player information in the repository
     */
    fun updatePlayer(player: Player) {
        Log.d(TAG, "Updating player: ${player.name}")
        _loading.value = true // Start loading
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                repository.updatePlayer(player, context)

                _players.value = _players.value?.map {
                    if (it.id == player.id) player else it
                }?.toMutableList() // Update LiveData safely

                Log.d(TAG, "Player updated successfully: ${player.name}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating player: ${e.message}", e)
            } finally {
                _loading.postValue(false) // Stop loading
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
     * Remove a player by ID and update the repository
     */
    fun removePlayerById(playerId: Int) {
        _players.value?.apply {
            removeAll { it.id == playerId }
            savePlayers(getApplication()) // Save players after removal
            _statusMessage.postValue("Player removed.") // Post status message
            Log.d(TAG, "Player removed with ID: $playerId")
        }
    }

    /**
     * Update player name and save to repository
     */
    fun updatePlayerName(playerId: Int, newName: String, context: Context) {
        Log.d(TAG, "Updating player name to: $newName")
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlayerName(playerId, newName, context)
            withContext(Dispatchers.Main) {
                loadPlayers() // Reload players after update
                _statusMessage.postValue("Player name updated to $newName") // Post status message
            }
        }
    }

    /**
     * Get player by ID
     */
    fun getPlayerById(id: Int): LiveData<Player?> {
        val playerLiveData = MutableLiveData<Player?>()
        playerLiveData.value = _players.value?.find { it.id == id }
        return playerLiveData
    }

    /**
     * Load player-specific settings from repository
     */
    private fun loadPlayerSettings() {
        Log.d(TAG, "Loading player settings.")
        viewModelScope.launch(Dispatchers.IO) {
            val settingsMap = mutableMapOf<Player, PlayerSettings>()
            val context = getApplication<Application>().applicationContext

            _players.value?.forEach { player ->
                val playerSettings = repository.getPlayerSettings(context, player.id)
                settingsMap[player] = playerSettings
            }

            _playerSettings.postValue(settingsMap) // Update LiveData safely
            Log.d(TAG, "Player settings loaded successfully.")
        }
    }

    /**
     * Update player settings and save to repository
     */
    fun updatePlayerSettings(player: Player, settings: PlayerSettings) {
        Log.d(TAG, "Updating settings for player: ${player.name}")
        val context = getApplication<Application>().applicationContext
        repository.savePlayerSettings(player.id, settings, context)
        val updatedSettingsMap = _playerSettings.value?.toMutableMap() ?: mutableMapOf()
        updatedSettingsMap[player] = settings
        _playerSettings.value = updatedSettingsMap
        _statusMessage.postValue("${player.name}'s settings updated.") // Post status message
    }

    /**
     * Load team from repository asynchronously
     */
    private fun loadTeam() {
        Log.d(TAG, "Loading team from repository.")
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val savedTeam = withContext(Dispatchers.IO) {
                repository.getTeam(context)
            }
            _teamPlayers.postValue(savedTeam.toMutableList()) // Update LiveData safely
            Log.d(TAG, "Team loaded successfully.")
        }
    }

    /**
     * Save team to repository asynchronously
     */
    fun saveTeam() {
        Log.d(TAG, "Saving team to repository.")
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            _teamPlayers.value?.let { teamPlayers ->
                repository.saveTeam(teamPlayers, context)
                Log.d(TAG, "Team saved successfully.")
            }
        }
    }

    /**
     * Add a player to the team
     */
    fun addPlayerToTeam(player: Player) {
        _teamPlayers.value?.apply {
            if (!contains(player)) {
                add(player)
                player.isOnTeam = true
                updatePlayer(player) // Update player after adding to team
                saveTeam() // Save team after update
                _statusMessage.postValue("${player.name} added to the team.") // Post status message
                Log.d(TAG, "Player added to the team: ${player.name}")
            }
        }
    }

    /**
     * Remove a player from the team
     */
    fun removePlayerFromTeam(player: Player) {
        _teamPlayers.value?.apply {
            remove(player)
            player.isOnTeam = false
            updatePlayer(player) // Update player after removing from team
            saveTeam() // Save team after update
            _statusMessage.postValue("${player.name} removed from the team.") // Post status message
            Log.d(TAG, "Player removed from the team: ${player.name}")
        }
    }

    /**
     * Check if player is in the team
     */
    fun isPlayerInTeam(player: Player): Boolean {
        return _teamPlayers.value?.contains(player) ?: false
    }
}
