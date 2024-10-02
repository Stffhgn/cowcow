package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing core player data in the game.
 */
class PlayerViewModel(
    application: Application,
    private val repository: PlayerRepository
) : AndroidViewModel(application) {

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // --- LiveData for Players ---
    private val _players = MutableLiveData<MutableList<Player>>(mutableListOf())
    val players: LiveData<MutableList<Player>> get() = _players

    private val _teamPlayers = MutableLiveData<MutableList<Player>>(mutableListOf())
    val teamPlayers: LiveData<MutableList<Player>> get() = _teamPlayers

    private val _playerSettings = MutableLiveData<Map<Player, PlayerSettings>>()
    val playerSettings: LiveData<Map<Player, PlayerSettings>> get() = _playerSettings

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    private val _selectedPlayer = MutableLiveData<Player>()
    val selectedPlayer: LiveData<Player> get() = _selectedPlayer

    // Define MutableLiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // --- Initialization ---
    init {
        loadPlayers()
        loadTeam()
    }

    // --- Player Management ---

    /**
     * Set the selected player.
     */
    fun setSelectedPlayer(player: Player) {
        _selectedPlayer.value = player
    }

    /**
     * Load players from the repository asynchronously.
     */
    fun loadPlayers() {
        _loading.value = true // Start loading
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val playerList = withContext(Dispatchers.IO) {
                    repository.getPlayers(context)
                }
                _players.value = playerList
            } catch (e: Exception) {
                // Handle the error
            } finally {
                _loading.value = false // Stop loading
            }
        }
    }

    /**
     * Save players to the repository asynchronously.
     */
    private fun savePlayers(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.savePlayers(_players.value ?: emptyList(), context)
            }
        }
    }

    /**
     * Add a new player and save to the repository.
     */
    fun addPlayer(player: Player) {
        _players.value?.apply {
            add(player)
            savePlayers(getApplication())
            _statusMessage.value = "${player.name} added successfully."
        }
    }

    /**
     * Update a player's data and toggle the loading state during the operation.
     */
    fun updatePlayer(player: Player) {
        _loading.value = true // Start loading
        viewModelScope.launch {
            try {
                // Logic to update the player in the repository
                val context = getApplication<Application>().applicationContext
                repository.updatePlayer(player, context)
                // Update LiveData after the player is updated
                _players.value = _players.value?.map {
                    if (it.id == player.id) player else it
                }
            } catch (e: Exception) {
                // Handle the error
            } finally {
                _loading.value = false // Stop loading
            }
        }
    }

    // Clear the error message once it's handled
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Remove player by ID and update the repository.
     */
    fun removePlayerById(playerId: Int) {
        _players.value?.apply {
            removeAll { it.id == playerId }
            savePlayers(getApplication())
            _statusMessage.value = "Player removed."
        }
    }

    /**
     * Update the player's name and save to the repository.
     */
    fun updatePlayerName(playerId: Int, newName: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlayerName(playerId, newName, context)
            withContext(Dispatchers.Main) {
                loadPlayers() // Reload the players to reflect changes in the UI
                _statusMessage.value = "Player name updated to $newName"
            }
        }
    }

    /**
     * Get player by ID.
     */
    fun getPlayerById(id: Int): LiveData<Player?> {
        val playerLiveData = MutableLiveData<Player?>()
        playerLiveData.value = _players.value?.find { it.id == id }
        return playerLiveData
    }

    /**
     * Load player-specific settings such as preferences.
     */
    private fun loadPlayerSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val settingsMap = mutableMapOf<Player, PlayerSettings>()
            _players.value?.forEach { player ->
                val playerSettings = repository.getPlayerSettings(player.id)
                settingsMap[player] = playerSettings
            }
            _playerSettings.postValue(settingsMap)
        }
    }

    /**
     * Update settings for a player and save them to the repository.
     */
    fun updatePlayerSettings(player: Player, settings: PlayerSettings) {
        val context = getApplication<Application>().applicationContext
        repository.savePlayerSettings(player.id, settings, context)
        val updatedSettingsMap = _playerSettings.value?.toMutableMap() ?: mutableMapOf()
        updatedSettingsMap[player] = settings
        _playerSettings.value = updatedSettingsMap
        _statusMessage.value = "${player.name}'s settings updated."
    }

    // --- Team Management ---

    /**
     * Load team from repository asynchronously.
     */
    private fun loadTeam() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val savedTeam = withContext(Dispatchers.IO) {
                repository.getTeam(context)
            }
            _teamPlayers.value = savedTeam.toMutableList()
        }
    }

    /**
     * Save team to repository asynchronously.
     */
    fun saveTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            _teamPlayers.value?.let { teamPlayers ->
                repository.saveTeam(teamPlayers, context)
            }
        }
    }

    /**
     * Add player to team and update repository.
     */
    fun addPlayerToTeam(player: Player) {
        _teamPlayers.value?.apply {
            if (!contains(player)) {
                add(player)
                player.isOnTeam = true
                updatePlayer(player)
                saveTeam()
                _statusMessage.value = "${player.name} added to the team."
            }
        }
    }

    /**
     * Remove player from team and update repository.
     */
    fun removePlayerFromTeam(player: Player) {
        _teamPlayers.value?.apply {
            remove(player)
            player.isOnTeam = false
            updatePlayer(player)
            saveTeam()
            _statusMessage.value = "${player.name} removed from the team."
        }
    }

    /**
     * Check if player is on the team.
     */
    fun isPlayerInTeam(player: Player): Boolean {
        return _teamPlayers.value?.contains(player) ?: false
    }
}
