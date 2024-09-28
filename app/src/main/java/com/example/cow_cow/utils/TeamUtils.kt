package com.example.cow_cow.utils

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object TeamUtils {

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
     * Updates the team's total score based on its members' points.
     */
    fun updateTeamScore(team: Team) {
        team.teamScore = team.members.sumOf { it.calculateTotalPoints() }
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
