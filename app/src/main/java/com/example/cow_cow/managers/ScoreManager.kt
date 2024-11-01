package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.PenaltyType

class ScoreManager(private val playerManager: PlayerManager) {

    private val TAG = "ScoreManager"

    /**
     * Adds points to the player's base score and saves the updated score.
     */
    fun addPointsToPlayer(player: Player, points: Int) {
        Log.d(TAG, "Adding $points points to player: ${player.name}")

        // Add points and calculate updated total score
        player.basePoints += points
        val updatedScore = calculatePlayerScore(player)
        Log.d(TAG, "Player ${player.name}'s new total score after adding points: $updatedScore")

        // Save updated player score
        playerManager.savePlayer(player)
    }

    /**
     * Calculate total score for a player, factoring in base points, bonuses, penalties, and active power-ups.
     *
     * @param player The player whose score is being calculated.
     * @return The player's total score.
     */
    fun calculatePlayerScore(player: Player): Int {
        var totalPoints = player.basePoints + player.bonusPoints
        Log.d(TAG, "Calculating score for ${player.name}: Base points = ${player.basePoints}, Bonus points = ${player.bonusPoints}")

        // Subtract penalties from total points
        totalPoints -= player.penaltyPoints
        Log.d(TAG, "Penalty points for ${player.name}: ${player.penaltyPoints}. Total after penalty = $totalPoints")

        // Apply active power-up effects
        player.activePowerUps.filter { it.isActive }.forEach { powerUp ->
            totalPoints = applyPowerUpToScore(totalPoints, powerUp)
            Log.d(TAG, "Power-up ${powerUp.type} applied for ${player.name}. Total points = $totalPoints")
        }

        // Ensure total points do not fall below zero
        if (totalPoints < 0) {
            totalPoints = 0
            Log.d(TAG, "Total points adjusted to zero for ${player.name} due to negative balance.")
        }

        Log.d(TAG, "Final calculated score for ${player.name}: $totalPoints")
        return totalPoints
    }

    /**
     * Applies the effect of a power-up to the player's score.
     *
     * @param currentScore The current score before applying the power-up.
     * @param powerUp The active power-up to apply.
     * @return The new score after applying the power-up effect.
     */
    private fun applyPowerUpToScore(currentScore: Int, powerUp: PowerUp): Int {
        var adjustedScore = currentScore
        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                adjustedScore *= 2
                Log.d(TAG, "Double Points power-up applied. New score: $adjustedScore")
            }
            PowerUpType.SCORE_MULTIPLIER -> {
                adjustedScore *= powerUp.effectValue
                Log.d(TAG, "Score Multiplier (${powerUp.effectValue}x) applied. New score: $adjustedScore")
            }
            PowerUpType.BONUS_POINTS -> {
                adjustedScore += powerUp.effectValue
                Log.d(TAG, "Bonus Points applied: +${powerUp.effectValue}. New score: $adjustedScore")
            }
            else -> {
                Log.d(TAG, "No specific score effect for power-up: ${powerUp.type}.")
            }
        }
        return adjustedScore
    }

    /**
     * Apply a penalty to a player, adjusting their score accordingly.
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying ${penalty.penaltyType} penalty to ${player.name}")
        when (penalty.penaltyType) {
            PenaltyType.POINT_DEDUCTION -> {
                Log.d(TAG, "Deducting ${penalty.pointsDeducted} points from ${player.name}")
                player.penaltyPoints += penalty.pointsDeducted
            }
            PenaltyType.SILENCED -> {
                Log.d(TAG, "${player.name} has been silenced.")
                player.isSilenced = true
            }
            PenaltyType.TEMPORARY_BAN -> {
                Log.d(TAG, "${player.name} has been temporarily banned.")
                player.isSilenced = true  // Custom ban logic
            }
            PenaltyType.FALSE_CALL -> {
                Log.d(TAG, "${player.name} made a false call.")
                handleFalseCallPenalty(player)
            }
            PenaltyType.TIME_PENALTY -> {
                Log.d(TAG, "Applying time penalty to ${player.name}")
                applyTimePenalty(player, penalty)
            }
            PenaltyType.OTHER -> {
                Log.d(TAG, "Applying custom penalty to ${player.name}")
                handleCustomPenalty(player, penalty)
            }
            else -> {
                Log.d(TAG, "Unknown penalty type for ${player.name}")
            }
        }
        playerManager.savePlayer(player)
    }

    /**
     * Calculate the total score for a team by summing up all player scores.
     */
    fun calculateTeamScore(team: Team): Int {
        Log.d(TAG, "Calculating score for team: ${team.name}")
        return team.members.sumOf { calculatePlayerScore(it) }
    }

    /**
     * Distribute points evenly among the team members.
     */
    fun distributePointsToTeam(team: Team, pointsToDistribute: Int) {
        val pointsPerPlayer = pointsToDistribute / team.members.size
        Log.d(TAG, "Distributing $pointsToDistribute points across ${team.members.size} players.")

        team.members.forEach { player ->
            addPointsToPlayer(player, pointsPerPlayer)
            Log.d(TAG, "$pointsPerPlayer points added to ${player.name}.")
        }
    }

    /**
     * Compare and get the player with the highest score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        val player = players.maxByOrNull { calculatePlayerScore(it) }
        Log.d(TAG, "Player with highest score: ${player?.name} with score ${player?.let { calculatePlayerScore(it) }}")
        return player
    }

    /**
     * Generate a sorted leaderboard of players.
     */
    fun getLeaderboard(players: List<Player>): List<Player> {
        Log.d(TAG, "Generating leaderboard for players.")
        return players.sortedByDescending { calculatePlayerScore(it) }
    }

    /**
     * Resets the scores for a single player, typically at the start of a new game.
     */
    fun resetPlayerScores(player: Player) {
        Log.d(TAG, "Resetting scores for player ${player.name}.")

        // Reset player score attributes
        player.basePoints = 0
        player.bonusPoints = 0
        player.penaltyPoints = 0

        // Clear any active power-ups and penalties
        player.activePowerUps.clear()
        player.penalties.clear()

        // Save the reset player data
        playerManager.savePlayer(player)

        Log.d(TAG, "Player ${player.name}'s score and status have been reset.")
    }

    // Helper methods for specific penalties
    private fun handleFalseCallPenalty(player: Player) {
        Log.d(TAG, "${player.name} made a false call. Applying false call penalty.")
        player.penaltyPoints += 5
    }

    private fun applyTimePenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying time penalty to ${player.name}.")
        // Custom time penalty logic (if applicable)
    }

    private fun handleCustomPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying custom penalty to ${player.name}.")
        // Custom penalty handling logic
    }
}
