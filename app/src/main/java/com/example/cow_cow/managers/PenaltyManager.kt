package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.models.Player
import kotlinx.coroutines.*

class PenaltyManager(private val playerManager: PlayerManager) { // Inject PlayerManager instance
    private val TAG = "PenaltyManager"

    /**
     * Checks if a player is currently penalized.
     *
     * @param player The player to check.
     * @return Boolean indicating if the player has active penalties.
     */
    fun isPlayerPenalized(player: Player): Boolean {
        // Remove expired penalties before checking.
        val currentTime = System.currentTimeMillis()
        checkAndDeactivateExpiredPenalties(player, currentTime)

        // Check if any active penalties remain.
        val hasActivePenalties = player.penalties.any { it.isActive }
        log("Player ${player.name} penalized status: $hasActivePenalties")

        return hasActivePenalties
    }

    /**
     * Get the list of all active penalties for players.
     *
     * @return A list of active penalties across all players.
     */
    fun getActivePenalties(): List<Penalty> {
        val activePenalties = mutableListOf<Penalty>()

        // Retrieve active penalties from each player using the playerManager instance.
        playerManager.getAllPlayers().forEach { player ->
            activePenalties.addAll(player.penalties.filter { it.isActive })
        }

        log("Retrieved ${activePenalties.size} active penalties.")
        return activePenalties
    }

    /**
     * Apply a penalty to a specific player.
     *
     * @param player The player receiving the penalty.
     * @param penalty The penalty to be applied.
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
        log("Applying ${penalty.penaltyType} penalty to ${player.name}")

        when (penalty.penaltyType) {
            PenaltyType.POINT_DEDUCTION, PenaltyType.FALSE_CALL -> applyPointDeduction(player, penalty)
            PenaltyType.SILENCED -> applySilence(player, penalty.duration)
            PenaltyType.TEMPORARY_BAN -> banPlayer(player, penalty.duration)
            PenaltyType.TIME_PENALTY -> applyTimePenalty(player, penalty)
            PenaltyType.OTHER -> handleCustomPenalty(player, penalty)
        }

        // Mark penalty as inactive if it's not stackable.
        if (!penalty.stackable) {
            penalty.isActive = false
        }
    }

    /**
     * Removes a specific penalty from a player.
     *
     * @param player The player whose penalty will be removed.
     * @param penalty The penalty to be removed.
     */
    fun removePenalty(player: Player, penalty: Penalty) {
        log("Attempting to remove penalty ${penalty.name} from player ${player.name}.")

        val removed = player.penalties.removeIf { it.id == penalty.id }
        if (removed) {
            log("Penalty ${penalty.name} successfully removed from player ${player.name}.")
            when (penalty.penaltyType) {
                PenaltyType.POINT_DEDUCTION, PenaltyType.FALSE_CALL -> {
                    player.basePoints += penalty.pointsDeducted
                    log("Restored ${penalty.pointsDeducted} points to player ${player.name} after removing penalty.")
                }
                PenaltyType.SILENCED, PenaltyType.TEMPORARY_BAN -> {
                    player.isSilenced = false
                    log("Player ${player.name} is no longer silenced after removing penalty.")
                }
                PenaltyType.TIME_PENALTY -> {
                    player.timePlayed -= penalty.duration
                    log("Reverted time penalty of ${penalty.duration} ms for player ${player.name}.")
                }
                PenaltyType.OTHER -> {
                    handleCustomPenaltyRemoval(player, penalty)
                }
            }
        } else {
            log("Penalty ${penalty.name} not found for player ${player.name}.")
        }
    }

    private fun handleCustomPenaltyRemoval(player: Player, penalty: Penalty) {
        log("Custom penalty removal for ${penalty.name} is processed for player ${player.name}.")
    }


    fun applyPenaltyAdjustments(player: Player, currentPoints: Int): Int {
        var adjustedPoints = currentPoints
        player.penalties.filter { it.isActive }.forEach { penalty ->
            when (penalty.penaltyType) {
                PenaltyType.POINT_DEDUCTION, PenaltyType.FALSE_CALL -> {
                    adjustedPoints -= penalty.pointsDeducted
                    log("Penalty applied: Deducted ${penalty.pointsDeducted} points from ${player.name}.")
                }
                else -> log("Unhandled penalty type for ${player.name}.")
            }
        }
        return adjustedPoints
    }

    private fun applyPointDeduction(player: Player, penalty: Penalty) {
        val effectiveDeduction = (penalty.pointsDeducted * penalty.multiplier).toInt()
        player.basePoints -= effectiveDeduction
        log("Deducted $effectiveDeduction points from ${player.name} due to penalty ${penalty.name}.")
    }

    private fun applySilence(player: Player, durationMillis: Long) {
        log("Silencing player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true
        CoroutineScope(Dispatchers.Main).launch {
            delay(durationMillis)
            player.isSilenced = false
            log("Player ${player.name} is no longer silenced.")
        }
    }

    private fun banPlayer(player: Player, durationMillis: Long) {
        log("Banning player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true
        CoroutineScope(Dispatchers.Main).launch {
            delay(durationMillis)
            player.isSilenced = false
            log("Player ${player.name} ban has been lifted.")
        }
    }

    private fun applyTimePenalty(player: Player, penalty: Penalty) {
        player.timePlayed += penalty.duration
        log("Added ${penalty.duration} ms to ${player.name}'s time played due to penalty ${penalty.name}.")
    }

    private fun handleCustomPenalty(player: Player, penalty: Penalty) {
        log("Applying custom penalty ${penalty.name} to player ${player.name}.")
    }

    fun checkAndDeactivateExpiredPenalties(player: Player, currentTime: Long) {
        player.penalties.filter { it.isActive && it.hasExpired(currentTime, null) }.forEach {
            it.isActive = false
            log("Deactivated expired penalty ${it.name} for player ${player.name}.")
        }
    }

    fun clearAllPenalties(player: Player) {
        log("Clearing all penalties for player ${player.name}.")
        player.penalties.clear()
        player.isSilenced = false
        player.penaltyPoints = 0
        log("All penalty points reset for player ${player.name}.")
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }
}
