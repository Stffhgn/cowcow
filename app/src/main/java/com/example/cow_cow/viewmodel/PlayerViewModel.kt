package com.example.cow_cow.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.DataUtils

class PlayerViewModel(private val repository: PlayerRepository, private val context: Context) : ViewModel() {

    private val _players = MutableLiveData<MutableList<Player>>(mutableListOf())
    val players: LiveData<MutableList<Player>> get() = _players

    private val team = mutableListOf<Player>() // Store players who are in the team

    init {
        // Initialize players list by loading players from the repository with context
        _players.value = repository.getPlayers(context).toMutableList()
    }

    fun addPlayer(player: Player, context: Context) {
        val updatedPlayers = _players.value ?: mutableListOf()
        updatedPlayers.add(player)
        _players.value = updatedPlayers
        repository.savePlayers(updatedPlayers, context)
    }

    fun setPlayers(players: MutableList<Player>) {
        _players.value = players
    }

    fun updatePlayer(player: Player, context: Context) {
        val playerList = _players.value ?: return
        val index = playerList.indexOfFirst { it.id == player.id }
        if (index != -1) {
            playerList[index] = player
            _players.value = playerList
            repository.updatePlayer(player, context) // Persist the update
        }
    }

    fun deletePlayer(player: Player, context: Context): Player? {
        val playerList = _players.value ?: return null

        // Call the repository's delete function and return the deleted player if successful
        val deletedPlayer = repository.deletePlayer(player, context)

        // Update the LiveData if the player was successfully deleted
        if (deletedPlayer != null) {
            playerList.removeAll { it.id == deletedPlayer.id }
            _players.value = playerList
        }

        return deletedPlayer
    }

    // Define the getPlayerById method
    fun getPlayerById(id: Int): Player? {
        return _players.value?.find { it.id == id }
    }

    // Load the team from storage if it's not already loaded
    fun loadTeam(context: Context) {
        if (team.isEmpty()) {
            val savedTeam = DataUtils.loadTeam(context) // Load team data
            team.addAll(savedTeam) // Add all saved players to the team list
        }
    }

    fun isPlayerInTeam(player: Player): Boolean {
        // Check if player is in the loaded team
        return team.contains(player)
    }

    // Add a player to the team
    fun addPlayerToTeam(player: Player) {
        if (!team.contains(player)) {
            team.add(player)
        }
    }

    // Remove a player from the team
    fun removePlayerFromTeam(player: Player) {
        team.remove(player)
    }

    // Get all players in the team
    fun getTeamPlayers(): List<Player> {
        return team
    }

    fun loadPlayers(context: Context) {
        val loadedPlayers = repository.getPlayers(context) // Fetch players from repository
        _players.value = loadedPlayers.toMutableList() // Update LiveData with loaded players
    }

    // Remove player from both players list and team
    fun removePlayer(player: Player, context: Context) {
        val playerList = _players.value ?: return
        // Remove the player from the main list
        playerList.removeAll { it.id == player.id }
        _players.value = playerList

        // Persist the updated player list in storage
        repository.savePlayers(playerList, context)
    }
}
