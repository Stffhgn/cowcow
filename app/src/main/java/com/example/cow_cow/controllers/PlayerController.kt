package com.example.cow_cow.controllers

import android.content.ContentValues.TAG
import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PowerUpManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.ScavengerHuntItem

class PlayerController(private val players: MutableList<Player>) {

    // Add a new player to the list of players
    fun addPlayer(player: Player): Boolean {
        return if (players.any { it.id == player.id }) {
            Log.d(TAG, "Attempted to add player with duplicate ID: ${player.id}")
            false
        } else {
            players.add(player)
            Log.d(TAG, "Player ${player.name} with ID ${player.id} added successfully.")
            true
        }
    }

    // Remove a player by their ID
    fun removePlayer(playerId: String): Boolean {
        val result = players.removeIf { it.id == playerId }
        Log.d(TAG, if (result) "Player with ID $playerId removed." else "Player with ID $playerId not found.")
        return result
    }

    // Update the player's score
    fun updatePlayerScore(player: Player, points: Int) {
        Log.d(TAG, "Updating score for player ${player.name} by $points points.")
        player.basePoints += points
        Log.d(TAG, "New score for player ${player.name}: ${player.basePoints} base points.")
    }

    // Apply a penalty to the player using the PenaltyManager
    fun applyPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying penalty ${penalty.name} to player ${player.name} using PenaltyManager.")
        PenaltyManager.applyPenalty(player, penalty)
    }

    // Assign a player to a team (and mark them as part of the team)
    fun assignToTeam(player: Player, teamId: String) {
        player.isOnTeam = true
        player.teamId = teamId
        Log.d(TAG, "Player ${player.name} assigned to team $teamId.")
    }

    // Remove player from a team
    fun removeFromTeam(player: Player) {
        Log.d(TAG, "Removing player ${player.name} from their team.")
        player.isOnTeam = false
        player.teamId = null
    }

    // Check if the player is on a team
    fun isPlayerOnTeam(player: Player): Boolean {
        val isOnTeam = player.isOnTeam
        Log.d(TAG, "Player ${player.name} is ${if (isOnTeam) "on" else "not on"} a team.")
        return isOnTeam
    }

    // Apply custom rules to a player (extendable for future use)
    fun applyCustomRule(player: Player, rule: String) {
        Log.d(TAG, "Applying custom rule '$rule' to player ${player.name}.")
        // Add logic for applying custom rules to the player (e.g., penalties or bonuses)
    }

    // Reset the player's stats (to be used when resetting the game)
    fun resetPlayerStats(player: Player) {
        Log.d(TAG, "Resetting stats for player ${player.name}.")
        player.basePoints = 0
        player.cowCount = 0
        player.churchCount = 0
        player.waterTowerCount = 0
        player.penaltyPoints = 0
        player.achievements.clear()
        player.penalties.clear()
        player.bonusPoints = 0
        player.winStreak = 0
        player.objectivesCompleted = 0
        player.gamesPlayed = 0
        player.activePowerUps.clear()
        Log.d(TAG, "Player ${player.name} stats reset.")
    }

    // --- Scavenger Hunt Related Logic ---

    // Check if the player can access a given scavenger hunt item.
    fun canPlayerAccessItem(player: Player, item: ScavengerHuntItem): Boolean {
        val canAccess = when {
            item.isFree -> true
            item.isPremium && player.premiumAccess -> true
            item.isPurchased && player.purchasedItems.contains(item) -> true
            else -> false
        }
        Log.d(TAG, "Player ${player.name} can ${if (canAccess) "" else "not "}access item: ${item.name}.")
        return canAccess
    }

    // Add a purchased scavenger hunt item to the player's inventory.
    fun purchaseScavengerHuntItem(player: Player, item: ScavengerHuntItem): Boolean {
        return if (item.isPurchased && !player.purchasedItems.contains(item)) {
            player.purchasedItems.add(item)
            Log.d(TAG, "Player ${player.name} purchased item: ${item.name}.")
            true
        } else {
            Log.d(TAG, "Player ${player.name} already owns or cannot purchase item: ${item.name}.")
            false
        }
    }

    // Add an object (e.g., Cow, Church, Water Tower) to the player's list of called objects.
    fun addObjectCalled(player: Player, objectType: String) {
        player.objectsCalled.add(objectType)
        val pointsAwarded = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }
        player.basePoints += pointsAwarded
        Log.d(TAG, "Player ${player.name} called $objectType and was awarded $pointsAwarded points.")
    }

    // Calculate the total points for the player, including base points, bonuses, and penalties.
    fun calculateTotalPoints(player: Player): Int {
        var totalPoints = player.basePoints + player.bonusPoints
        Log.d(TAG, "Calculating total points for player ${player.name}. Base: ${player.basePoints}, Bonus: ${player.bonusPoints}.")

        // Apply penalties if the player does not have immunity.
        if (!PowerUpManager.hasImmunity(player)) {
            totalPoints -= player.penaltyPoints
            Log.d(TAG, "Penalties applied for ${player.name}: ${player.penaltyPoints}. Total points after penalties: $totalPoints.")
        } else {
            Log.d(TAG, "${player.name} has active immunity. Penalties are not applied.")
        }

        // Apply effects of active power-ups using the PowerUpManager.
        totalPoints = player.activePowerUps
            .filter { it.isActive && !it.hasExpired(System.currentTimeMillis()) }
            .fold(totalPoints) { currentPoints, powerUp ->
                PowerUpManager.applyEffectToPoints(currentPoints, player, powerUp)
            }
        Log.d(TAG, "Total points after applying power-up effects for ${player.name}: $totalPoints.")

        // Ensure total points do not fall below zero.
        if (totalPoints < 0) {
            totalPoints = 0
            Log.d(TAG, "Total points adjusted to zero for ${player.name} due to negative balance.")
        }

        Log.d(TAG, "Final calculated total points for player ${player.name}: $totalPoints.")
        return totalPoints
    }

    // Function to transfer points from another player (to be expanded as needed).
    fun transferPointsFromOtherPlayer(player: Player) {
        Log.d(TAG, "Initiating point transfer for player ${player.name}.")
        PowerUpManager.transferPointsFromAnotherPlayer(player)
    }
}
