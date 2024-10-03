package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.models.Player
import java.util.*

object PenaltyManager {

    private val TAG = "PenaltyManager"

    /**
     * Apply a penalty to a specific player.
     *
     * @param player The player receiving the penalty.
     * @param penalty The penalty to be applied.
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying penalty ${penalty.name} to player ${player.name}")
        player.penalties.add(penalty)
        when (penalty.penaltyType) {
            PenaltyType.POINT_DEDUCTION -> deductPoints(player, penalty)
            PenaltyType.SILENCED -> silencePlayer(player, penalty.duration)
            PenaltyType.TEMPORARY_BAN -> banPlayer(player, penalty.duration)
            PenaltyType.FALSE_CALL -> handleFalseCall(player)
            PenaltyType.TIME_PENALTY -> applyTimePenalty(player, penalty)
            PenaltyType.OTHER -> handleCustomPenalty(player, penalty)
        }
        player.applyPenalty(penalty) // Update player's state
    }

    /**
     * Removes expired penalties from the player's list.
     *
     * @param player The player whose penalties will be checked.
     */
    fun removeExpiredPenalties(player: Player) {
        Log.d(TAG, "Checking for expired penalties for player ${player.name}")
        val currentTime = System.currentTimeMillis()
        player.penalties.removeIf { penalty ->
            val expired = penalty.hasExpired(currentTime, null)
            if (expired) Log.d(TAG, "Penalty ${penalty.name} has expired for player ${player.name}")
            expired
        }
    }

    fun removePenalty(player: Player, penalty: Penalty) {
        // Log the penalty removal action
        Log.d("PenaltyManager", "Attempting to remove penalty ${penalty.name} from player ${player.name}.")

        // Remove the specific penalty from the player's active penalties list
        val removed = player.penalties.remove(penalty)

        if (removed) {
            Log.d("PenaltyManager", "Penalty ${penalty.name} removed from player ${player.name}.")
        } else {
            Log.d("PenaltyManager", "Penalty ${penalty.name} not found for player ${player.name}.")
        }

        // Optionally: Clean up any state changes the penalty may have caused (e.g., unsilence player)
        if (penalty.penaltyType == PenaltyType.SILENCED) {
            player.isSilenced = false
            Log.d("PenaltyManager", "Player ${player.name} is no longer silenced.")
        }

        // If necessary, refresh the player's points or status
        if (penalty.penaltyType == PenaltyType.POINT_DEDUCTION) {
            player.basePoints += penalty.pointsDeducted
            Log.d("PenaltyManager", "Restored ${penalty.pointsDeducted} points to player ${player.name}.")
        }
    }

    /**
     * Clears all penalties for a specific player.
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearPlayerPenalties(player: Player) {
        // Log the action
        Log.d("PlayerManager", "Clearing all penalties for player ${player.name}.")

        // Clear the penalties
        player.penalties.clear()

        // Log the completion of penalty clearing
        Log.d("PlayerManager", "All penalties cleared for player ${player.name}.")
    }

    /**
     * Deducts points from a player based on the penalty.
     *
     * @param player The player from whom points will be deducted.
     * @param penalty The penalty detailing the points to be deducted.
     */
    private fun deductPoints(player: Player, penalty: Penalty) {
        val effectivePointsDeduction = (penalty.pointsDeducted * penalty.multiplier).toInt()
        player.basePoints -= effectivePointsDeduction
        Log.d(TAG, "${effectivePointsDeduction} points deducted from player ${player.name} due to penalty ${penalty.name}")
    }

