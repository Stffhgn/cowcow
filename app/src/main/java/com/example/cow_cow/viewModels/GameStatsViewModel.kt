package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Game
import com.example.cow_cow.repositories.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameStatsViewModel(
    application: Application,
    private val gameRepository: GameRepository
) : AndroidViewModel(application) {

    private val _game = MutableLiveData<Game>()
    val game: LiveData<Game> get() = _game

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    /**
     * Load current game data.
     */
    fun loadGame() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gameData = gameRepository.getGameState()
                withContext(Dispatchers.Main) {
                    if (gameData != null) {
                        _game.value = gameData
                    } else {
                        _errorMessage.value = "Game data not found"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error loading game data: ${e.message}"
                }
            }
        }
    }

    /**
     * Update game stats locally and sync with repository.
     */
    fun updateStats(newRound: Int, newElapsedTime: Long) {
        _game.value?.let { game ->
            game.round += newRound
            game.elapsedTime += newElapsedTime
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gameRepository.updateGameState(game) // Update the game asynchronously
                    withContext(Dispatchers.Main) {
                        loadGame() // Reload game data to ensure everything is up to date in the UI
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _errorMessage.value = "Error updating game stats: ${e.message}"
                    }
                }
            }
        }
    }

    /**
     * Reset the game stats.
     */
    fun resetGame() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                gameRepository.resetGame()  // Reset game in the repository
                withContext(Dispatchers.Main) {
                    loadGame()  // Reload the new game state
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error resetting game: ${e.message}"
                }
            }
        }
    }

    /**
     * Clear error message after displaying it.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
