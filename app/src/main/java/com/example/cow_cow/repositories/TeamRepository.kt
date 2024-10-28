package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TeamRepository(private val context: Context) {

    private val PREFS_NAME = "com.example.cow_cow.TEAM_PREFERENCES"
    private val TEAM_KEY = "TEAM_KEY"
    private val gson = Gson()
    private var team: Team? = null

    /**
     * Get SharedPreferences instance
     */
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Adds a new team to the repository by saving it to SharedPreferences.
     *
     * @param newTeam The team to add to the repository.
     */
    fun addTeam(newTeam: Team) {
        // Assign new team to the repository's team variable
        team = newTeam
        saveTeam()  // Save team to SharedPreferences
        Log.d("TeamRepository", "New team added: ${newTeam.name} with members: ${newTeam.members.size}")
    }

    /**
     * Remove the current team from the repository by deleting it from SharedPreferences.
     */
    fun removeTeam() {
        val prefs = getSharedPreferences()
        val editor = prefs.edit()
        editor.remove(TEAM_KEY)
        editor.apply()  // Commit the changes

        team = null  // Reset the team to null
        Log.d("TeamRepository", "Team removed from SharedPreferences")
    }

    /**
     * Load the single team from SharedPreferences.
     */
    fun loadTeam() {
        val prefs = getSharedPreferences()
        val json = prefs.getString(TEAM_KEY, null)
        if (json != null) {
            val type = object : TypeToken<Team>() {}.type
            team = gson.fromJson(json, type)
            Log.d("TeamRepository", "Loaded team: ${team?.name} with members: ${team?.members?.size ?: 0}")
        } else {
            // Create a default team if none exists
            team = Team(id = "1", name = "Default Team", members = mutableListOf())
            Log.d("TeamRepository", "No team found, created default team: ${team?.name}")
        }
    }

    /**
     * Save the current team to SharedPreferences.
     */
    fun saveTeam() {
        team?.let {
            val prefs = getSharedPreferences()
            val editor = prefs.edit()
            val json = gson.toJson(it)
            editor.putString(TEAM_KEY, json)
            editor.apply() // Commit the changes
            Log.d("TeamRepository", "Team saved to SharedPreferences: ${it.name}")
        }
    }

    /**
     * Get the current team (creates a default if none exists).
     */
    fun getTeam(): Team {
        if (team == null) {
            loadTeam()

            // If loading the team still results in `team` being null, create a default one
            if (team == null) {
                Log.w("TeamRepository", "No existing team found, creating a new default team.")
                team = Team(id = "1", name = "Default Team", members = mutableListOf())
                saveTeam() // Save this new default team
            }
        }
        return team!!
    }


    /**
     * Add a player to the team and save changes.
     */
    fun addPlayerToTeam(player: Player) {
        team?.let {
            if (!it.members.contains(player)) {
                it.addMember(player)
                saveTeam()
                Log.d("TeamRepository", "Added player ${player.name} to team ${it.name}")
            } else {
                Log.d("TeamRepository", "Player ${player.name} is already in team ${it.name}")
            }
        }
    }

    /**
     * Remove a player from the team and save changes.
     */
    fun removePlayerFromTeam(player: Player) {
        team?.let {
            if (it.members.contains(player)) {
                it.removeMember(player)
                saveTeam()
                Log.d("TeamRepository", "Removed player ${player.name} from team ${it.name}")
            } else {
                Log.d("TeamRepository", "Player ${player.name} not found in team ${it.name}")
            }
        }
    }

    /**
     * Distribute points to the team and save changes.
     */
    fun distributeTeamPoints(points: Int) {
        team?.let {
            it.teamScore += points
            saveTeam()
            Log.d("TeamRepository", "Distributed $points points to team ${it.name}. New team score: ${it.teamScore}")
        }
    }

    /**
     * Reset the team and save changes.
     */
    fun resetTeam() {
        team = Team(id = "1", name = "Default Team", members = mutableListOf())
        saveTeam()
        Log.d("TeamRepository", "Team has been reset to default.")
    }
}
