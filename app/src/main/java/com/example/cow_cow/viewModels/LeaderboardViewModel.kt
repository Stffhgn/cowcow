package com.example.cow_cow.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository

class LeaderboardViewModel(
    private val playerRepository: PlayerRepository,
    private val context: Context
) : ViewModel() {

    // LiveData to store the list of players in the leaderboard
    private val _leaderboard = MutableLiveData<List<Player>>()
    val leaderboard: LiveData<List<Player>> get() = _leaderboard

    /**
     * Load the leaderboard data by fetching all players and sorting by their scores.
     */
    fun loadLeaderboard() {
        val players = playerRepository.getPlayers()
        _leaderboard.value = players.sortedByDescending { it.calculateTotalPoints() }
    }
}
