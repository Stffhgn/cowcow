package com.example.cow_cow.managers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.PenaltyType

object ScoreManager {

    /**
     * Calculate total score for a single player, factoring in base points, bonuses, penalties,
     * and active power-ups.
     *
     * @param player The player whose score is being calculated.
     * @return The player's total score.
     */
    fun calculatePlayerScore(player: Player): Int {
        var totalPoints = player.basePoints + player.bonusPoints

        // Subtract penalties
        totalPoints -= player.penaltyPoints

        // Apply power-up effects
        player.activePowerUps.filter { it.isActive }.forEach { powerUp ->
            totalPoints = applyPowerUpToScore(totalPoints, powerUp)
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
            PowerUpType.DOUBLE_POINTS -> currentScore * 2
            PowerUpType.BONUS_POINTS -> currentScore + powerUp.effectValue
            PowerUpType.EXTRA_POINTS -> currentScore + powerUp.effectValue
            else -> currentScore
        }
    }

    /**
     * Apply a penalty to a player, adjusting their score accordingly.
     *
     * @param player The player receiving the penalty.
     * @param penalty The penalty to apply.
     */
    fun applyPenalty(player: Player, penalty: Penalty) {
        when (penalty.penaltyType) {
            PenaltyType.POINT_DEDUCTION -> player.penaltyPoints += penalty.pointsDeducted
            PenaltyType.SILENCED -> player.isSilenced = true  // Example of non-point penalty
            // Add more penalty handling logic as needed
        }
    }

    /**
     * Calculate the total score for a team by summing up all player scores.
     *
     * @param team The team whose score is being calculated.
     * @return The team's total score.
     */
    fun calculateTeamScore(team: Team): Int {
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
        team.members.forEach { player ->
            player.addBasePoints(pointsPerPlayer)
        }
    }

    /**
     * Compare and get the player with the highest score.
     *
     * @param players The list of players to compare.
     * @return The player with the highest score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        return players.maxByOrNull { calculatePlayerScore(it) }
    }

    /**
     * Handle leaderboard logic by sorting players based on their scores.
     *
     * @param players The list of players to rank.
     * @return The list of players sorted by their scores in descending order.
     */
    fun getLeaderboard(players: List<Player>): List<Player> {
        return players.sortedByDescending { calculatePlayerScore(it) }
    }

    /**
     * Resets the scores for all players, typically at the start of a new game.
     *
     * @param players The list of players to reset.
     */
    fun resetPlayerScores(players: List<Player>) {
        players.forEach { player ->
            player.basePoints = 0
            player.bonusPoints = 0
            player.penaltyPoints = 0
            player.activePowerUps.clear()
            player.penalties.clear()
        }
    }

    /**
     * Resets team scores by clearing individual player scores within the team.
     *
     * @param team The team whose scores should be reset.
     */
    fun resetTeamScores(team: Team) {
        resetPlayerScores(team.members)
        team.teamScore = 0
    }
}
