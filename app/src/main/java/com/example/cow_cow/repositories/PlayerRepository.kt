package com.example.cow_cow.repositories

import android.content.Context
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils

class PlayerRepository {

    fun getPlayers(context: Context): List<Player> {
        // This method may need a Context if you're loading players from storage
        // Adjust accordingly if needed
        return DataUtils.loadPlayers(context) // Placeholder
    }

    fun updatePlayer(player: Player, context: Context) {
        // Convert the list of players to a MutableList
        val players = DataUtils.loadPlayers(context).toMutableList()

        // Find the index of the player to update
        val index = players.indexOfFirst { it.id == player.id }

        // If the player is found, update the player and save the list
        if (index != -1) {
            players[index] = player
            DataUtils.savePlayers(context, players)
        }
    }

    fun deletePlayer(player: Player, context: Context): Player? {
        val players = DataUtils.loadPlayers(context).toMutableList()

        // Find and remove the player by ID, returning the player if found
        val isRemoved = players.removeAll { it.id == player.id }

        // Save the updated list back to storage
        if (isRemoved) {
            DataUtils.savePlayers(context, players)
            return player // Return the deleted player
        }

        return null // Return null if the player was not found or deleted
    }

    fun savePlayers(players: List<Player>, context: Context) {
        DataUtils.savePlayers(context, players)
    }
}