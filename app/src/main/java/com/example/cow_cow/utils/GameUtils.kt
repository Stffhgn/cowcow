package com.example.cow_cow.utils

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object GameUtils {

    /**
     * Adds the relevant object to the player's count.
     */
    fun addCowToPlayer(player: Player) {
        player.cowCount += 1
    }

    fun addChurchToPlayer(player: Player) {
        player.churchCount += 1
    }

    fun addWaterTowerToPlayer(player: Player) {
        player.waterTowerCount += 1
    }

    /**
     * Resets a player's counts for Cow, Church, and Water Tower.
     */
    fun resetPlayerCounts(player: Player) {
        player.cowCount = 0
        player.churchCount = 0
        player.waterTowerCount = 0
    }

    /**
     * Resets all players' counts for Cow, Church, and Water Tower.
     */
    fun resetAllPlayersCounts(players: List<Player>) {
        players.forEach { resetPlayerCounts(it) }
    }

    /**
     * Distributes points evenly among all team members.
     */
    fun distributeTeamPoints(team: Team, totalPoints: Int) {
        if (team.members.isNotEmpty()) {
            val pointsPerPlayer = totalPoints / team.members.size
            team.members.forEach { it.cowCount += pointsPerPlayer }
            updateTeamScore(team)
        }
    }

    /**
     * Updates the total team score based on member scores.
     */
    private fun updateTeamScore(team: Team) {
        team.teamScore = team.members.sumOf { it.calculateTotalPoints() }
    }

    /**
     * Determines the player with the most cows called.
     */
    fun getPlayerWithMostCows(players: List<Player>): Player? {
        return players.maxByOrNull { it.cowCount }
    }

    /**
     * Returns the player with the highest total score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        return players.maxByOrNull { it.calculateTotalPoints() }
    }
}
