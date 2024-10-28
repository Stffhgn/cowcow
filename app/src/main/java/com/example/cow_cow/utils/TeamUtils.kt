package com.example.cow_cow.utils

import android.util.Log
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object TeamUtils {

    private val adjectives = listOf("Thunder", "Mighty", "Wild", "Swift", "Fearless", "Iron", "Dynamic")
    private val nouns = listOf("Eagles", "Racers", "Wolves", "Titans", "Lions", "Sharks", "Panthers")

    fun generateRandomTeamName(): String {
        val adjective = adjectives.random()
        val noun = nouns.random()
        return "$adjective $noun"
    }

    /**
     * Adds a player to a team.
     */
    fun addPlayerToTeam(player: Player, team: Team) {
        if (!team.members.contains(player)) {
            team.members.add(player)
            updateTeamScore(team)
        }
    }

    /**
     * Removes a player from a team.
     */
    fun removePlayerFromTeam(player: Player, team: Team) {
        if (team.members.contains(player)) {
            team.members.remove(player)
            updateTeamScore(team)
        }
    }

    /**
     * Updates the total team score based on the scores of players who are on the team.
     */
    private fun updateTeamScore(team: Team) {
        // Sum the points for all players who are marked as being on the team.
        team.teamScore = team.members
            .filter { it.isOnTeam }
            .sumOf { ScoreManager.calculatePlayerScore(it) }

        Log.d("GameUtils", "Updated team score for team '${team.name}': ${team.teamScore}")
    }

    /**
     * Distributes points evenly among team members.
     */
    fun distributeTeamPoints(team: Team, totalPoints: Int) {
        val pointsPerPlayer = totalPoints / team.members.size
        team.members.forEach { it.cowCount += pointsPerPlayer }
        updateTeamScore(team)
    }
}
