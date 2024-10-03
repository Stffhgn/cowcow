package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.cow_cow.managers.TeamManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TeamRepository(private val context: Context) {

    private val PREFS_NAME = "com.example.cow_cow.TEAM_PREFERENCES"
    private val TEAMS_KEY = "TEAMS_KEY"
    private val gson = Gson()
    private val teams = mutableListOf<Team>()

    /**
     * Get SharedPreferences instance
     */
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Load teams from SharedPreferences and update TeamManager.
     */
    fun loadTeams() {
        val prefs = getSharedPreferences()
        val json = prefs.getString(TEAMS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<List<Team>>() {}.type
            val loadedTeams: List<Team> = gson.fromJson(json, type)

            // Reset the TeamManager and populate it with the loaded teams
            TeamManager.resetAllTeams()
            loadedTeams.forEach {
                val team = TeamManager.createTeam(it.name).apply { members.addAll(it.members) }
                teams.add(team) // Synchronize repository's teams list
            }
        }
    }

    /**
     * Save all teams to SharedPreferences.
     */
    private fun saveTeams() {
        val prefs = getSharedPreferences()
        val editor = prefs.edit()
        val json = gson.toJson(teams)
        editor.putString(TEAMS_KEY, json)
        editor.apply() // Commit the changes
    }

    /**
     * Save a single team and persist it.
     */
    fun saveTeam(team: Team) {
        val index = teams.indexOfFirst { it.id == team.id }
        if (index != -1) {
            teams[index] = team
        } else {
            teams.add(team)
        }
        saveTeams() // Persist changes after saving the team
    }

    /**
     * Get the current team (or create a default one if none exists).
     */
    fun getTeam(): Team {
        return teams.firstOrNull() ?: Team(id = 1, name = "Team", members = mutableListOf())
    }

    /**
     * Create a new team, add it to the TeamManager, and save it.
     */
    fun createTeam(teamName: String): Team {
        val newTeam = TeamManager.createTeam(teamName)
        teams.add(newTeam)
        saveTeams()
        return newTeam
    }

    /**
     * Add a player to a team, update the score, and save the team.
     */
    fun addPlayerToTeam(player: Player, team: Team) {
        TeamManager.addPlayerToTeam(player, team)
        saveTeam(team) // Persist the changes after updating the team
    }

    /**
     * Remove a player from a team and save the changes.
     */
    fun removePlayerFromTeam(player: Player, team: Team) {
        TeamManager.removePlayerFromTeam(player, team)
        saveTeam(team) // Persist the changes after updating the team
    }

    /**
     * Distribute points to the team and save the changes.
     */
    fun distributeTeamPoints(team: Team, points: Int) {
        TeamManager.distributeTeamPoints(team, points)
        saveTeam(team) // Persist the changes after distributing points
    }

    /**
     * Get a team by its name from TeamManager.
     */
    fun getTeamByName(teamName: String): Team? {
        return TeamManager.getTeamByName(teamName)
    }

    /**
     * Get all teams from TeamManager.
     */
    fun getAllTeams(): List<Team> {
        return TeamManager.getAllTeams()
    }

    /**
     * Reset all teams via TeamManager and save the changes.
     */
    fun resetAllTeams() {
        TeamManager.resetAllTeams()
        teams.clear() // Clear the local list of teams
        saveTeams()   // Persist the reset state
    }

    /**
     * Remove a team and save the changes.
     */
    fun removeTeam(team: Team) {
        TeamManager.removeTeam(team)
        teams.remove(team) // Remove it from the repository's list as well
        saveTeams()        // Persist the removal
    }

    /**
     * Check if a player is part of any team via TeamManager.
     */
    fun isPlayerInAnyTeam(player: Player): Boolean {
        return TeamManager.isPlayerInAnyTeam(player)
    }
}
