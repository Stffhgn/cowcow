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
     * Handle score calculation when a player makes a game event call (like Cow, Church, etc.)
     * This function will calculate the new score and return it.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     * @return The updated total score for the player.
     */
    fun calculatePlayerScoreAfterEvent(player: Player, objectType: String): Int {
        // Base points based on object type
        var basePoints = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }

        Log.d(TAG, "Base points for $objectType: $basePoints")

        // Update player's base points
        player.addBasePoints(basePoints)

        // Calculate the player's total score
        val totalScore = calculatePlayerScore(player)
        Log.d(TAG, "Total score for ${player.name} after calling $objectType: $totalScore")

        return totalScore
    }

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

        // Subtract penalties
        totalPoints -= player.penaltyPoints
        Log.d(TAG, "Penalty points for ${player.name}: ${player.penaltyPoints}. Total after penalty = $totalPoints")

        // Apply power-up effects
        player.activePowerUps.filter { it.isActive }.forEach { powerUp ->
            totalPoints = applyPowerUpToScore(totalPoints, powerUp)
            Log.d(TAG, "Power-up ${powerUp.type} applied for ${player.name}. Total points = $totalPoints")
        }

        return totalPoints
    }

    /**
     * Apply a power-up's effect to the score.
     *
     * @param currentScore The current score before applying the power-up.
     * @param powerUp The power-up that affects the score.
     * @return The updated score after applying the power-up.
     */
    private fun applyPowerUpToScore(currentScore: Int, powerUp: PowerUp): Int {
        return when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                Log.d(TAG, "Applying DOUBLE_POINTS power-up.")
                currentScore * 2
            }
            PowerUpType.BONUS_POINTS -> {
                Log.d(TAG, "Applying BONUS_POINTS power-up with value: ${powerUp.effectValue}.")
                currentScore + powerUp.effectValue
            }
            else -> {
                Log.d(TAG, "No applicable power-up effect.")
                currentScore
            }
        }
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
            player.addBasePoints(pointsPerPlayer)
            Log.d(TAG, "${pointsPerPlayer} points added to ${player.name}.")
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
