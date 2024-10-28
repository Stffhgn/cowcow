package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.models.Player
import kotlinx.coroutines.*

object PenaltyManager {
    private const val TAG = "PenaltyManager"

    /**
     * Checks if a player is currently penalized.
     *
     * @param player The player to check.
     * @return Boolean indicating if the player has active penalties.
     */
    fun isPlayerPenalized(player: Player): Boolean {
        // Remove expired penalties before checking.
        val currentTime = System.currentTimeMillis()
        PenaltyManager.checkAndDeactivateExpiredPenalties(player, currentTime)

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
        // Retrieve active penalties from each player.
        val activePenalties = mutableListOf<Penalty>()

        // Assuming you have access to a list of players.
        PlayerManager.getAllPlayers().forEach { player ->
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

        // Remove the penalty from the player's list of penalties.
        val removed = player.penalties.removeIf { it.id == penalty.id }

        if (removed) {
            log("Penalty ${penalty.name} successfully removed from player ${player.name}.")

            // Revert the effects of the penalty, if applicable.
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
                    log("Handling custom removal logic for penalty ${penalty.name} for player ${player.name}.")
                    handleCustomPenaltyRemoval(player, penalty)
                }
            }
        } else {
            log("Penalty ${penalty.name} not found for player ${player.name}.")
        }
    }

    /**
     * Handles the removal of custom penalty effects from a player.
     *
     * @param player The player from whom the penalty effect is being removed.
     * @param penalty The custom penalty to be handled.
     */
    private fun handleCustomPenaltyRemoval(player: Player, penalty: Penalty) {
        // Implement custom logic for removing specific penalty effects here.
        log("Custom penalty removal for ${penalty.name} is processed for player ${player.name}.")
    }

    /**
     * Adjust score based on active penalties.
     *
     * @param player The player whose points are being adjusted.
     * @param currentPoints The current points of the player.
     * @return The adjusted points after applying penalties.
     */
    fun applyPenaltyAdjustments(player: Player, currentPoints: Int): Int {
        var adjustedPoints = currentPoints

        // If the player has active penalties like TIME_PENALTY or FALSE_CALL, adjust points.
        player.penalties.filter { it.isActive }.forEach { penalty ->
            when (penalty.penaltyType) {
                PenaltyType.POINT_DEDUCTION, PenaltyType.FALSE_CALL -> {
                    adjustedPoints -= penalty.pointsDeducted
                    log("Penalty applied: Deducted ${penalty.pointsDeducted} points from ${player.name}.")
                }
                // Handle other penalty types as needed...
                else -> {
                    log("Unhandled penalty type for ${player.name}.")
                }
            }
        }

        return adjustedPoints
    }

    /**
     * Apply point deduction to the player.
     *
     * @param player The player from whom points will be deducted.
     * @param penalty The penalty detailing the points to be deducted.
     */
    private fun applyPointDeduction(player: Player, penalty: Penalty) {
        val effectiveDeduction = (penalty.pointsDeducted * penalty.multiplier).toInt()
        player.basePoints -= effectiveDeduction
        log("Deducted $effectiveDeduction points from ${player.name} due to penalty ${penalty.name}.")
    }

    /**
     * Silences the player for a specified duration.
     *
     * @param player The player to be silenced.
     * @param durationMillis The duration (in milliseconds) of the silence penalty.
     */
    private fun applySilence(player: Player, durationMillis: Long) {
        log("Silencing player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true
        CoroutineScope(Dispatchers.Main).launch {
            delay(durationMillis)
            player.isSilenced = false
            log("Player ${player.name} is no longer silenced.")
        }
    }

    /**
     * Temporarily bans a player from making calls or participating.
     *
     * @param player The player to be banned.
     * @param durationMillis The duration (in milliseconds) of the ban.
     */
    private fun banPlayer(player: Player, durationMillis: Long) {
        log("Banning player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true
        CoroutineScope(Dispatchers.Main).launch {
            delay(durationMillis)
            player.isSilenced = false
            log("Player ${player.name} ban has been lifted.")
        }
    }

    /**
     * Applies a time penalty (e.g., increases a player's time played).
     *
     * @param player The player receiving the time penalty.
     * @param penalty The penalty object detailing the time penalty.
     */
    private fun applyTimePenalty(player: Player, penalty: Penalty) {
        player.timePlayed += penalty.duration
        log("Added ${penalty.duration} ms to ${player.name}'s time played due to penalty ${penalty.name}.")
    }

    /**
     * Handles custom penalties or any penalties not covered by the standard ones.
     *
     * @param player The player receiving the custom penalty.
     * @param penalty The penalty object.
     */
    private fun handleCustomPenalty(player: Player, penalty: Penalty) {
        log("Applying custom penalty ${penalty.name} to player ${player.name}.")
        // Custom logic for unique penalties can be added here.
    }

    /**
     * Checks if a player's penalties have expired and deactivates them.
     *
     * @param player The player whose penalties are being checked.
     * @param currentTime The current system time.
     */
    fun checkAndDeactivateExpiredPenalties(player: Player, currentTime: Long) {
        player.penalties.filter { it.isActive && it.hasExpired(currentTime, null) }.forEach {
            it.isActive = false
            log("Deactivated expired penalty ${it.name} for player ${player.name}.")
        }
    }

    /**
     * Clears all penalties for a specific player.
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearAllPenalties(player: Player) {
        log("Clearing all penalties for player ${player.name}.")

        // Remove all penalties from the player's list.
        player.penalties.clear()

        // Reset any states that might have been affected by penalties.
        player.isSilenced = false
        log("Player ${player.name} is no longer silenced.")

        // Optionally reset other penalty effects, like point deductions or time penalties.
        player.penaltyPoints = 0
        log("All penalty points reset for player ${player.name}.")
    }

    /**
     * Log function to centralize logging.
     */
    private fun log(message: String) {
        Log.d(TAG, message)
    }
}
