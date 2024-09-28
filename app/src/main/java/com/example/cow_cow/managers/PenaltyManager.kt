package com.example.cow_cow.managers

import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.PenaltyType
import com.example.cow_cow.models.Player
import java.util.*

object PenaltyManager {

    /**
     * Apply a penalty to a specific player.
     *
     * @param player The player receiving the penalty.
     * @param penalty The penalty to be applied.
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
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
        val currentTime = System.currentTimeMillis()
        player.penalties.removeIf { penalty -> penalty.hasExpired(currentTime, null) }
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
    }

    /**
     * Silences the player for a specified duration.
     *
     * @param player The player to be silenced.
     * @param durationMillis The duration (in milliseconds) of the silence penalty.
     */
    private fun silencePlayer(player: Player, durationMillis: Long) {
        player.isSilenced = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                player.isSilenced = false
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
        player.isSilenced = true  // You can customize this to handle specific restrictions
        Timer().schedule(object : TimerTask() {
            override fun run() {
                player.isSilenced = false
            }
        }, durationMillis)
    }

    /**
     * Handles the case where the player made a false call (e.g., called a cow incorrectly).
     *
     * @param player The player who made the false call.
     */
    private fun handleFalseCall(player: Player) {
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
        // Implement any custom logic based on your game
        // This could be something like unique punishments, special rules, etc.
    }

    /**
     * Removes all penalties from a player (useful for clearing states when the game resets).
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearAllPenalties(player: Player) {
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
        return player.isPenalized()
    }

    /**
     * Example penalty application flow, could be invoked by game events.
     */
    fun triggerPenaltyBasedOnEvent(player: Player, event: String) {
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
