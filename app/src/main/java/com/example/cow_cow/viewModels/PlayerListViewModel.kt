package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository

class PlayerListViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    // LiveData to observe the list of players
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    init {
        loadPlayers()
    }

    /**
     * Load players from the repository.
     */
    private fun loadPlayers() {
        val playerList = playerRepository.getPlayers() // Assuming synchronous loading for simplicity
        _players.value = playerList
    }

    /**
     * Sort players by score and update LiveData.
     */
    fun sortPlayersByScore() {
        _players.value = _players.value?.sortedByDescending { it.calculateTotalPoints() }
    }

    /**
     * Add a new player to the list and notify the repository.
     */
    fun addPlayer(player: Player) {
        val updatedPlayers = _players.value?.toMutableList() ?: mutableListOf()
        updatedPlayers.add(player)
        _players.value = updatedPlayers
        playerRepository.savePlayers(updatedPlayers)
    }

    /**
     * Remove a player by ID and update the list.
     */
    fun removePlayerById(playerId: Int) {
        val updatedPlayers = _players.value?.filter { it.id != playerId }
        _players.value = updatedPlayers
        playerRepository.savePlayers(updatedPlayers ?: emptyList())
    }
}
