package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerListViewModel(
    application: Application,
    private val playerRepository: PlayerRepository
) : AndroidViewModel(application) {

    // LiveData to observe the list of players
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    // Logging tag
    private val TAG = "PlayerListViewModel"

    init {
        // Load players when the ViewModel is initialized
        loadPlayers()
    }

    /**
     * Load players from the repository.
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository")
        val context = getApplication<Application>().applicationContext
        val playerList = playerRepository.getPlayers(context)
        _players.value = playerList
        Log.d(TAG, "Loaded ${playerList.size} players.")
    }

    /**
     * Get a player by ID.
     *
     * @param playerId The ID of the player to retrieve.
     * @return The player with the given ID, or null if not found.
     */
    private fun getPlayerById(playerId: String): Player? {
        return _players.value?.find { it.id == playerId }
    }

    /**
     * Sort players by score and update LiveData.
     */
    fun sortPlayersByScore() {
        Log.d(TAG, "Sorting players by total points")
        _players.value = _players.value?.sortedByDescending { it.calculateTotalPoints() }
        Log.d(TAG, "Players sorted by score.")
    }

    /**
     * Add a new player to the list and notify the repository.
     */
    fun addPlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Adding player to repository: ${player.name}")

            val updatedPlayers = _players.value?.toMutableList() ?: mutableListOf()
            updatedPlayers.add(player)

            // Save the updated list in the repository
            val context = getApplication<Application>().applicationContext
            playerRepository.savePlayers(updatedPlayers, context)

            withContext(Dispatchers.Main) {
                // Update LiveData to force UI update
                _players.value = updatedPlayers
                Log.d(TAG, "Player added: ${player.name} - LiveData updated")
            }
        }
    }

    /**
     * Increment the number of games played by a player and update the repository.
     *
     * @param playerId The ID of the player whose games played count will be incremented.
     */
    fun incrementPlayerGamesPlayed(playerId: String) {
        Log.d(TAG, "Incrementing games played for player ID: $playerId")
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val player = getPlayerById(playerId)
            player?.let {
                it.incrementGamesPlayed()
                playerRepository.updatePlayer(it, context)
                withContext(Dispatchers.Main) {
                    loadPlayers()  // Reload players to reflect changes
                    Log.d(TAG, "Games played updated for player ID: $playerId")
                }
            } ?: Log.e(TAG, "Player not found for ID: $playerId")
        }
    }

    /**
     * Update the rank of a player and persist the change.
     *
     * @param playerId The ID of the player to update.
     * @param newRank The new rank for the player.
     */
    fun updatePlayerRank(playerId: String, newRank: String) {
        Log.d(TAG, "Updating rank for player ID: $playerId to rank: $newRank")
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val player = getPlayerById(playerId)
            player?.let {
                it.updateRank(newRank)
                playerRepository.updatePlayer(it, context)
                withContext(Dispatchers.Main) {
                    loadPlayers()  // Reload players to reflect changes
                    Log.d(TAG, "Player rank updated for player ID: $playerId")
                }
            } ?: Log.e(TAG, "Player not found for ID: $playerId")
        }
    }

    /**
     * Remove a player by ID and update the list.
     * This function filters out the player with the specified ID and updates
     * both the LiveData and the repository with the remaining players.
     *
     * @param playerId The ID of the player to remove.
     */
    fun removePlayerById(playerId: String) {
        Log.d(TAG, "Attempting to remove player with ID: $playerId")

        viewModelScope.launch(Dispatchers.IO) {
            // Retrieve the current list of players, and filter out the player with the given ID
            val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
            val updatedPlayers = currentPlayers.filter { it.id != playerId }

            // Check if the player to be removed existed
            if (currentPlayers.size == updatedPlayers.size) {
                Log.w(TAG, "Player with ID: $playerId not found. No removal performed.")
                return@launch
            }

            // Save updated players to repository
            val context = getApplication<Application>().applicationContext
            playerRepository.savePlayers(updatedPlayers, context)

            // Update LiveData on the main thread
            withContext(Dispatchers.Main) {
                _players.value = updatedPlayers
                Log.d(TAG, "Player removed successfully. Total players remaining: ${updatedPlayers.size}")
            }
        }
    }
}
