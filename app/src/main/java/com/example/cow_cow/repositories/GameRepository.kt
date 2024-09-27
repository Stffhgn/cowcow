package com.example.cow_cow.repositories

import com.example.cow_cow.models.Player

class GameRepository {

    // Initialize a list of players
    private val players = mutableListOf<Player>()  // Empty list of players at the start

    // This method returns the list of players
    fun getPlayers(): List<Player> {
        return players  // Return the list of players
    }

    // You can also add methods to add, remove, or update players if needed
    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    fun clearPlayers() {
        players.clear()  // Clear the list of players
    }
}
