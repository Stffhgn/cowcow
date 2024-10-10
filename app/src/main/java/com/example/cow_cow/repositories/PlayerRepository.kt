package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.*
import com.example.cow_cow.utils.PlayerIDGenerator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerRepository(private val context: Context) {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val PLAYERS_KEY = "PLAYERS_KEY"
    private val TEAM_KEY = "TEAM_KEY"
    private val gson = Gson()

    private val _playersLiveData = MutableLiveData<List<Player>>()
    val playersLiveData: LiveData<List<Player>> get() = _playersLiveData

    private val _teamLiveData = MutableLiveData<List<Player>>()
    val teamLiveData: LiveData<List<Player>> get() = _teamLiveData

    private var currentPlayer: Player? = null

    /**
     * Retrieves SharedPreferences for storing player data.
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Fetches the list of players from SharedPreferences.
     */
    fun getPlayers(context: Context): List<Player> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(PLAYERS_KEY, null)
        val playerList = if (json != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson<List<Player>>(json, type)
        } else {
            emptyList()
        }

        _playersLiveData.postValue(playerList)
        return playerList
    }

    /**
     * Saves the list of players to SharedPreferences.
     */
    fun savePlayers(players: List<Player>, context: Context) {
        Log.d("PlayerRepository", "Saving players to SharedPreferences.")
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()
        Log.d("PlayerRepository", "Players saved to SharedPreferences: ${players.size} players")

        // Update the LiveData after saving the players
        _playersLiveData.postValue(players)
    }

    /**
     * Adds a new player to the list and saves it to SharedPreferences.
     */
    fun addPlayer(name: String, teamName: String? = null, userLogin: String? = null): Player {
        Log.d("PlayerRepository", "Adding new player: $name")
        val players = getPlayers(context).toMutableList()

        // Generate a unique ID for the new player
        val playerId = PlayerIDGenerator.generatePlayerID(teamName, userLogin)

        val newPlayer = Player(
            id = playerId,
            name = name,
        )
        players.add(newPlayer)
        savePlayers(players, context)
        Log.d("PlayerRepository", "New player added with ID: ${newPlayer.id}")

        return newPlayer
    }

    /**
     * Updates a specific player and saves the updated list to SharedPreferences.
     */
    fun updatePlayer(player: Player, context: Context) {
        Log.d("PlayerRepository", "Updating player with ID: ${player.id}.")
        val players = getPlayers(context).toMutableList()
        val index = players.indexOfFirst { it.id == player.id }
        if (index != -1) {
            players[index] = player
            savePlayers(players, context)
            Log.d("PlayerRepository", "Player with ID: ${player.id} updated successfully.")
        } else {
            Log.e("PlayerRepository", "Player with ID: ${player.id} not found.")
        }
    }

    /**
     * Retrieves player-specific settings from SharedPreferences.
     */
    fun getPlayerSettings(context: Context, playerId: String): PlayerSettings {
        Log.d("PlayerRepository", "Fetching settings for player ID: $playerId.")
        val sharedPreferences = getSharedPreferences(context)
        val settingsJson = sharedPreferences.getString("player_settings_$playerId", null)

        return if (settingsJson != null) {
            gson.fromJson(settingsJson, PlayerSettings::class.java)
        } else {
            Log.d("PlayerRepository", "No settings found for player ID: $playerId. Using default settings.")
            PlayerSettings() // Return default settings
        }
    }

    // Set current player (can be called when the user logs in or session starts)
    fun setCurrentPlayer(player: Player) {
        currentPlayer = player
    }

    // Fetch the globally stored current player
    fun getCurrentPlayer(): Player? {
        return currentPlayer
    }

    /**
     * Updates the player's name in SharedPreferences.
     */
    fun updatePlayerName(playerId: String, newName: String, context: Context) {
        Log.d("PlayerRepository", "Updating name for player ID: $playerId.")
        val players = getPlayers(context).toMutableList()
        val index = players.indexOfFirst { it.id == playerId }
        if (index != -1) {
            players[index] = players[index].copy(name = newName)
            savePlayers(players, context)
            Log.d("PlayerRepository", "Player name updated to $newName for player ID: $playerId.")
        } else {
            Log.e("PlayerRepository", "Player with ID: $playerId not found.")
        }
    }

    /**
     * Saves player-specific settings to SharedPreferences.
     */
    fun savePlayerSettings(playerId: String, settings: PlayerSettings, context: Context) {
        Log.d("PlayerRepository", "Saving settings for player ID: $playerId.")
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()

        // Convert settings to JSON format and save in SharedPreferences
        val settingsJson = gson.toJson(settings)
        editor.putString("player_settings_$playerId", settingsJson)
        editor.apply()

        Log.d("PlayerRepository", "Settings saved successfully for player ID: $playerId.")
    }

    /**
     * Removes a player by their ID from the list.
     */
    fun removePlayerById(playerId: String, context: Context) {
        Log.d("PlayerRepository", "Removing player with ID: $playerId.")
        val players = getPlayers(context).toMutableList()
        val updatedPlayers = players.filter { it.id != playerId }
        savePlayers(updatedPlayers, context)
        Log.d("PlayerRepository", "Player with ID: $playerId removed successfully.")
    }

    /**
     * Returns the list of players sorted by their total score.
     */
    fun getPlayersSortedByScore(context: Context): List<Player> {
        Log.d("PlayerRepository", "Sorting players by score.")
        val players = getPlayers(context)
        return players.sortedByDescending { it.calculateTotalPoints() }
    }

    /**
     * Fetches the current team from SharedPreferences.
     */
    fun getTeam(context: Context): List<Player> {
        Log.d("PlayerRepository", "Fetching team from SharedPreferences.")
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
     * Saves the current team to SharedPreferences.
     */
    fun saveTeam() {
        Log.d("PlayerRepository", "Delegating team save to TeamRepository.")
        val teamRepository = TeamRepository(context)
        teamRepository.saveTeam()
    }

    /**
     * Fetches achievements for a specific player.
     */
    fun getAchievements(context: Context, playerId: String): List<Achievement> {
        val player = getPlayerById(context, playerId)
        return player?.achievements ?: emptyList()
    }

    /**
     * Fetches penalties for a specific player.
     */
    fun getPenalties(context: Context, playerId: String): List<Penalty> {
        val player = getPlayerById(context, playerId)
        return player?.penalties ?: emptyList()
    }

    /**
     * Fetches custom rules for a specific player.
     */
    fun getCustomRules(context: Context, playerId: String): List<CustomRule> {
        val player = getPlayerById(context, playerId)
        return player?.customRules ?: emptyList()
    }

    /**
     * Retrieves a player by their ID.
     */
    private fun getPlayerById(context: Context, playerId: String): Player? {
        val players = getPlayers(context)
        return players.find { it.id == playerId }
    }

    /**
     * Updates the player's achievements in SharedPreferences.
     */
    fun updatePlayerAchievements(context: Context, playerId: String, achievements: List<Achievement>) {
        Log.d("PlayerRepository", "Updating achievements for player ID: $playerId.")
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(achievements = achievements.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        } else {
            Log.e("PlayerRepository", "Player with ID: $playerId not found.")
        }
    }

    /**
     * Updates the player's penalties in SharedPreferences.
     */
    fun updatePlayerPenalties(context: Context, playerId: String, penalties: List<Penalty>) {
        Log.d("PlayerRepository", "Updating penalties for player ID: $playerId.")
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(penalties = penalties.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        } else {
            Log.e("PlayerRepository", "Player with ID: $playerId not found.")
        }
    }

    /**
     * Updates the player's custom rules in SharedPreferences.
     */
    fun updatePlayerCustomRules(context: Context, playerId: String, customRules: List<CustomRule>) {
        Log.d("PlayerRepository", "Updating custom rules for player ID: $playerId.")
        val players = getPlayers(context).toMutableList()
        val playerIndex = players.indexOfFirst { it.id == playerId }

        if (playerIndex != -1) {
            val updatedPlayer = players[playerIndex].copy(customRules = customRules.toMutableList())
            players[playerIndex] = updatedPlayer
            savePlayers(players, context)
        } else {
            Log.e("PlayerRepository", "Player with ID: $playerId not found.")
        }
    }

    suspend fun fetchPlayers(): List<Player> = withContext(Dispatchers.IO) {
        try {
            Log.d("PlayerRepository", "Fetching players from SharedPreferences")
            // Mocking fetch from SharedPreferences
            // Add actual implementation for fetching players
            return@withContext listOf<Player>()  // Replace with actual fetched players
        } catch (e: Exception) {
            Log.e("PlayerRepository", "Error fetching players: ${e.message}")
            throw e  // Re-throw exception
        }
    }

    fun resetPlayers() {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        editor.remove(PLAYERS_KEY) // Remove all players data
        editor.apply()
        Log.d("PlayerRepository", "All players have been reset.")
    }
}