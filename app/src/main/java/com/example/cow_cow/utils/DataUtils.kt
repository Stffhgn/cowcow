package com.example.cow_cow.utils

import android.content.Context
import com.example.cow_cow.models.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataUtils {

    // Constants for SharedPreferences keys
    private const val PLAYERS_PREFS = "players_prefs"
    private const val PLAYERS_KEY = "players_key"
    private const val TEAM_KEY = "team_key"

    private val gson = Gson()

    // ----- Player Data Management -----

    /**
     * Saves the list of players to SharedPreferences.
     */
    fun savePlayers(context: Context, players: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val playersJson = gson.toJson(players)
        editor.putString(PLAYERS_KEY, playersJson)
        editor.apply()
    }

    /**
     * Loads the list of players from SharedPreferences.
     */
    fun loadPlayers(context: Context): List<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val playersJson = sharedPreferences.getString(PLAYERS_KEY, null)
        return if (playersJson != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson(playersJson, type)
        } else {
            emptyList()
        }
    }

    // ----- Team Data Management -----

    /**
     * Saves the team data to SharedPreferences.
     */
    fun saveTeam(context: Context, team: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val teamJson = gson.toJson(team)
        editor.putString(TEAM_KEY, teamJson)
        editor.apply()
    }

    /**
     * Loads the team data from SharedPreferences.
     */
    fun loadTeam(context: Context): List<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val teamJson = sharedPreferences.getString(TEAM_KEY, null)
        return if (teamJson != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson(teamJson, type)
        } else {
            emptyList()
        }
    }

    /**
     * Clears the team data from SharedPreferences.
     */
    fun clearTeam(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(TEAM_KEY)
        editor.apply()
    }
}
