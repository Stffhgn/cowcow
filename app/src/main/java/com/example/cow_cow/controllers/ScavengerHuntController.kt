package com.example.cow_cow.controllers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScavengerHuntController(
    private val repository: ScavengerHuntRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main) // Allows for coroutines to be used in the controller
) {

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    /**
     * Load scavenger hunt items from the repository asynchronously.
     */
    fun loadScavengerHuntItems(context: Context) {
        scope.launch {
            try {
                val items = withContext(Dispatchers.IO) { repository.getScavengerHuntItems(context) }
                _scavengerHuntItems.value = items
            } catch (e: Exception) {
                // Handle error (e.g., log the error or show a user-friendly message)
                _scavengerHuntItems.value = emptyList() // Provide a fallback
            }
        }
    }

    /**
     * Add a new scavenger hunt item asynchronously.
     */
    fun addScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repository.addScavengerHuntItem(item, context) }
                loadScavengerHuntItems(context) // Reload items after modification
            } catch (e: Exception) {
                // Handle error (log, notify user, etc.)
            }
        }
    }

    /**
     * Remove an existing scavenger hunt item asynchronously.
     */
    fun removeScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repository.removeScavengerHuntItem(item, context) }
                loadScavengerHuntItems(context) // Reload items after modification
            } catch (e: Exception) {
                // Handle error (log, notify user, etc.)
            }
        }
    }

    /**
     * Clear all scavenger hunt items (for reset).
     */
    fun clearScavengerHuntItems(context: Context) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repository.saveScavengerHuntItems(emptyList(), context) }
                _scavengerHuntItems.value = emptyList()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
