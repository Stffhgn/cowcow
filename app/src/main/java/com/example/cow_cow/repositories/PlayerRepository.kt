package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.cow_cow.models.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerRepository {

    // Constants for SharedPreferences
    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val PLAYERS_KEY = "PLAYERS_KEY"
    private val TEAM_KEY = "TEAM_KEY"
    private val gson = Gson()

    /**
     * Get the SharedPreferences instance.
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Retrieve the list of players from SharedPreferences.
     */
    fun getPlayers(context: Context): List<Player> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(PLAYERS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    /**
     * Save the list of players to SharedPreferences.
     */
    fun savePlayers(players: List<Player>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()
    }

    /**
     * Update a player's information and save to SharedPreferences.
     */
    fun updatePlayer(player: Player, context: Context) {
        val players = getPlayers(context).toMutableList()
        val index = players.indexOfFirst { it.id == player.id }
        if (index != -1) {
            players[index] = player
            savePlayers(players, context)
        }
    }

    /**
     * Remove a player by their ID and save changes.
     */
    fun removePlayerById(playerId: Int, context: Context) {
        val players = getPlayers(context).toMutableList()
        val updatedPlayers = players.filter { it.id != playerId }
        savePlayers(updatedPlayers, context)
    }

    /**
     * Retrieve the team from SharedPreferences.
     */
    fun getTeam(context: Context): List<Player> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(TEAM_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    /**
     * Save the team to SharedPreferences.
     */
    fun saveTeam(team: List<Player>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(team)
        editor.putString(TEAM_KEY, json)
        editor.apply()
    }
}
