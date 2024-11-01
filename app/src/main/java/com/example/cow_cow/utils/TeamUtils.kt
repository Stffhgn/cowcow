package com.example.cow_cow.utils

import android.util.Log
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

class TeamUtils(private val scoreManager: ScoreManager) {

    private val adjectives = listOf("Thunder", "Mighty", "Wild", "Swift", "Fearless", "Iron", "Dynamic")
    private val nouns = listOf("Eagles", "Racers", "Wolves", "Titans", "Lions", "Sharks", "Panthers")

    /**
     * Generates a random team name using a random adjective and noun.
     */
    fun generateRandomTeamName(): String {
        val adjective = adjectives.random()
        val noun = nouns.random()
        return "$adjective $noun"
    }

    /**
     * Adds a player to the team if they are not already a member and updates the team score.
     */
    fun addPlayerToTeam(player: Player, team: Team) {
        if (!team.members.contains(player)) {
            team.members.add(player)
            updateTeamScore(team)
        }
    }

    /**
     * Removes a player from the team if they are a member and updates the team score.
     */
    fun removePlayerFromTeam(player: Player, team: Team) {
        if (team.members.contains(player)) {
            team.members.remove(player)
            updateTeamScore(team)
        }
    }

    /**
     * Updates the total team score by summing the scores of players who are currently on the team.
     */
    private fun updateTeamScore(team: Team) {
        team.teamScore = team.members
            .filter { it.isOnTeam }
            .sumOf { scoreManager.calculatePlayerScore(it) }

        Log.d("TeamUtils", "Updated team score for team '${team.name}': ${team.teamScore}")
    }

    /**
     * Distributes points evenly among all team members and updates the team score.
     */
    fun distributeTeamPoints(team: Team, totalPoints: Int) {
        val pointsPerPlayer = if (team.members.isNotEmpty()) totalPoints / team.members.size else 0
        team.members.forEach { it.cowCount += pointsPerPlayer }
        updateTeamScore(team)
    }
}
