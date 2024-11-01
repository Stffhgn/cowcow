package com.example.cow_cow.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository

class LeaderboardViewModel(
    private val playerRepository: PlayerRepository,
    context: Context
) : ViewModel() {

    // LiveData to store the sorted list of players for the leaderboard
    private val _leaderboard = MutableLiveData<List<Player>>()
    val leaderboard: LiveData<List<Player>> get() = _leaderboard

    // Initialize the PlayerController
    private var playerController: PlayerController? = null

    /**
     * Fetches players, sorts them by their total points using PlayerController, and updates the leaderboard.
     */
    fun loadLeaderboard() {
        val players = playerRepository.getPlayers()
        val playerManager = PlayerManager(playerRepository)
        val penaltyManager = PenaltyManager(playerManager)
        // Create a PlayerController instance with the retrieved players
        playerController = PlayerController(players.toMutableList(),penaltyManager)

        // Sort players by their total points using the PlayerController and update the LiveData
        _leaderboard.value = players.sortedByDescending { playerController?.calculateTotalPoints(it) ?: 0 }
    }
}
