package com.example.cow_cow.controllers

import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.PowerUp

class CowCowController {

    private val TAG = "CowCowController"

    /**
     * Applies points to the player based on the object called (Cow, Church, Water Tower).
     * Takes into account custom rules, penalties, and power-ups.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     * @return The points added based on the object and any modifiers.
     */
    fun applyPoints(player: Player, objectType: String): Int {
        Log.d(TAG, "applyPoints: Player ${player.name} called $objectType")

        // Determine the base points based on the object type
        var points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }
        Log.d(TAG, "applyPoints: Base points for $objectType is $points")

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
        Log.d(TAG, "applyPoints: Player ${player.name} now has ${player.basePoints} points after calling $objectType")

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
        // Add logic here to make the game more challenging or apply penalties for false calls
        Log.d(TAG, "validateCall: Checking call validity for ${player.name} calling $objectType")
        return true // Assuming all calls are valid for now.
    }

    /**
     * Resets the game for a list of players. Clears scores, counts, and resets penalties or bonuses.
     *
     * @param players The list of players to reset.
     */
    fun resetGame(players: List<Player>) {
        Log.d(TAG, "resetGame: Resetting game for all players")
        players.forEach { player ->
            player.basePoints = 0
            player.cowCount = 0
            player.churchCount = 0
            player.waterTowerCount = 0
            player.penalties.clear()
            player.activePowerUps.clear()
            Log.d(TAG, "resetGame: Reset player ${player.name} scores and counts")
        }
    }

    // --- Helper functions ---

    /**
     * Applies any custom rule to modify the points awarded to the player.
     * @param rule The custom rule applied to the game.
     * @param points The current points for the object call.
     * @return The modified points.
     */
    fun applyCustomRule(rule: CustomRule, points: Int): Int {
        Log.d(TAG, "applyCustomRule: Applying custom rule ${rule.ruleName} to modify points")
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
        Log.d(TAG, "applyPenalties: Applying penalties to ${player.name}")
        var modifiedPoints = points
        player.penalties.filter { it.isActive }.forEach { penalty ->
            modifiedPoints -= penalty.pointsDeducted
            Log.d(TAG, "applyPenalties: Deducting ${penalty.pointsDeducted} points from ${player.name}")
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
        Log.d(TAG, "applyPowerUpEffect: Applying power-up ${powerUp.type} to modify points")
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
        Log.d(TAG, "updateObjectCount: Updated $objectType count for player ${player.name}")
    }
}
