package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.PenaltyType

object ScoreManager {

    private const val TAG = "ScoreManager"

    /**
     * Calculate total score for a single player, factoring in base points, bonuses, penalties,
     * and active power-ups.
     *
     * @param player The player whose score is being calculated.
     * @return The player's total score.
     */
    fun calculatePlayerScore(player: Player): Int {
        var totalPoints = player.basePoints + player.bonusPoints
        Log.d(TAG, "Calculating score for ${player.name}: Base points = ${player.basePoints}, Bonus points = ${player.bonusPoints}")

        // Subtract penalties from the total points
        totalPoints -= player.penaltyPoints
        Log.d(TAG, "Penalty points for ${player.name}: ${player.penaltyPoints}. Total after penalty = $totalPoints")

        // Apply any active power-up effects
        player.activePowerUps.filter { it.isActive }.forEach { powerUp ->
            totalPoints = applyPowerUpToScore(totalPoints, powerUp)
            Log.d(TAG, "Power-up ${powerUp.type} applied for ${player.name}. Total points = $totalPoints")
        }

        // Ensure that the total points do not go below zero
        if (totalPoints < 0) {
            totalPoints = 0
            Log.d(TAG, "Total points adjusted to zero for ${player.name} due to negative balance.")
        }

        Log.d(TAG, "Final score for ${player.name}: $totalPoints")
        return totalPoints
    }

    /**
     * Add a specified number of points to a player's base score.
     *
     * @param player The player to whom points will be added.
     * @param points The number of points to add.
     */
    fun addPointsToPlayer(player: Player, points: Int) {
        Log.d(TAG, "Adding $points points to player: ${player.name}")

        // Directly add the points to the player's base score
        player.basePoints += points

        // Log the updated score
        val updatedScore = calculatePlayerScore(player)
        Log.d(TAG, "Player ${player.name}'s new total score: $updatedScore")
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
            // Handle other power-up effects here if needed.
            else -> {
                Log.d(TAG, "No specific score effect for power-up: ${powerUp.type}.")
            }
        }
        return adjustedScore
    }


    /**
     * Apply a penalty to a player, adjusting their score accordingly.
     *
     * @param player The player receiving the penalty.
     * @param penalty The penalty to apply.
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
    }

    /**
     * Calculate the total score for a team by summing up all player scores.
     *
     * @param team The team whose score is being calculated.
     * @return The team's total score.
     */
    fun calculateTeamScore(team: Team): Int {
        Log.d(TAG, "Calculating score for team: ${team.name}")
        return team.members.sumOf { calculatePlayerScore(it) }
    }

    /**
     * Distribute points evenly among the team members.
     *
     * @param team The team receiving the points.
     * @param pointsToDistribute The total points to distribute among team members.
     */
    fun distributePointsToTeam(team: Team, pointsToDistribute: Int) {
        val pointsPerPlayer = pointsToDistribute / team.members.size
        Log.d(TAG, "Distributing $pointsToDistribute points across ${team.members.size} players.")

        team.members.forEach { player ->
            ScoreManager.addPointsToPlayer(player, pointsPerPlayer)
            Log.d(TAG, "$pointsPerPlayer points added to ${player.name}.")
        }
    }

    /**
     * Compare and get the player with the highest score.
     *
     * @param players The list of players to compare.
     * @return The player with the highest score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        val player = players.maxByOrNull { calculatePlayerScore(it) }
        Log.d(TAG, "Player with highest score: ${player?.name} with score ${player?.let { calculatePlayerScore(it) }}")
        return player
    }

    /**
     * Handle leaderboard logic by sorting players based on their scores.
     *
     * @param players The list of players to rank.
     * @return The list of players sorted by their scores in descending order.
     */
    fun getLeaderboard(players: List<Player>): List<Player> {
        Log.d(TAG, "Generating leaderboard for players.")
        return players.sortedByDescending { calculatePlayerScore(it) }
    }

    /**
     * Resets the scores for all players, typically at the start of a new game.
     *
     * @param players The list of players to reset.
     */
    fun resetPlayerScores(players: List<Player>) {
        Log.d(TAG, "Resetting scores for all players.")
        players.forEach { player ->
            player.basePoints = 0
            player.bonusPoints = 0
            player.penaltyPoints = 0
            player.activePowerUps.clear()
            player.penalties.clear()
            Log.d(TAG, "Player ${player.name}'s score and status have been reset.")
        }
    }

    /**
     * Resets team scores by clearing individual player scores within the team.
     *
     * @param team The team whose scores should be reset.
     */
    fun resetTeamScores(team: Team) {
        Log.d(TAG, "Resetting scores for team: ${team.name}.")
        resetPlayerScores(team.members)
        team.teamScore = 0
    }

    // Helper methods to handle specific penalties
    private fun handleFalseCallPenalty(player: Player) {
        Log.d(TAG, "${player.name} made a false call. Applying false call penalty.")
        player.penaltyPoints += 5 // Example penalty for a false call
    }

    private fun applyTimePenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying time penalty to ${player.name}.")
        // Custom logic for applying time penalties (if applicable to your game)
    }

    private fun handleCustomPenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Applying custom penalty to ${player.name}.")
        // Custom logic for handling other types of penalties
    }
}
