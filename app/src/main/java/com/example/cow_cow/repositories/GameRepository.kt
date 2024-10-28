package com.example.cow_cow.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Game
import com.example.cow_cow.models.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameRepository(private val context: Context) {

    private val players = mutableListOf<Player>()
    private val _gameState = MutableLiveData<Game>()
    val gameState: LiveData<Game> get() = _gameState

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val PLAYERS_KEY = "PLAYERS_KEY"
        private const val GAME_STATE_KEY = "GAME_STATE_KEY"
    }

    init {
        // Load initial data for players and game state
        loadPlayers()
        loadGameState()
    }

    // --- PLAYER MANAGEMENT ---
    /**
     * Retrieve the current list of players.
     */
    fun getPlayers(): List<Player> = players

    /**
     * Add a player and save the updated list to persistent storage.
     */
    fun addPlayer(player: Player) {
        if (!players.contains(player)) {
            players.add(player)
            savePlayers()
        }
    }

    /**
     * Remove a player and update the persistent storage.
     */
    fun removePlayer(player: Player) {
        players.remove(player)
        savePlayers()
    }

    /**
     * Clear all players and update persistent storage.
     */
    fun clearPlayers() {
        players.clear()
        savePlayers()
    }

    /**
     * Save the current list of players to SharedPreferences.
     */
    private fun savePlayers() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(players)
        editor.putString(PLAYERS_KEY, json)
        editor.apply()
    }

    /**
     * Load the list of players from SharedPreferences.
     */
    private fun loadPlayers() {
        val json = sharedPreferences.getString(PLAYERS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<List<Player>>() {}.type
            players.clear()
            players.addAll(gson.fromJson(json, type))
        }
    }

    // --- GAME STATE MANAGEMENT ---
    /**
     * Retrieve the current game state.
     */
    fun getGameState(): Game? = _gameState.value

    /**
     * Update the game state and save it to persistent storage.
     */
    fun updateGameState(newGameState: Game) {
        _gameState.value = newGameState
        saveGameState()
    }

    /**
     * Save the current game state to SharedPreferences.
     */
    private fun saveGameState() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(_gameState.value)
        editor.putString(GAME_STATE_KEY, json)
        editor.apply()
    }

    /**
     * Load the game state from SharedPreferences.
     */
    private fun loadGameState() {
        val json = sharedPreferences.getString(GAME_STATE_KEY, null)
        if (json != null) {
            val gameStateType = object : TypeToken<Game>() {}.type
            _gameState.value = gson.fromJson(json, gameStateType)
        } else {
            // Initialize a new game state if none exists
            _gameState.value = Game()
        }
    }

    // --- SCORE MANAGEMENT ---
    /**
     * Calculate the total score for a player based on game rules.
     */
    fun calculatePlayerScore(player: Player): Int {
        return player.basePoints - player.penaltyPoints
    }

    /**
     * Update a player's score and save the changes.
     */
    fun updatePlayerScore(player: Player, points: Int) {
        player.basePoints += points
        savePlayers()  // Persist the updated player data
    }

    // --- GAME RESET ---
    /**
     * Reset the game by clearing all players and resetting the game state.
     */
    fun resetGame() {
        clearPlayers()
        _gameState.value = Game()  // Reset the game state
        saveGameState()
    }
}
