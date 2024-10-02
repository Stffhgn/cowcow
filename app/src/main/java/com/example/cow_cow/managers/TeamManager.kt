package com.example.cow_cow.managers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object TeamManager {

    private val teams = mutableListOf<Team>()  // List to store all teams

    /**
     * Create a new team.
     *
     * @param teamName Name of the team.
     * @return Newly created Team object.
     */
    fun createTeam(teamName: String): Team {
        val newTeam = Team(mutableListOf(), teamName)
        teams.add(newTeam)
        return newTeam
    }

    /**
     * Add a player to a team.
     *
     * @param player The player to be added to the team.
     * @param team The team to which the player is being added.
     */
    fun addPlayerToTeam(player: Player, team: Team) {
        if (!team.members.contains(player)) {
            team.members.add(player)
            updateTeamScore(team)
        }
    }

    /**
     * Remove a player from a team.
     *
     * @param player The player to be removed.
     * @param team The team from which the player is being removed.
     */
    fun removePlayerFromTeam(player: Player, team: Team) {
        if (team.members.contains(player)) {
            team.members.remove(player)
            updateTeamScore(team)
        }
    }

    /**
     * Get the team score by calculating the sum of all member scores.
     *
     * @param team The team whose score is being calculated.
     * @return Total score of the team.
     */
    fun updateTeamScore(team: Team) {
        team.teamScore = team.members.sumOf { it.calculateTotalPoints() }
    }

    /**
     * Distribute points evenly across team members.
     *
     * @param team The team to distribute points to.
     * @param points The total points to be distributed.
     */
    fun distributeTeamPoints(team: Team, points: Int) {
        val pointsPerPlayer = points / team.members.size
        team.members.forEach { player ->
            player.basePoints += pointsPerPlayer
        }
        updateTeamScore(team)
    }

    /**
     * Get all the teams currently managed.
     *
     * @return A list of all teams.
     */
    fun getAllTeams(): List<Team> {
        return teams
    }

    /**
     * Get a specific team by its name.
     *
     * @param teamName The name of the team to search for.
     * @return The matching Team object, or null if no match is found.
     */
    fun getTeamByName(teamName: String): Team? {
        return teams.find { it.name == teamName }
    }

    /**
     * Reset all team scores and members, typically before starting a new game.
     */
    fun resetAllTeams() {
        teams.forEach { team ->
            team.members.clear()
            team.teamScore = 0
        }
    }

    /**
     * Remove a team entirely.
     *
     * @param team The team to be removed.
     */
    fun removeTeam(team: Team) {
        teams.remove(team)
    }

    /**
     * Check if a player is already part of a team.
     *
     * @param player The player to check.
     * @return True if the player is part of any team, false otherwise.
     */
    fun isPlayerInAnyTeam(player: Player): Boolean {
        return teams.any { it.members.contains(player) }
    }
}