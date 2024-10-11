package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerStatsViewModel(
    application: Application,
    private val playerRepository: PlayerRepository,
    private val playerId: String
) : AndroidViewModel(application) {

    private val _player = MutableLiveData<Player>()
    val player: LiveData<Player> get() = _player

    /**
     * Load player data by ID.
     */
    fun loadPlayer() {
        CoroutineScope(Dispatchers.IO).launch {
            val playerData = playerRepository.getPlayers().find { it.id == playerId }
            playerData?.let {
                withContext(Dispatchers.Main) {
                    _player.value = it
                }
            }
        }
    }

    /**
     * Update player stats locally and sync with server.
     */
    fun updateStats(newScore: Int, newCowCount: Int, newChurchCount: Int, newWaterTowerCount: Int) {
        _player.value?.let {
            it.basePoints += newScore
            it.cowCount += newCowCount
            it.churchCount += newChurchCount
            it.waterTowerCount += newWaterTowerCount
            CoroutineScope(Dispatchers.IO).launch {
                playerRepository.updatePlayer(it) // Update the player asynchronously
                withContext(Dispatchers.Main) {
                    loadPlayer() // Reload player data to make sure everything's up to date in UI
                }
            }
        }
    }

    /**
     * Synchronize player stats with the server.

    private fun syncWithServer(player: Player) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Example API call to sync with the server
                // You could use Retrofit or another HTTP library to make this request
                val response = PlayerApiService.create().updatePlayerStats(player.id, player)
                if (response.isSuccessful) {
                    // Successfully synced with server
                } else {
                    // Handle server error
                }
            } catch (e: Exception) {
                // Handle network failure, possibly retry
            }
        }
    }
    */

}
