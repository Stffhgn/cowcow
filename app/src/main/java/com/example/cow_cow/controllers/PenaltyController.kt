package com.example.cow_cow.controllers

import android.util.Log
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PenaltyType
import java.util.*

class PenaltyController(private val penaltyManager: PenaltyManager) { // Inject PenaltyManager instance

    private val TAG = "PenaltyController"

    /**
     * Apply a penalty to a player based on the specified event.
     *
     * @param player The player receiving the penalty.
     * @param event The event that triggers the penalty.
     */
    fun applyPenaltyForEvent(player: Player, event: String) {
        Log.d(TAG, "Applying penalty for event '$event' to player ${player.name}")

        val penalty = when (event) {
            "missed_call" -> createPenalty("Missed Call", PenaltyType.POINT_DEDUCTION, 10)
            "overstepped_turn" -> createPenalty("Turn Violation", PenaltyType.SILENCED, 0, 5000L)
            "false_call" -> createPenalty("False Call", PenaltyType.POINT_DEDUCTION, 5)
            else -> {
                Log.w(TAG, "No penalty defined for event: $event")
                return
            }
        }

        // Apply the penalty using the PenaltyManager instance.
        penaltyManager.applyPenalty(player, penalty)
    }

    /**
     * Create a penalty object with the given details.
     *
     * @param name The name of the penalty.
     * @param penaltyType The type of penalty.
     * @param pointsDeducted The points to be deducted.
     * @param duration The duration of the penalty (optional, defaults to 0 for instant penalties).
     * @return A Penalty object.
     */
    private fun createPenalty(
        name: String,
        penaltyType: PenaltyType,
        pointsDeducted: Int = 0,
        duration: Long = 0L
    ): Penalty {
        return Penalty(
            id = UUID.randomUUID().toString(),
            name = name,
            pointsDeducted = pointsDeducted,
            penaltyType = penaltyType,
            duration = duration
        )
    }

    /**
     * Check and deactivate any expired penalties for a player.
     *
     * @param player The player whose penalties are checked.
     */
    fun checkAndDeactivateExpiredPenalties(player: Player) {
        val currentTime = System.currentTimeMillis()
        penaltyManager.checkAndDeactivateExpiredPenalties(player, currentTime)
    }

    /**
     * Remove a specific penalty from a player.
     *
     * @param player The player from whom the penalty is removed.
     * @param penalty The penalty to remove.
     */
    fun removePenalty(player: Player, penalty: Penalty) {
        penaltyManager.removePenalty(player, penalty)
    }

    /**
     * Clear all penalties for a player (e.g., when resetting a player's state).
     *
     * @param player The player whose penalties are cleared.
     */
    fun clearAllPenalties(player: Player) {
        penaltyManager.clearAllPenalties(player)
    }

    /**
     * Check if a player is currently penalized.
     *
     * @param player The player to check.
     * @return Boolean indicating if the player has active penalties.
     */
    fun isPlayerPenalized(player: Player): Boolean {
        return penaltyManager.isPlayerPenalized(player)
    }
}
