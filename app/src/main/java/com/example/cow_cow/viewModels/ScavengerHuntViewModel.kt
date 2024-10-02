package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScavengerHuntViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScavengerHuntRepository()

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    // Status message for success or other info
    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> get() = _statusMessage

    // Error message for handling errors
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Init block loads the scavenger hunt items at startup
    init {
        loadScavengerHuntItems()
    }

    /**
     * Load scavenger hunt items from local storage or repository
     */
    fun loadScavengerHuntItems() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val items = withContext(Dispatchers.IO) {
                    repository.getScavengerHuntItems(context)
                }
                _scavengerHuntItems.value = items
                _statusMessage.value = "Items loaded successfully"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load scavenger hunt items: ${e.message}"
            }
        }
    }

    /**
     * Save scavenger hunt items, e.g., when adding/removing from settings
     */
    fun saveScavengerHuntItems(items: List<ScavengerHuntItem>, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.saveScavengerHuntItems(items, context)
                _statusMessage.postValue("Items saved successfully")
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to save scavenger hunt items: ${e.message}")
            }
        }
    }

    /**
     * Add a scavenger hunt item and update the list.
     */
    fun addScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.addScavengerHuntItem(item, context)
                }
                loadScavengerHuntItems() // Reload the items after adding a new one
                _statusMessage.value = "Scavenger hunt item added successfully."
            } catch (e: Exception) {
                _errorMessage.value = "Error adding scavenger hunt item: ${e.message}"
            }
        }
    }

    /**
     * Marks a scavenger hunt item as found by the player and updates the list.
     * @param item The scavenger hunt item that was found.
     * @param player The player who found the item.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        // Perform this operation on the background thread
        viewModelScope.launch {
            try {
                // Mark the item as found and award points
                item.isFound = true
                player.addBasePoints(item.getPoints())  // Assuming getPoints() is part of ScavengerHuntItem

                // Update the list in the repository
                val currentItems = _scavengerHuntItems.value?.toMutableList() ?: mutableListOf()
                val updatedItems = currentItems.map { if (it.name == item.name) item else it }
                _scavengerHuntItems.postValue(updatedItems)

                // Optionally show a success message
                _statusMessage.postValue("${item.name} was found by ${player.name}!")

                // Save the updated list back to the repository (optional)
                withContext(Dispatchers.IO) {
                    repository.saveScavengerHuntItems(updatedItems, getApplication<Application>().applicationContext)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error marking item as found: ${e.message}")
            }
        }
    }

    /**
     * Update scavenger hunt items from server/API
     */
    fun updateFromServer(newItems: List<ScavengerHuntItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                repository.updateScavengerHuntItemsFromServer(newItems, context)
                // Reload updated items into LiveData
                loadScavengerHuntItems()
                _statusMessage.postValue("Items updated from server")
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to update items from server: ${e.message}")
            }
        }
    }

    /**
     * Clear error and status messages after they have been shown to the user.
     */
    fun clearMessages() {
        _statusMessage.value = null
        _errorMessage.value = null
    }
}
