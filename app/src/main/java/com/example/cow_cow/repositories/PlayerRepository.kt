package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.CustomRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerRepository {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val PLAYERS_KEY = "PLAYERS_KEY"
    private val TEAM_KEY = "TEAM_KEY"
    private val gson = Gson()

    private val _playersLiveData = MutableLiveData<List<Player>>()
    val playersLiveData: LiveData<List<Player>> get() = _playersLiveData

    private val _teamLiveData = MutableLiveData<List<Player>>()
    val teamLiveData: LiveData<List<Player>> get() = _teamLiveData

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

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

    fun savePlayers(players: List<Player>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()

        _playersLiveData.value = players
    }

    fun updatePlayer(player: Player, context: Context) {
        val players = getPlayers(context).toMutableList()
        val index = players.indexOfFirst { it.id == player.id }
        if (index != -1) {
            players[index] = player
            savePlayers(players, context)
        }
    }

    // New method to update the player's name
    fun updatePlayerName(playerId: Int, newName: String, context: Context) {
        val players = getPlayers(context).toMutableList()
        val index = players.indexOfFirst { it.id == playerId }
        if (index != -1) {
            players[index] = players[index].copy(name = newName)
            savePlayers(players, context)
        }
    }

    fun removePlayerById(playerId: Int, context: Context) {
        val players = getPlayers(context).toMutableList()
        val updatedPlayers = players.filter { it.id != playerId }
        savePlayers(updatedPlayers, context)
    }


    fun getPlayersSortedByScore(context: Context): List<Player> {
        val players = getPlayers(context)
        return players.sortedByDescending { it.calculateTotalPoints() }
    }

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

    fun saveTeam(team: List<Player>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(team)
        editor.putString(TEAM_KEY, json)
        editor.apply()

        _teamLiveData.value = team
    }

    fun getAchievements(context: Context, playerId: Int): List<Achievement> {
        val player = getPlayerById(context, playerId)
        return player?.achievements ?: emptyList()
    }

    fun getPenalties(context: Context, playerId: Int): List<Penalty> {
        val player = getPlayerById(context, playerId)
        return player?.penalties ?: emptyList()
    }

    fun getCustomRules(context: Context, playerId: Int): List<CustomRule> {
        val player = getPlayerById(context, playerId)
        return player?.customRules ?: emptyList()
    }

    private fun getPlayerById(context: Context, playerId: Int): Player? {
        val players = getPlayers(context)
        return players.find { it.id == playerId }
    }

    fun updatePlayerAchievements(context: Context, playerId: Int, achievements: List<Achievement>) {
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(achievements = achievements.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        }
    }

    fun updatePlayerPenalties(context: Context, playerId: Int, penalties: List<Penalty>) {
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(penalties = penalties.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        }
    }

    fun updatePlayerCustomRules(context: Context, playerId: Int, customRules: List<CustomRule>) {
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(customRules = customRules.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        }
    }
}
