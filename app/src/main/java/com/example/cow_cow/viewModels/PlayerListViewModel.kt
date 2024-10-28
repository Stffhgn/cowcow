package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.enums.RankType
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
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

    // Now you can instantiate PlayerManager
    private val playerManager = PlayerManager(playerRepository)

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

        // Launch a coroutine to load players on a background thread
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val playerList = withContext(Dispatchers.IO) {
                playerRepository.getPlayers()  // Fetch players in background
            }

            _players.postValue(playerList)  // Update LiveData on the main thread
            Log.d(TAG, "Loaded ${playerList.size} players.")
        }
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
     * Sort players by score and update LiveData using the ScoreManager for point calculation.
     */
    fun sortPlayersByScore() {
        Log.d(TAG, "Sorting players by total points")

        // Use the ScoreManager to calculate and sort the players' scores.
        _players.value = _players.value?.sortedByDescending { player ->
            ScoreManager.calculatePlayerScore(player)
        }

        Log.d(TAG, "Players sorted by score.")
    }


    /**
     * Add a new player using the PlayerManager and update LiveData.
     */
    fun addPlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Adding player through PlayerManager: ${player.name}")

            // Use PlayerManager to add the player
            playerManager.addPlayer(player)

            // Retrieve the updated list of players from PlayerManager
            val updatedPlayers = playerManager.getAllPlayers()

            // Update LiveData with the new list on the main thread
            _players.postValue(updatedPlayers)
            Log.d(TAG, "Player added: ${player.name} - LiveData updated")
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
                // Increment the games played count.
                it.gamesPlayed += 1

                // Update the player in the repository.
                playerRepository.updatePlayer(it)

                // Switch back to the main thread to update the UI.
                withContext(Dispatchers.Main) {
                    loadPlayers()  // Reload players to reflect changes.
                    Log.d(TAG, "Games played updated for player ID: $playerId to ${it.gamesPlayed}")
                }
            } ?: Log.e(TAG, "Player not found for ID: $playerId")
        }
    }

    /**
     * Update the rank of a player based on their points and persist the change.
     *
     * @param playerId The ID of the player to update.
     */
    fun updatePlayerRank(playerId: String) {
        Log.d(TAG, "Updating rank for player ID: $playerId based on points.")
        viewModelScope.launch(Dispatchers.IO) {
            val player = getPlayerById(playerId)

            player?.let {
                // Use the ScoreManager to calculate the player's total points.
                val totalPoints = ScoreManager.calculatePlayerScore(it)

                // Calculate the new rank based on the total points.
                val newRank = calculateRankBasedOnPoints(totalPoints)

                // Update the player's rank if it has changed.
                if (it.rank != newRank) {
                    it.rank = newRank
                    playerRepository.updatePlayer(it)

                    withContext(Dispatchers.Main) {
                        loadPlayers()  // Reload players to reflect changes.
                        Log.d(TAG, "Player rank updated for player ID: $playerId to rank: ${it.rank}")
                    }
                } else {
                    Log.d(TAG, "Player rank remains the same: ${it.rank}")
                }
            } ?: Log.e(TAG, "Player not found for ID: $playerId")
        }
    }

    /**
     * Calculate the rank based on the player's total points.
     *
     * @param points The total points of the player.
     * @return The RankType corresponding to the player's points.
     */
    private fun calculateRankBasedOnPoints(points: Int): RankType {
        return when {
            points <= 50 -> RankType.BEGINNER
            points in 51..200 -> RankType.INTERMEDIATE
            points in 201..500 -> RankType.ADVANCED
            else -> RankType.EXPERT
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
            playerManager.savePlayers(updatedPlayers)

            // Update LiveData on the main thread
            withContext(Dispatchers.Main) {
                _players.value = updatedPlayers
                Log.d(TAG, "Player removed successfully. Total players remaining: ${updatedPlayers.size}")
            }
        }
    }
}
