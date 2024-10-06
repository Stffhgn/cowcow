package com.example.cow_cow.controllers

import com.example.cow_cow.managers.CustomRuleManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.GameViewModel
import android.util.Log
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.managers.TeamManager

class MainGameController(
    private val gameViewModel: GameViewModel,
    private val playerManager: PlayerManager,
    private val teamManager: TeamManager,
    private val scoreManager: ScoreManager
) {
    // Handle when "Cow" is called by a player
    fun handleCowCalled(player: Player): Int {
        return applyActionWithRules(player, "Cow", points = 1)
    }

    // Handle when "Church" is called by a player
    fun handleChurchCalled(player: Player): Int {
        return applyActionWithRules(player, "Church", points = 2)
    }

    // Handle when "Water Tower" is called by a player
    fun handleWaterTowerCalled(player: Player): Int {
        return applyActionWithRules(player, "Water Tower", points = 3)
    }

    // Function to reset the game state
    fun resetMainGame() {
        gameViewModel.resetCalledObjects()
        Log.d("MainGameController", "Game reset successfully.")
    }

    // Helper function to apply game actions with rules
    private fun applyActionWithRules(player: Player, objectType: String, points: Int): Int {
        if (!validateAction(player, objectType)) {
            Log.d("MainGameController", "Invalid action by ${player.name}: $objectType.")
            return 0 // Invalid call, no points awarded
        }

        // Apply the points and update the ViewModel
        gameViewModel.applyPointsForAction(player, objectType)
        Log.d("MainGameController", "${player.name} successfully called $objectType for $points points.")

        // Apply additional rules (like penalties, bonuses, etc.)
        applyCustomRules(player, objectType)

        return points
    }

    // Validation logic for game actions (e.g., no repeat calls, false calls)
    private fun validateAction(player: Player, objectType: String): Boolean {
        // Log what the player called
        Log.d("MainGameController", "${player.name} called $objectType.")

        // No validation rules applied, always return true
        return true
    }

    // Apply any custom rules or bonuses based on player actions
    private fun applyCustomRules(player: Player, objectType: String) {
        val customRule = player.customRule
        customRule?.let {
            CustomRuleManager.applyCustomRule(player, it)
            Log.d("MainGameController", "Custom rule '${it.ruleName}' applied for ${player.name}")
        } ?: Log.d("MainGameController", "No custom rule applied for ${player.name} during $objectType call.")
    }
}
