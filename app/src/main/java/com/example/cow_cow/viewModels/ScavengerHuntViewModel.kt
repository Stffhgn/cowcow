package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.managers.ScavengerHuntManager
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScavengerHuntViewModel(
    application: Application,
    private val players: List<Player>
) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext

    // Initialize the repository with the application context
    private val repository = ScavengerHuntRepository(context)

    // LiveData for scavenger hunt items from the repository
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> = repository.scavengerHuntItems

    // LiveData for status and error messages
    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> get() = _statusMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Logging tag
    private val TAG = "ScavengerHuntViewModel"

    init {
        startScavengerHunt()
    }

    /**
     * Starts the scavenger hunt using the ScavengerHuntManager.
     */
    private fun startScavengerHunt() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    ScavengerHuntManager.startScavengerHunt(players, context)
                }
                _statusMessage.value = "Scavenger hunt started successfully"
                Log.d(TAG, "Scavenger hunt started.")
            } catch (e: Exception) {
                _errorMessage.value = "Unable to start scavenger hunt. Please try again."
                Log.e(TAG, "Error starting scavenger hunt: ${e.message}", e)
            }
        }
    }

    /**
     * Marks a scavenger hunt item as found and updates the status message.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    ScavengerHuntManager.markItemAsFound(item, player, context)
                }
                _statusMessage.value = "${item.name} found by ${player.name}!"
                Log.d(TAG, "${item.name} marked as found by ${player.name}.")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to mark item as found. Please try again."
                Log.e(TAG, "Error marking item as found: ${e.message}", e)
            }
        }
    }

    /**
     * Checks if the scavenger hunt is completed.
     *
     * @return True if the hunt is completed, false otherwise.
     */
    fun isHuntCompleted(): Boolean {
        val huntCompleted = ScavengerHuntManager.isHuntCompleted()
        Log.d(TAG, "Scavenger hunt completed: $huntCompleted")
        return huntCompleted
    }

    /**
     * Clears error and status messages.
     */
    fun clearMessages() {
        _statusMessage.value = null
        _errorMessage.value = null
    }
}
