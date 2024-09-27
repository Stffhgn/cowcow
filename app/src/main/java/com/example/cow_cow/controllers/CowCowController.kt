package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player

class CowCowController {

    // This function applies the game rules for Cow, Church, Water Tower
    fun applyPoints(player: Player, objectType: String): Int {
        // Determine the points based on the object type
        val points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }

        // Update the player's score
        player.basePoints += points
        return points
    }

    // Example function to check if the call was valid (to be extended as per the game's rules)
    fun validateCall(player: Player, objectType: String): Boolean {
        // Add logic to check if the call was valid (like "no repeats" or "false calls")
        // For now, let's assume all calls are valid.
        return true
    }

    // Function to reset the game (clears the scores, resets state, etc.)
    fun resetGame(players: List<Player>) {
        players.forEach { player ->
            player.basePoints = 0
            player.cowCount = 0
            player.churchCount = 0
            player.waterTowerCount = 0
        }
    }
}