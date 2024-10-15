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
import kotlinx.coroutines.*

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

    init {
        // Initialize LiveData with existing players
        _playersLiveData.value = getPlayers()
    }

    /**
     * Retrieves SharedPreferences for storing player data.
     */
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Fetches the list of players from SharedPreferences.
     */
    fun getPlayers(): List<Player> {
        val prefs = getSharedPreferences()
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
    fun savePlayers(players: List<Player>) {
        Log.d("PlayerRepository", "Saving players to SharedPreferences.")
        val prefs = getSharedPreferences()
        val editor = prefs.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()
        Log.d("PlayerRepository", "Players saved to SharedPreferences: ${players.size} players")

        // Update the LiveData after saving the players
        _playersLiveData.postValue(players)
        Log.d("PlayerRepository", "playersLiveData updated with ${players.size} players.")

    }

    /**
     * Adds a new player to the list and saves it to SharedPreferences.
     */
    fun addPlayer(player: Player) {
        Log.d("PlayerRepository", "Adding new player: ${player.name}")
        CoroutineScope(Dispatchers.IO).launch {
            val players = getPlayers().toMutableList()

            // Ensure the player ID is unique
            if (players.any { it.id == player.id }) {
                Log.e("PlayerRepository", "Player with ID: ${player.id} already exists.")
                return@launch
            }

            players.add(player)
            savePlayers(players)
            withContext(Dispatchers.Main) {
                _playersLiveData.value = players
            }
            Log.d("PlayerRepository", "New player added with ID: ${player.id}")
        }
    }

    /**
     * Updates a specific player and saves the updated list to SharedPreferences.
     */
    fun updatePlayer(player: Player) {
        Log.d("PlayerRepository", "Updating player with ID: ${player.id}.")
        CoroutineScope(Dispatchers.IO).launch {
            val players = getPlayers().toMutableList()
            val index = players.indexOfFirst { it.id == player.id }
            if (index != -1) {
                players[index] = player
                savePlayers(players)
                withContext(Dispatchers.Main) {
                    _playersLiveData.value = players // Update LiveData on main thread
                }
                Log.d("PlayerRepository", "Player with ID: ${player.id} updated successfully.")
            } else {
                Log.e("PlayerRepository", "Player with ID: ${player.id} not found.")
            }
        }
    }

    /**
     * Refresh the list of players from SharedPreferences and update LiveData.
     */
    fun refreshPlayers() {
        Log.d("PlayerRepository", "Refreshing player list.")
        CoroutineScope(Dispatchers.IO).launch {
            val updatedPlayers = getPlayers() // Fetch updated players from SharedPreferences
            withContext(Dispatchers.Main) {
                _playersLiveData.value = updatedPlayers // Update LiveData on the main thread
            }
            Log.d("PlayerRepository", "Player list refreshed with ${updatedPlayers.size} players.")
        }
    }

    /**
     * Removes a player by their ID from the list.
     */
    fun removePlayerById(playerId: String) {
        Log.d("PlayerRepository", "Removing player with ID: $playerId.")
        CoroutineScope(Dispatchers.IO).launch {
            val players = getPlayers().toMutableList()
            val updatedPlayers = players.filter { it.id != playerId }
            savePlayers(updatedPlayers)
            withContext(Dispatchers.Main) {
                _playersLiveData.value = updatedPlayers
            }
            Log.d("PlayerRepository", "Player with ID: $playerId removed successfully.")
        }
    }

    /**
     * Updates the player's name in SharedPreferences.
     */
    fun updatePlayerName(playerId: String, newName: String) {
        Log.d("PlayerRepository", "Updating name for player ID: $playerId.")
        CoroutineScope(Dispatchers.IO).launch {
            val players = getPlayers().toMutableList()
            val index = players.indexOfFirst { it.id == playerId }
            if (index != -1) {
                players[index] = players[index].copy(name = newName)
                savePlayers(players)
                withContext(Dispatchers.Main) {
                    _playersLiveData.value = players
                }
                Log.d("PlayerRepository", "Player name updated to $newName for player ID: $playerId.")
            } else {
                Log.e("PlayerRepository", "Player with ID: $playerId not found.")
            }
        }
    }

    /**
     * Retrieves player-specific settings from SharedPreferences.
     */
    fun getPlayerSettings(playerId: String): PlayerSettings {
        Log.d("PlayerRepository", "Fetching settings for player ID: $playerId.")
        val sharedPreferences = getSharedPreferences()
        val settingsJson = sharedPreferences.getString("player_settings_$playerId", null)

        return if (settingsJson != null) {
            gson.fromJson(settingsJson, PlayerSettings::class.java)
        } else {
            Log.d("PlayerRepository", "No settings found for player ID: $playerId. Using default settings.")
            PlayerSettings() // Return default settings
        }
    }

    /**
     * Saves player-specific settings to SharedPreferences.
     */
    fun savePlayerSettings(playerId: String, settings: PlayerSettings) {
        Log.d("PlayerRepository", "Saving settings for player ID: $playerId.")
        val sharedPreferences = getSharedPreferences()
        val editor = sharedPreferences.edit()

        // Convert settings to JSON format and save in SharedPreferences
        val settingsJson = gson.toJson(settings)
        editor.putString("player_settings_$playerId", settingsJson)
        editor.apply()

        Log.d("PlayerRepository", "Settings saved successfully for player ID: $playerId.")
    }

    /**
     * Fetches the current team from SharedPreferences.
     */
    fun getTeam(): List<Player> {
        Log.d("PlayerRepository", "Fetching team from SharedPreferences.")
        val prefs = getSharedPreferences()
        val json = prefs.getString(TEAM_KEY, null)
        val teamList = if (json != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            gson.fromJson<List<Player>>(json, type)
        } else {
            emptyList()
        }

        _teamLiveData.postValue(teamList)
        return teamList
    }

    /**
     * Saves the current team to SharedPreferences.
     */
    fun saveTeam(teamPlayers: List<Player>) {
        Log.d("PlayerRepository", "Saving team to SharedPreferences.")
        val prefs = getSharedPreferences()
        val editor = prefs.edit()
        val json = gson.toJson(teamPlayers)
        editor.putString(TEAM_KEY, json)
        editor.apply()
        Log.d("PlayerRepository", "Team saved successfully with ${teamPlayers.size} players.")

        _teamLiveData.postValue(teamPlayers)
    }

    /**
     * Adds a player to the team.
     */
    fun addPlayerToTeam(player: Player) {
        Log.d("PlayerRepository", "Adding player to team: ${player.name}")
        CoroutineScope(Dispatchers.IO).launch {
            val teamPlayers = getTeam().toMutableList()
            if (!teamPlayers.any { it.id == player.id }) {
                teamPlayers.add(player)
                saveTeam(teamPlayers)
                withContext(Dispatchers.Main) {
                    _teamLiveData.value = teamPlayers
                }
                Log.d("PlayerRepository", "Player added to team: ${player.name}")
            } else {
                Log.w("PlayerRepository", "Player ${player.name} is already in the team.")
            }
        }
    }

    /**
     * Removes a player from the team.
     */
    fun removePlayerFromTeam(player: Player) {
        Log.d("PlayerRepository", "Removing player from team: ${player.name}")
        CoroutineScope(Dispatchers.IO).launch {
            val teamPlayers = getTeam().toMutableList()
            val updatedTeam = teamPlayers.filter { it.id != player.id }
            saveTeam(updatedTeam)
            withContext(Dispatchers.Main) {
                _teamLiveData.value = updatedTeam
            }
            Log.d("PlayerRepository", "Player removed from team: ${player.name}")
        }
    }

    /**
     * Retrieves a player by their ID.
     */
    fun getPlayerById(playerId: String): Player? {
        val players = getPlayers()
        return players.find { it.id == playerId }
    }

    /**
     * Resets all players.
     */
    fun resetPlayers() {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = getSharedPreferences()
            val editor = prefs.edit()
            editor.remove(PLAYERS_KEY) // Remove all players data
            editor.apply()
            withContext(Dispatchers.Main) {
                _playersLiveData.value = emptyList()
            }
            Log.d("PlayerRepository", "All players have been reset.")
        }
    }

    // Additional methods can be adjusted similarly...

    // Set current player (can be called when the user logs in or session starts)
    fun setCurrentPlayer(player: Player) {
        currentPlayer = player
    }

    // Fetch the globally stored current player
    fun getCurrentPlayer(): Player? {
        return currentPlayer
    }
}
