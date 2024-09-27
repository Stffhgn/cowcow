package com.example.cow_cow.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.DataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing player data in the game.
 */
class PlayerViewModel(
    application: Application,
    private val repository: PlayerRepository
) : AndroidViewModel(application) {

    // LiveData for all players
    private val _players = MutableLiveData<MutableList<Player>>(mutableListOf())
    val players: LiveData<MutableList<Player>> get() = _players

    // LiveData for team players
    private val _teamPlayers = MutableLiveData<MutableList<Player>>(mutableListOf())
    val teamPlayers: LiveData<MutableList<Player>> get() = _teamPlayers

    init {
        // Load players and team
        loadPlayers()
        loadTeam()
    }

    /**
     * Load players from the repository asynchronously
     */
    fun loadPlayers() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val loadedPlayers = withContext(Dispatchers.IO) {
                repository.getPlayers(context)
            }
            _players.value = loadedPlayers.toMutableList()
        }
    }

    /**
     * Save players to the repository asynchronously
     */
    fun savePlayers(players: MutableList<Player>, context: Context) {
        // Save players to persistent storage using the context
    }

    /**
     * Add a new player and save to repository
     */
    fun addPlayer(player: Player) {
        val updatedPlayers = _players.value ?: mutableListOf()
        updatedPlayers.add(player)
        _players.value = updatedPlayers

        // Save to repository asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            repository.savePlayers(updatedPlayers, context) // Pass both players and context.
        }
    }

    /**
     * Update an existing player and save to repository
     */
    fun updatePlayer(player: Player) {
        val updatedPlayers = _players.value?.map { if (it.id == player.id) player else it }?.toMutableList()
        _players.value = updatedPlayers

        // Update repository asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            repository.updatePlayer(player, context)
        }
    }

    /**
     * Remove player by ID and update repository
     */
    fun removePlayerById(playerId: Int) {
        val updatedPlayers = _players.value?.filter { it.id != playerId }?.toMutableList()
        _players.value = updatedPlayers

        // Update repository asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            repository.removePlayerById(playerId, context)
        }
    }

    /**
     * Load team from DataUtils asynchronously
     */
    fun loadTeam() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val savedTeam = withContext(Dispatchers.IO) {
                DataUtils.loadTeam(context)
            }
            _teamPlayers.value = savedTeam.toMutableList()
        }
    }

    /**
     * Save team to DataUtils asynchronously
     */
    fun saveTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            _teamPlayers.value?.let { teamPlayers ->
                DataUtils.saveTeam(context, teamPlayers)  // Call the DataUtils function and pass the correct parameters
            }
        }
    }

    /**
     * Set the entire players list and update LiveData.
     */
    fun setPlayers(players: MutableList<Player>) {
        _players.value = players
    }

    /**
     * Add player to team and update repository
     */
    fun addPlayerToTeam(player: Player) {
        val teamList = _teamPlayers.value ?: mutableListOf()
        if (!teamList.contains(player)) {
            teamList.add(player)
            _teamPlayers.value = teamList
            player.isOnTeam = true
            updatePlayer(player)
            saveTeam()
        }
    }

    /**
     * Remove player from team and update repository
     */
    fun removePlayerFromTeam(player: Player) {
        val teamList = _teamPlayers.value ?: mutableListOf()
        if (teamList.contains(player)) {
            teamList.remove(player)
            _teamPlayers.value = teamList
            player.isOnTeam = false
            updatePlayer(player)
            saveTeam()
        }
    }

    /**
     * Check if player is on team
     */
    fun isPlayerInTeam(player: Player): Boolean {
        val teamList = _teamPlayers.value ?: return false
        return teamList.contains(player)
    }

    /**
     * Get player by ID
     */
    fun getPlayerById(id: Int): Player? {
        return _players.value?.find { it.id == id }
    }
}
