package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScavengerHuntViewModel(application: Application, private val repository: ScavengerHuntRepository) : AndroidViewModel(application) {

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    // LiveData for status or error handling
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

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
                _statusMessage.value = "Failed to load scavenger hunt items: ${e.message}"
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
                _statusMessage.postValue("Failed to save scavenger hunt items: ${e.message}")
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
                _statusMessage.postValue("Failed to update items from server: ${e.message}")
            }
        }
    }
}
