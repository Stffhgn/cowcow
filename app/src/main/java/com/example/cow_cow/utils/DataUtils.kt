package com.example.cow_cow.utils

import android.content.Context
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataUtils {

    // Constants for SharedPreferences keys
    private const val PLAYERS_PREFS = "players_prefs"  // Name of the SharedPreferences file
    private const val PLAYERS_KEY = "players_key"      // Key to store the list of players
    private const val TEAM_KEY = "team_key"            // Key to store the team data
    private const val SCAVENGER_HUNT_KEY = "scavenger_hunt_key"  // Key to store scavenger hunt data

    // ----- Player Data Management -----

    /**
     * Saves the list of players to SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param players The list of Player objects to be saved.
     */
    fun savePlayers(context: Context, players: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val playersJson = gson.toJson(players)  // Convert the list of players to JSON
        editor.putString(PLAYERS_KEY, playersJson) // save the serialized JSON
        editor.apply()  // Commit changes
    }

    /**
     * Loads the list of players from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @return A list of Player objects.
     */
    fun loadPlayers(context: Context): List<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val playersJson = sharedPreferences.getString(PLAYERS_KEY, null)
        return if (playersJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Player>>() {}.type
            gson.fromJson(playersJson, type)  // Convert JSON back to list of Player objects
        } else {
            mutableListOf()  // Return an empty list if no players are saved
        }
    }

    // ----- Team Data Management -----

    /**
     * Saves the team data to SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param team The list of Player objects representing the team.
     */
    fun saveTeam(context: Context, team: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val teamJson = gson.toJson(team)  // Convert the team data to JSON
        editor.putString(TEAM_KEY, teamJson)
        editor.apply()  // Commit changes
    }

    /**
     * Loads the team data from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @return A list of Player objects representing the team.
     */
    fun loadTeam(context: Context): MutableList<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val teamJson = sharedPreferences.getString(TEAM_KEY, null)
        return if (teamJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Player>>() {}.type
            gson.fromJson(teamJson, type)  // Convert JSON back to list of Player objects
        } else {
            mutableListOf()  // Return an empty list if no team data is saved
        }
    }

    /**
     * Clears the team data from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     */
    fun clearTeam(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(TEAM_KEY)  // Remove the saved team data
        editor.apply()  // Commit changes
    }

    // ----- Scavenger Hunt Data Management -----

    /**
     * Saves the scavenger hunt data to SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param scavengerHuntList The list of ScavengerHuntItem objects to be saved.
     */
    fun saveScavengerHunt(context: Context, scavengerHuntList: List<ScavengerHuntItem>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val scavengerHuntJson = gson.toJson(scavengerHuntList)  // Convert the scavenger hunt list to JSON
        editor.putString(SCAVENGER_HUNT_KEY, scavengerHuntJson)
        editor.apply()  // Commit changes
    }

    /**
     * Loads the scavenger hunt data from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @return A list of ScavengerHuntItem objects.
     */
    fun loadScavengerHunt(context: Context): MutableList<ScavengerHuntItem> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val scavengerHuntJson = sharedPreferences.getString(SCAVENGER_HUNT_KEY, null)
        return if (scavengerHuntJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<ScavengerHuntItem>>() {}.type
            gson.fromJson(scavengerHuntJson, type)  // Convert JSON back to list of ScavengerHuntItem objects
        } else {
            mutableListOf()  // Return an empty list if no scavenger hunt data is saved
        }
    }

    /**
     * Clears the scavenger hunt data from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     */
    fun clearScavengerHunt(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(SCAVENGER_HUNT_KEY)  // Remove the saved scavenger hunt data
        editor.apply()  // Commit changes
    }
}