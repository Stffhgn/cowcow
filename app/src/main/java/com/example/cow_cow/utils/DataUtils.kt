package com.example.cow_cow.utils

import android.content.Context
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.TeamRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataUtils {

    // Constants for SharedPreferences keys
    private const val PLAYERS_PREFS = "players_prefs"
    private const val PLAYERS_KEY = "players_key"
    private const val TEAM_KEY = "team_key"
    private const val ACHIEVEMENTS_KEY = "achievements_key"
    private const val CUSTOM_RULES_PREFS = "custom_rules_prefs"
    private const val CUSTOM_RULES_KEY = "custom_rules_key"

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
    fun saveTeam(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val teamRepository = TeamRepository(context)
        val team = teamRepository.getTeam()

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

    // ----- Achievements Data Management -----

    /**
     * Saves the list of achievements to SharedPreferences.
     */
    fun saveAchievements(context: Context, achievements: List<Achievement>) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val achievementsJson = gson.toJson(achievements)
        editor.putString(ACHIEVEMENTS_KEY, achievementsJson)
        editor.apply()
    }

    /**
     * Loads the list of achievements from SharedPreferences.
     */
    fun loadAchievements(context: Context): List<Achievement> {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val achievementsJson = sharedPreferences.getString(ACHIEVEMENTS_KEY, null)
        return if (achievementsJson != null) {
            val type = object : TypeToken<List<Achievement>>() {}.type
            gson.fromJson(achievementsJson, type)
        } else {
            emptyList()
        }
    }

    /**
     * Clears the achievements data from SharedPreferences.
     */
    fun clearAchievements(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PLAYERS_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(ACHIEVEMENTS_KEY)
        editor.apply()
    }

    // ----- Custom Rules Data Management -----

    /**
     * Saves the list of custom rules to SharedPreferences.
     */
    fun saveCustomRules(context: Context, customRules: List<CustomRule>) {
        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val customRulesJson = gson.toJson(customRules)
        editor.putString(CUSTOM_RULES_KEY, customRulesJson)
        editor.apply()
    }

    /**
     * Loads the list of custom rules from SharedPreferences.
     */
    fun loadCustomRules(context: Context): List<CustomRule> {
        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val customRulesJson = sharedPreferences.getString(CUSTOM_RULES_KEY, null)
        return if (customRulesJson != null) {
            val type = object : TypeToken<List<CustomRule>>() {}.type
            gson.fromJson(customRulesJson, type)
        } else {
            emptyList()
        }
    }

    /**
     * Clears the custom rules from SharedPreferences.
     */
    fun clearCustomRules(context: Context) {
        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(CUSTOM_RULES_KEY)
        editor.apply()
    }
}
