package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
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
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    /**
     * Load scavenger hunt items from the repository asynchronously and start a new hunt.
     */
    fun loadScavengerHuntItems(context: Context, player: Player, difficulty: String? = null) {
        scope.launch {
            try {
                Log.d("ScavengerHuntController", "Loading scavenger hunt items for player: ${player.name}")
                val items = withContext(Dispatchers.IO) { repository.getScavengerHuntItems() }
                _scavengerHuntItems.postValue(items)
                Log.d("ScavengerHuntController", "Starting scavenger hunt with ${items.size} items.")
                ScavengerHuntManager.startScavengerHunt(player, repository, difficulty)
            } catch (e: Exception) {
                Log.e("ScavengerHuntController", "Error loading scavenger hunt items: ${e.message}")
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
                Log.d("ScavengerHuntController", "Adding new scavenger hunt item: ${item.name}")
                withContext(Dispatchers.IO) { repository.addScavengerHuntItem(item, context) }
                loadScavengerHuntItems(context, Player(1, "Player")) // Replace with actual player object
            } catch (e: Exception) {
                Log.e("ScavengerHuntController", "Error adding scavenger hunt item: ${e.message}")
            }
        }
    }

    /**
     * Mark a scavenger hunt item as found and persist changes.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player, context: Context) {
        scope.launch {
            try {
                Log.d("ScavengerHuntController", "Marking item as found: ${item.name} by player: ${player.name}")
                ScavengerHuntManager.markItemAsFound(item, player)
                withContext(Dispatchers.IO) {
                    repository.saveScavengerHuntItems(ScavengerHuntManager.getActiveScavengerHuntItems(), context)
                }
                _scavengerHuntItems.postValue(ScavengerHuntManager.getActiveScavengerHuntItems())
            } catch (e: Exception) {
                Log.e("ScavengerHuntController", "Error marking scavenger hunt item as found: ${e.message}")
            }
        }
    }

    /**
     * Clear all scavenger hunt items (for reset).
     */
    fun clearScavengerHuntItems(context: Context) {
        scope.launch {
            try {
                Log.d("ScavengerHuntController", "Clearing all scavenger hunt items.")
                ScavengerHuntManager.stopScavengerHunt()
                withContext(Dispatchers.IO) { repository.saveScavengerHuntItems(emptyList(), context) }
                _scavengerHuntItems.postValue(emptyList())
            } catch (e: Exception) {
                Log.e("ScavengerHuntController", "Error clearing scavenger hunt items: ${e.message}")
            }
        }
    }
}
