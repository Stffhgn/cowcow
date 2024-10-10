package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Penalty

class PlayerController {

    // Add a new player to the list of players
    fun addPlayer(players: MutableList<Player>, player: Player): Boolean {
        if (players.any { it.id == player.id }) {
            // Prevent adding duplicate player IDs
            return false
        }
        players.add(player)
        return true
    }

    // Remove a player by their ID
    fun removePlayer(players: MutableList<Player>, playerId: String): Boolean {
        return players.removeIf { it.id == playerId }
    }

    // Update the player's score
    fun updatePlayerScore(player: Player, points: Int) {
        player.basePoints += points
    }

    // Apply a penalty to the player
    fun applyPenalty(player: Player, penalty: Penalty) {
        player.applyPenalty(penalty)
    }

    // Assign a player to a team (and mark them as part of the team)
    fun assignToTeam(player: Player, teamId: String) {
        player.isOnTeam = true
        player.teamId = teamId
    }

    // Remove player from a team
    fun removeFromTeam(player: Player) {
        player.isOnTeam = false
        player.teamId = null
    }

    // Check if the player is on a team
    fun isPlayerOnTeam(player: Player): Boolean {
        return player.isOnTeam
    }

    // Apply custom rules to a player (extendable for future use)
    fun applyCustomRule(player: Player, rule: String) {
        // Add logic for applying custom rules to the player (e.g., penalties or bonuses)
    }

    // Reset the player's stats (to be used when resetting the game)
    fun resetPlayerStats(player: Player) {
        player.basePoints = 0
        player.cowCount = 0
        player.churchCount = 0
        player.waterTowerCount = 0
        player.penaltyPoints = 0
        player.achievements.clear()
        player.penalties.clear()
        player.bonusPoints = 0
        player.activePowerUps.clear()
        player.winStreak = 0
        player.objectivesCompleted = 0
        player.gamesPlayed = 0
    }
}