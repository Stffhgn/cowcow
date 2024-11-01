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

    private val playerManager: PlayerManager = PlayerManager(playerRepository)
    private val scoreManager: ScoreManager = ScoreManager(playerManager)

    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    private val TAG = "PlayerListViewModel"

    init {
        loadPlayers()
    }

    /**
     * Load players from the repository.
     */
    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository")

        viewModelScope.launch {
            val playerList = withContext(Dispatchers.IO) {
                playerRepository.getPlayers()
            }

            _players.postValue(playerList)
            Log.d(TAG, "Loaded ${playerList.size} players.")
        }
    }

    /**
     * Get a player by ID.
     */
    private fun getPlayerById(playerId: String): Player? {
        return _players.value?.find { it.id == playerId }
    }

    /**
     * Sort players by score and update LiveData using ScoreManager for point calculation.
     */
    fun sortPlayersByScore() {
        Log.d(TAG, "Sorting players by total points")

        _players.value = _players.value?.sortedByDescending { player ->
            scoreManager.calculatePlayerScore(player)
        }

        Log.d(TAG, "Players sorted by score.")
    }

    /**
     * Add a new player using PlayerManager and save them individually.
     */
    fun addPlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Adding player through PlayerManager: ${player.name}")

            playerManager.addPlayer(player)
            playerManager.savePlayer(player)  // Save the new player individually
            val updatedPlayers = playerManager.getAllPlayers()

            _players.postValue(updatedPlayers)
            Log.d(TAG, "Player added: ${player.name} - LiveData updated")
        }
    }

    /**
     * Increment the number of games played by a player and save them individually.
     */
    fun incrementPlayerGamesPlayed(playerId: String) {
        Log.d(TAG, "Incrementing games played for player ID: $playerId")
        viewModelScope.launch(Dispatchers.IO) {
            val player = getPlayerById(playerId)

            player?.let {
                it.gamesPlayed += 1
                playerManager.savePlayer(it)  // Save the updated player

                withContext(Dispatchers.Main) {
                    loadPlayers()
                    Log.d(TAG, "Games played updated for player ID: $playerId to ${it.gamesPlayed}")
                }
            } ?: Log.e(TAG, "Player not found for ID: $playerId")
        }
    }

    /**
     * Update the rank of a player based on their points and save them individually.
     */
    fun updatePlayerRank(playerId: String) {
        Log.d(TAG, "Updating rank for player ID: $playerId based on points.")
        viewModelScope.launch(Dispatchers.IO) {
            val player = getPlayerById(playerId)

            player?.let {
                val totalPoints = scoreManager.calculatePlayerScore(it)
                val newRank = calculateRankBasedOnPoints(totalPoints)

                if (it.rank != newRank) {
                    it.rank = newRank
                    playerManager.savePlayer(it)  // Save the updated player

                    withContext(Dispatchers.Main) {
                        loadPlayers()
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
     */
    fun removePlayerById(playerId: String) {
        Log.d(TAG, "Attempting to remove player with ID: $playerId")

        viewModelScope.launch(Dispatchers.IO) {
            val playerToRemove = getPlayerById(playerId)

            if (playerToRemove != null) {
                playerManager.removePlayer(playerToRemove)  // Save the deletion for the specific player
                val updatedPlayers = playerManager.getAllPlayers()

                withContext(Dispatchers.Main) {
                    _players.value = updatedPlayers
                    Log.d(TAG, "Player removed successfully. Total players remaining: ${updatedPlayers.size}")
                }
            } else {
                Log.w(TAG, "Player with ID: $playerId not found. No removal performed.")
            }
        }
    }
}
