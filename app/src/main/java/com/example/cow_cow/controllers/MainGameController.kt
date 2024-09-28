package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.GameViewModel

class MainGameController(private val gameViewModel: GameViewModel) {

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
    }

    // Helper function to apply game actions with rules
    private fun applyActionWithRules(player: Player, objectType: String, points: Int): Int {
        if (!validateAction(player, objectType)) {
            return 0 // Invalid call, no points awarded
        }

        // Apply the points and update the ViewModel
        gameViewModel.applyPointsForAction(player, objectType)

        // Apply additional rules (like penalties, bonuses, etc.)
        applyCustomRules(player, objectType)

        return points
    }

    // Validation logic for game actions (e.g., no repeat calls, false calls)
    private fun validateAction(player: Player, objectType: String): Boolean {
        // Add logic to check if the call is valid based on game rules
        // For example, disallow consecutive calls of the same type
        return true
    }

    // Apply any custom rules or bonuses based on player actions
    private fun applyCustomRules(player: Player, objectType: String) {
        // Add logic to apply custom rules, bonuses, or penalties
        // Example: If a "Water Tower" is called, apply a bonus rule for that player
    }
}
