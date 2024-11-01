package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.*
import com.example.cow_cow.utils.DataUtils.savePlayers
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
     * Saves a single player to SharedPreferences.
     *
     * @param player The player to save.
     */
    fun savePlayer(player: Player) {
        Log.d("PlayerRepository", "Saving player ${player.name} to SharedPreferences.")

        // Retrieve the existing list of players
        val prefs = getSharedPreferences()
        val playersJson = prefs.getString(PLAYERS_KEY, "[]")
        val players = gson.fromJson(playersJson, Array<Player>::class.java).toMutableList()

        // Check if the player already exists and update or add the new one
        val existingPlayerIndex = players.indexOfFirst { it.id == player.id }
        if (existingPlayerIndex != -1) {
            players[existingPlayerIndex] = player
            Log.d("PlayerRepository", "Updated player ${player.name}.")
        } else {
            players.add(player)
            Log.d("PlayerRepository", "Added new player ${player.name}.")
        }

        // Save the updated list back to SharedPreferences
        val editor = prefs.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()

        // Update the LiveData to notify observers
        _playersLiveData.postValue(players)
        Log.d("PlayerRepository", "Player ${player.name} saved to SharedPreferences.")
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

            // Save the updated list of players to SharedPreferences
            savePlayer(player)  // Call the method that saves a single player
        }
    }

    /**
     * Deletes a player from SharedPreferences and updates LiveData.
     *
     * @param player The player to delete.
     */
    fun deletePlayer(player: Player) {
        Log.d("PlayerRepository", "Deleting player: ${player.name}")

        CoroutineScope(Dispatchers.IO).launch {
            // Fetch the current list of players
            val players = getPlayers().toMutableList()

            // Remove the player from the list
            players.removeIf { it.id == player.id }

            // Save the updated list back to SharedPreferences
            val prefs = getSharedPreferences()
            val editor = prefs.edit()
            val json = gson.toJson(players)
            editor.putString(PLAYERS_KEY, json)
            editor.apply()

            // Update LiveData on the main thread to notify observers
            withContext(Dispatchers.Main) {
                _playersLiveData.value = players
                Log.d("PlayerRepository", "Player ${player.name} deleted successfully.")
            }
        }
    }

    /**
     * Updates a specific player and saves the changes.
     */
    fun updatePlayer(player: Player) {
        Log.d("PlayerRepository", "Updating player with ID: ${player.id}.")
        CoroutineScope(Dispatchers.IO).launch {
            val players = getPlayers().toMutableList()
            val index = players.indexOfFirst { it.id == player.id }
            if (index != -1) {
                players[index] = player
                savePlayer(player) // Save the updated player instead of the entire list
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
            val updatedPlayers = getPlayers()
            _playersLiveData.postValue(updatedPlayers)
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

            // Save each player individually after removal
            updatedPlayers.forEach { savePlayer(it) }

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
            try {
                val player = getPlayerById(playerId)
                if (player != null) {
                    // Update the player's name using the copy method
                    val updatedPlayer = player.copy(name = newName)

                    // Save the updated player
                    savePlayer(updatedPlayer)

                    // Refresh the LiveData with the updated player list
                    val updatedPlayers = getPlayers().toMutableList()
                    val index = updatedPlayers.indexOfFirst { it.id == playerId }
                    if (index != -1) {
                        updatedPlayers[index] = updatedPlayer
                        withContext(Dispatchers.Main) {
                            _playersLiveData.value = updatedPlayers
                            Log.d("PlayerRepository", "Player name updated to $newName for player ID: $playerId.")
                        }
                    }
                } else {
                    Log.e("PlayerRepository", "Player with ID: $playerId not found.")
                }
            } catch (e: Exception) {
                Log.e("PlayerRepository", "Error updating player name: ${e.message}", e)
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

    /**
     * Fetches player achievements.
     */
    fun getPlayerAchievements(playerId: String): List<Achievement> {
        val sharedPreferences = getSharedPreferences()
        val json = sharedPreferences.getString("player_achievements_$playerId", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<Achievement>>() {}.type)
        } else {
            emptyList()
        }
    }

    /**
     * Logs the GPS location where an item was found.
     */
    fun logGpsLocation(playerId: String, latitude: Double, longitude: Double) {
        Log.d("PlayerRepository", "Logging GPS location for player ID: $playerId at ($latitude, $longitude).")
        // Implement the logic to save or update GPS data associated with the player
    }

    /**
     * Uses a held power-up for a player.
     */
    fun usePowerUp(playerId: String, powerUp: PowerUp) {
        Log.d("PlayerRepository", "Using power-up for player ID: $playerId.")
        val player = getPlayerById(playerId) // Fetch the player by ID

        player?.let {
            if (it.heldPowerUps.contains(powerUp)) {
                it.heldPowerUps.remove(powerUp) // Remove the used power-up
                // Apply power-up effects if necessary
                savePlayer(it) // Save the updated player back to SharedPreferences
                Log.d("PlayerRepository", "Power-up used and saved for player ID: $playerId.")
            } else {
                Log.w("PlayerRepository", "Player ID: $playerId does not hold the specified power-up.")
            }
        } ?: Log.e("PlayerRepository", "Player with ID: $playerId not found.")
    }


    // Set current player (can be called when the user logs in or session starts)
    fun setCurrentPlayer(player: Player) {
        currentPlayer = player
    }

    // Fetch the globally stored current player
    fun getCurrentPlayer(): Player? {
        return currentPlayer
    }
}
