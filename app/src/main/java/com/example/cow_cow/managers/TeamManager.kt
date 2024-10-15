package com.example.cow_cow.managers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object TeamManager {

    private var team: Team? = null  // Single instance of the team

    /**
     * Create a new team.
     *
     * @param teamName Name of the team.
     * @return Newly created Team object.
     */
    fun createTeam(teamName: String): Team {
        team = Team(id = "1", name = teamName, members = mutableListOf())
        return team!!
    }

    /**
     * Get the current team. If the team doesn't exist, create a default one.
     *
     * @return The current team.
     */
    fun getTeam(): Team {
        if (team == null) {
            team = Team(id = "1", name = "Default Team", members = mutableListOf())
        }
        return team!!
    }

    /**
     * Add a player to the team.
     *
     * @param player The player to be added to the team.
     */
    fun addPlayerToTeam(player: Player) {
        team?.let {
            if (!it.members.contains(player)) {
                it.addMember(player)
                player.isOnTeam = true // Update the isOnTeam property
                updateTeamScore()
            }
        }
    }

    /**
     * Remove a player from the team.
     *
     * @param player The player to be removed.
     */
    fun removePlayerFromTeam(player: Player) {
        team?.let {
            if (it.members.contains(player)) {
                it.removeMember(player)
                player.isOnTeam = false // Update the isOnTeam property
                updateTeamScore()
            }
        }
    }

    /**
     * Update the team score by calculating the sum of all member scores.
     */
    fun updateTeamScore() {
        team?.let {
            it.teamScore = it.members.sumOf { player -> player.calculateTotalPoints() }
        }
    }

    /**
     * Distribute points evenly across team members.
     *
     * @param points The total points to be distributed.
     */
    fun distributeTeamPoints(points: Int) {
        team?.let {
            if (it.members.isNotEmpty()) {
                val pointsPerPlayer = points / it.members.size
                it.members.forEach { player ->
                    player.basePoints += pointsPerPlayer
                }
                updateTeamScore()
            }
        }
    }

    /**
     * Reset the team by clearing members and resetting the score.
     */
    fun resetTeam() {
        team?.let {
            it.members.clear()
            it.teamScore = 0
        }
    }

    /**
     * Check if a player is part of the team.
     *
     * @param player The player to check.
     * @return True if the player is part of the team, false otherwise.
     */
    fun isPlayerInTeam(player: Player): Boolean {
        return team?.members?.contains(player) ?: false
    }
}
