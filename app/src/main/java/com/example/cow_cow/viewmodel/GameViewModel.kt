package com.example.cow_cow.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils

class GameViewModel : ViewModel() {

    val players: MutableList<Player> = mutableListOf()
    val team: MutableList<Player> = mutableListOf()

    // Player Management

    // Add a player to the players list
    fun addPlayer(player: Player) {
        if (!players.contains(player)) {
            players.add(player)
        }
    }

    // Remove a player from the players list and team
    fun removePlayer(player: Player) {
        players.remove(player)
        team.remove(player)
    }

    // Get a player by ID
    fun getPlayerById(id: Int): Player? {
        return players.find { it.id == id }
    }

    // Team Management

    // Load team from storage
    fun loadTeam(context: Context) {
        if (team.isEmpty()) {
            val savedTeam = DataUtils.loadTeam(context)
            // Map saved team players to existing player instances
            for (savedPlayer in savedTeam) {
                val existingPlayer = players.find { it.id == savedPlayer.id }
                if (existingPlayer != null) {
                    team.add(existingPlayer)
                }
            }
        }
    }
    // Clear the Team if needed
    fun clearTeam(context: Context) {
        team.clear()
        saveTeam(context)
    }
    // Save team to storage
    fun saveTeam(context: Context) {
        DataUtils.saveTeam(context, team)
    }

    // Toggle a player's membership in the team
    fun togglePlayerInTeam(player: Player, context: Context) {
        if (team.contains(player)) {
            team.remove(player)
        } else {
            team.add(player)
        }
        saveTeam(context) // Save team whenever it changes
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

    // Check if a player is in the team
    fun isPlayerInTeam(player: Player): Boolean {
        return team.contains(player)
    }

    // Score Management

    // Increment player's counts based on action
    fun incrementPlayerCount(player: Player, action: String) {
        when (action) {
            "Cow" -> player.cowCount += 1
            "Church" -> player.churchCount += 1
            "Water Tower" -> player.waterTowerCount += 1
        }
    }

    // Utility Methods

    // Calculate team score
    fun calculateTeamScore(): Int {
        return team.sumBy { it.totalScore }
    }

    // Get list of players not in the team
    fun getIndividualPlayers(): List<Player> {
        return players.filter { !team.contains(it) }
    }

    // Data Persistence (Optional)

    // Load players from storage
    fun loadPlayers(context: Context) {
        if (players.isEmpty()) {
            players.addAll(DataUtils.loadPlayers(context))
        }
    }

    // Save players to storage
    fun savePlayers(context: Context) {
        DataUtils.savePlayers(context, players)
    }
}
