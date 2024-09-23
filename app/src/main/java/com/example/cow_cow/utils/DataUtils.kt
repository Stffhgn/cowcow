package com.example.cow_cow.utils

import android.content.Context
import com.example.cow_cow.models.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataUtils {

    private const val PLAYERS_PREFS = "players_prefs"
    private const val PLAYERS_KEY = "players_key"
    private const val TEAM_KEY = "team_key"

    // Save players data
    fun savePlayers(context: Context, players: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val playersJson = gson.toJson(players)
        editor.putString(PLAYERS_KEY, playersJson)
        editor.apply()
    }

    // Load players data
    fun loadPlayers(context: Context): List<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val playersJson = sharedPreferences.getString(PLAYERS_KEY, null)
        return if (playersJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Player>>() {}.type
            gson.fromJson(playersJson, type)
        } else {
            mutableListOf()
        }
    }

    // Save team data
    fun saveTeam(context: Context, team: List<Player>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val teamJson = gson.toJson(team)
        editor.putString(TEAM_KEY, teamJson)
        editor.apply()
    }

    // Load team data
    fun loadTeam(context: Context): MutableList<Player> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val teamJson = sharedPreferences.getString(TEAM_KEY, null)
        return if (teamJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Player>>() {}.type
            gson.fromJson(teamJson, type)
        } else {
            mutableListOf()
        }
    }

    // Optional: Clear team data
    fun clearTeam(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(TEAM_KEY)
        editor.apply()
    }
}