    /**
     * Silences the player for a specified duration.
     *
     * @param player The player to be silenced.
     * @param durationMillis The duration (in milliseconds) of the silence penalty.
     */
    private fun silencePlayer(player: Player, durationMillis: Long) {
        Log.d(TAG, "Silencing player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                player.isSilenced = false
                Log.d(TAG, "Player ${player.name} is no longer silenced.")
            }
        }, durationMillis)
    }

    /**
     * Temporarily bans a player from making calls or participating.
     *
     * @param player The player to be banned.
     * @param durationMillis The duration (in milliseconds) of the ban.
     */
    private fun banPlayer(player: Player, durationMillis: Long) {
        Log.d(TAG, "Banning player ${player.name} for $durationMillis milliseconds.")
        player.isSilenced = true  // You can customize this to handle specific restrictions
        Timer().schedule(object : TimerTask() {
            override fun run() {
                player.isSilenced = false
                Log.d(TAG, "Player ${player.name} ban has been lifted.")
            }
        }, durationMillis)
    }

    /**
     * Handles the case where the player made a false call (e.g., called a cow incorrectly).
     *
     * @param player The player who made the false call.
     */
    private fun handleFalseCall(player: Player) {
        Log.d(TAG, "Handling false call for player ${player.name}. Applying penalty.")
        // For false calls, deduct points or apply other penalties
        val falseCallPenalty = Penalty(
            id = UUID.randomUUID().hashCode(),
            name = "False Call",
            pointsDeducted = 5, // Example deduction
            penaltyType = PenaltyType.POINT_DEDUCTION
        )
        applyPenalty(player, falseCallPenalty)
    }

    /**
     * Applies a time penalty (e.g., reduces a player's allowed time).
     *
     * @param player The player receiving the time penalty.
     * @param penalty The penalty object detailing the time penalty.
     */
    private fun applyTimePenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying time penalty to player ${player.name}")
        // Implementation for time-based penalties can be expanded as per game mechanics
        // For example, reduce the player's remaining time in time-based modes
    }

    /**
     * Handles custom penalties or any penalties not covered by the standard ones.
     *
     * @param player The player receiving the custom penalty.
     * @param penalty The penalty object.
     */
    private fun handleCustomPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying custom penalty ${penalty.name} to player ${player.name}")
        // Implement any custom logic based on your game
        // This could be something like unique punishments, special rules, etc.
    }

    /**
     * Removes all penalties from a player (useful for clearing states when the game resets).
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearAllPenalties(player: Player) {
        Log.d(TAG, "Clearing all penalties for player ${player.name}.")
        player.penalties.clear()
    }

    /**
     * Check if the player is currently penalized.
     *
     * @param player The player to check.
     * @return Boolean indicating if the player has active penalties.
     */
    fun isPlayerPenalized(player: Player): Boolean {
        removeExpiredPenalties(player)
        val isPenalized = player.penalties.any { it.isActive }
        Log.d(TAG, "Player ${player.name} penalized: $isPenalized")
        return isPenalized
    }

    /**
     * Retrieve all active penalties.
     *
     * @return List of all currently active penalties.
     */
    fun getActivePenalties(): List<Penalty> {
        Log.d(TAG, "Retrieving all active penalties.")
        val allPenalties = mutableListOf<Penalty>()
        // Sweep through all players and retrieve their active penalties
        // Here, assume you have access to a list of all players
        // Example:
        // players.forEach { player -> allPenalties.addAll(player.penalties.filter { it.isActive }) }
        Log.d(TAG, "Total active penalties retrieved: ${allPenalties.size}")
        return allPenalties
    }

    /**
     * Example penalty application flow, could be invoked by game events.
     */
    fun triggerPenaltyBasedOnEvent(player: Player, event: String) {
        Log.d(TAG, "Triggering penalty based on event: $event for player ${player.name}")
        when (event) {
            "missed_call" -> {
                val penalty = Penalty(
                    id = UUID.randomUUID().hashCode(),
                    name = "Missed Call",
                    pointsDeducted = 10,
                    penaltyType = PenaltyType.POINT_DEDUCTION,
                    duration = 0L
                )
                applyPenalty(player, penalty)
            }
            "overstepped_turn" -> {
                val penalty = Penalty(
                    id = UUID.randomUUID().hashCode(),
                    name = "Turn Violation",
                    penaltyType = PenaltyType.SILENCED,
                    duration = 5000L // Silenced for 5 seconds
                )
                applyPenalty(player, penalty)
            }
            // Add more event-based triggers as needed
        }
    }
}
