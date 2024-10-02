package com.example.cow_cow.controllers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.managers.ScavengerHuntManager
import com.example.cow_cow.models.Player
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
     * Load scavenger hunt items from the repository asynchronously and start a new hunt.
     */
    fun loadScavengerHuntItems(context: Context, player: Player, difficulty: String? = null) {
        scope.launch {
            try {
                val items = withContext(Dispatchers.IO) { repository.getScavengerHuntItems(context) }
                _scavengerHuntItems.postValue(items)
                ScavengerHuntManager.startScavengerHunt(player, repository, difficulty)
            } catch (e: Exception) {
                // Handle error (e.g., log the error or show a user-friendly message)
                _scavengerHuntItems.postValue(emptyList()) // Provide a fallback
            }
        }
    }

    /**
     * Add a new scavenger hunt item asynchronously and persist it.
     */
    fun addScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repository.addScavengerHuntItem(item, context) }
                loadScavengerHuntItems(context, Player(1, "Player")) // You should pass the actual player object here
            } catch (e: Exception) {
                // Handle error (log, notify user, etc.)
            }
        }
    }

    /**
     * Mark a scavenger hunt item as found and persist changes.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player, context: Context) {
        scope.launch {
            try {
                ScavengerHuntManager.markItemAsFound(item, player)
                withContext(Dispatchers.IO) {
                    repository.saveScavengerHuntItems(ScavengerHuntManager.getActiveScavengerHuntItems(), context)
                }
                _scavengerHuntItems.postValue(ScavengerHuntManager.getActiveScavengerHuntItems())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Clear all scavenger hunt items (for reset).
     */
    fun clearScavengerHuntItems(context: Context) {
        scope.launch {
            try {
                ScavengerHuntManager.stopScavengerHunt()
                withContext(Dispatchers.IO) { repository.saveScavengerHuntItems(emptyList(), context) }
                _scavengerHuntItems.postValue(emptyList())
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
