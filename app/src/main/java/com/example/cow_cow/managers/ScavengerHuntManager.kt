package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.utils.ScavengerHuntItemGenerator

class ScavengerHuntManager(
    private val scavengerHuntRepository: ScavengerHuntRepository,
    private val scoreManager: ScoreManager // Injected ScoreManager instance
) {

    private val allScavengerHuntItems = mutableListOf<ScavengerHuntItem>()
    private val TAG = "ScavengerHuntManager"

    init {
        initialize()
    }

    fun initialize() {
        allScavengerHuntItems.clear()
        Log.d(TAG, "Initializing ScavengerHuntManager with repository.")

        val savedItems = scavengerHuntRepository.getSavedScavengerHuntItems()
        if (savedItems.isEmpty()) {
            Log.d(TAG, "No saved items found. Generating new items.")
            allScavengerHuntItems.addAll(ScavengerHuntItemGenerator.generateAllItems())
            saveAllItems()
            Log.d(TAG, "Generated and saved ${allScavengerHuntItems.size} new scavenger hunt items.")
        } else {
            allScavengerHuntItems.addAll(savedItems)
            Log.d(TAG, "Loaded ${allScavengerHuntItems.size} items from saved data.")
        }
    }

    /**
     * Starts the scavenger hunt by ensuring active items are available.
     */
    fun startScavengerHunt(): List<ScavengerHuntItem> {
        Log.d(TAG, "Starting scavenger hunt.")
        if (allScavengerHuntItems.isEmpty()) {
            loadOrGenerateItems()
        }
        return refreshItems()
    }

    /**
     * Refreshes active items on the board by replacing found items, returning the updated list.
     */
    fun refreshItems(): List<ScavengerHuntItem> {
        Log.d(TAG, "Refreshing scavenger hunt items on the board.")
        if (isHuntCompleted()) {
            // Reset all items
            allScavengerHuntItems.forEach {
                it.isFound = false
                it.isActive = false
            }
            Log.d(TAG, "All items found. Scavenger hunt reset.")
        }

        val activeItems = getActiveItemsForBoard()
        saveAllItems()
        Log.d(TAG, "Board refreshed with active items: ${activeItems.map { it.name }}")
        return activeItems
    }

    /**
     * Checks if the scavenger hunt is complete.
     */
    private fun isHuntCompleted(): Boolean {
        val huntCompleted = allScavengerHuntItems.all { it.isFound }
        Log.d(TAG, "Hunt completed status: $huntCompleted")
        return huntCompleted
    }

    /**
     * Marks an item as found and refreshes the board.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player): List<ScavengerHuntItem> {
        allScavengerHuntItems.find { it.name == item.name }?.apply {
            isFound = true
            isActive = false
            scoreManager.addPointsToPlayer(player, points)
            Log.d(TAG, "Item '${name}' marked as found and set to inactive for player '${player.name}'.")
            saveAllItems()
        } ?: Log.e(TAG, "Error: Item '${item.name}' not found in scavenger hunt items.")

        return refreshItems()
    }

    /**
     * Loads items from the repository or generates new ones.
     */
    private fun loadOrGenerateItems() {
        val savedItems = scavengerHuntRepository.getSavedScavengerHuntItems()
        if (savedItems.isNotEmpty()) {
            allScavengerHuntItems.clear()
            allScavengerHuntItems.addAll(savedItems)
            Log.d(TAG, "Loaded ${allScavengerHuntItems.size} saved scavenger hunt items.")
        } else {
            allScavengerHuntItems.clear()
            allScavengerHuntItems.addAll(ScavengerHuntItemGenerator.generateAllItems())
            saveAllItems()
            Log.d(TAG, "Generated and saved ${allScavengerHuntItems.size} new scavenger hunt items.")
        }
    }

    /**
     * Retrieves active items for the board, ensuring correct count per difficulty level.
     */
    fun getActiveItemsForBoard(): List<ScavengerHuntItem> {
        val activeItems = mutableListOf<ScavengerHuntItem>()
        activeItems.addAll(getActiveItemsForDifficulty(DifficultyLevel.EASY, 2))
        activeItems.addAll(getActiveItemsForDifficulty(DifficultyLevel.MEDIUM, 2))
        activeItems.addAll(getActiveItemsForDifficulty(DifficultyLevel.HARD, 1))

        Log.d(TAG, "Active items for the board: ${activeItems.map { it.name }}")
        return activeItems
    }

    private fun getActiveItemsForDifficulty(difficulty: DifficultyLevel, requiredCount: Int): List<ScavengerHuntItem> {
        val activeItems = allScavengerHuntItems.filter {
            it.difficultyLevel == difficulty && it.isActive && !it.isFound
        }.toMutableList()

        val itemsNeeded = requiredCount - activeItems.size

        Log.d(TAG, "Required count for $difficulty: $requiredCount, currently active: ${activeItems.size}, items needed: $itemsNeeded")

        if (itemsNeeded > 0) {
            var availableItems = allScavengerHuntItems.filter {
                it.difficultyLevel == difficulty && !it.isFound && !it.isActive
            }

            Log.d(TAG, "Available items for $difficulty after filtering: ${availableItems.size}")

            if (availableItems.size < itemsNeeded) {
                // Reset all items of this difficulty
                allScavengerHuntItems.filter { it.difficultyLevel == difficulty }.forEach {
                    it.isFound = false
                    it.isActive = false
                }
                // Re-fetch available items after reset
                availableItems = allScavengerHuntItems.filter {
                    it.difficultyLevel == difficulty && !it.isFound && !it.isActive
                }
                Log.d(TAG, "Items after resetting found status for $difficulty: ${availableItems.size}")
            }

            val itemsToAdd = availableItems.take(itemsNeeded).onEach { it.isActive = true }
            activeItems.addAll(itemsToAdd)
            Log.d(TAG, "Added ${itemsToAdd.size} items of $difficulty difficulty to the active board.")
        } else {
            Log.d(TAG, "No additional items needed for $difficulty difficulty. Current count: ${activeItems.size}")
        }

        // Ensure we have exactly the required count
        return activeItems.take(requiredCount)
    }


    private fun saveAllItems() {
        Log.d(TAG, "Saving ${allScavengerHuntItems.size} scavenger hunt items to repository.")
        scavengerHuntRepository.saveAllItems(allScavengerHuntItems)
        Log.d(TAG, "Saved items to repository.")
    }
}
