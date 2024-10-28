package com.example.cow_cow.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.managers.ScavengerHuntManager
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.launch

class ScavengerHuntViewModel(
    application: Application,
    private val players: List<Player>,
    repository: ScavengerHuntRepository
) : AndroidViewModel(application) {

    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> get() = _statusMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val TAG = "ScavengerHuntViewModel"

    init {
        Log.d(TAG, "Initializing ScavengerHuntViewModel with ${players.size} players.")
        ScavengerHuntManager.initialize(repository)  // Initialize the manager with the repository
        startScavengerHunt()
    }

    /**
     * Starts the scavenger hunt and updates the UI with active items.
     */
    fun startScavengerHunt() {
        viewModelScope.launch {
            try {
                ScavengerHuntManager.startScavengerHunt(players)
                _scavengerHuntItems.postValue(ScavengerHuntManager.getActiveScavengerHuntItems())
                _statusMessage.postValue("Scavenger hunt started successfully.")
                Log.d(TAG, "Scavenger hunt started with ${_scavengerHuntItems.value?.size ?: 0} items.")
            } catch (e: Exception) {
                _errorMessage.postValue("Unable to start scavenger hunt. Please try again.")
                Log.e(TAG, "Error starting scavenger hunt: ${e.message}", e)
            }
        }
    }

    /**
     * Marks a scavenger hunt item as found by a player, updates the active items, and notifies UI.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        viewModelScope.launch {
            try {
                ScavengerHuntManager.markItemAsFound(item, player)
                _scavengerHuntItems.postValue(ScavengerHuntManager.getActiveScavengerHuntItems())
                _statusMessage.postValue("${item.name} found by ${player.name}!")
                Log.d(TAG, "Item '${item.name}' marked as found by '${player.name}'. Active items count: ${_scavengerHuntItems.value?.size ?: 0}")

                // Check for hunt completion
                if (ScavengerHuntManager.isHuntCompleted()) {
                    _statusMessage.postValue("Scavenger hunt completed!")
                    Log.d(TAG, "Scavenger hunt completed. All items found.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to mark item as found. Please try again.")
                Log.e(TAG, "Error marking item as found: ${e.message}", e)
            }
        }
    }

    /**
     * Clears status and error messages.
     */
    fun clearMessages() {
        Log.d(TAG, "Clearing status and error messages.")
        _statusMessage.postValue(null)
        _errorMessage.postValue(null)
    }
}
