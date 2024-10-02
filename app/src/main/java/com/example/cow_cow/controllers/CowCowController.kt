package com.example.cow_cow.controllers

import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.PowerUp

class CowCowController {

    /**
     * Applies points to the player based on the object called (Cow, Church, Water Tower).
     * Takes into account custom rules, penalties, and power-ups.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     * @return The points added based on the object and any modifiers.
     */
    fun applyPoints(player: Player, objectType: String): Int {
        // Determine the base points based on the object type
        var points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }

        // Apply custom rules that might modify points
        player.customRules.forEach { rule ->
            points = applyCustomRule(rule, points)
        }

        // Apply penalties (e.g., point deductions)
        if (player.isPenalized()) {
            points = applyPenalties(player, points)
        }

        // Apply power-up modifiers (e.g., double points, extra points)
        player.activePowerUps.filter { it.isActive }.forEach { powerUp ->
            points = applyPowerUpEffect(powerUp, points)
        }

        // Update the player's score
        player.addBasePoints(points)
        updateObjectCount(player, objectType)

        return points
    }

    /**
     * Validates the call made by the player (e.g., no false calls, no repeats).
     * Can be extended to check for specific game rules, e.g., player can't call the same object twice in a row.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called.
     * @return True if the call is valid, false otherwise.
     */
    fun validateCall(player: Player, objectType: String): Boolean {
        // Example: Check if the player has already called this object type in the last round
        // Add logic here to make the game more challenging or apply penalties for false calls
        return true // Assuming all calls are valid for now.
    }

    /**
     * Resets the game for a list of players. Clears scores, counts, and resets penalties or bonuses.
     *
     * @param players The list of players to reset.
     */
    fun resetGame(players: List<Player>) {
        players.forEach { player ->
            player.basePoints = 0
            player.cowCount = 0
            player.churchCount = 0
            player.waterTowerCount = 0
            player.penalties.clear()
            player.activePowerUps.clear()
        }
    }

    // --- Helper functions ---

    /**
     * Applies any custom rule to modify the points awarded to the player.
     * @param rule The custom rule applied to the game.
     * @param points The current points for the object call.
     * @return The modified points.
     */
    private fun applyCustomRule(rule: CustomRule, points: Int): Int {
        // Add logic for custom rules here (e.g., double points for specific objects)
        return points // Returning unmodified points for now, but this can be expanded.
    }

    /**
     * Applies penalties to reduce points based on the player's penalties.
     * @param player The player receiving penalties.
     * @param points The current points for the object call.
     * @return The modified points after penalties.
     */
    private fun applyPenalties(player: Player, points: Int): Int {
        var modifiedPoints = points
        player.penalties.filter { it.isActive }.forEach { penalty ->
            modifiedPoints -= penalty.pointsDeducted
        }
        return modifiedPoints.coerceAtLeast(0) // Ensure points don't go negative.
    }

    /**
     * Applies power-up effects to modify the points awarded.
     * @param powerUp The power-up affecting the points.
     * @param points The current points for the object call.
     * @return The modified points after applying power-up effects.
     */
    private fun applyPowerUpEffect(powerUp: PowerUp, points: Int): Int {
        return when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> points * 2
            PowerUpType.SCORE_MULTIPLIER -> points + powerUp.effectValue
            else -> points // No modification for other power-ups
        }
    }

    /**
     * Updates the count of objects called by the player (Cow, Church, Water Tower).
     *
     * @param player The player whose count will be updated.
     * @param objectType The type of object called.
     */
    private fun updateObjectCount(player: Player, objectType: String) {
        when (objectType) {
            "Cow" -> player.cowCount++
            "Church" -> player.churchCount++
            "Water Tower" -> player.waterTowerCount++
        }
    }
}
