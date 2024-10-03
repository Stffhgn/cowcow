package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository

object ScavengerHuntManager {

    private val activeScavengerHuntItems = mutableListOf<ScavengerHuntItem>()
    private var huntCompleted: Boolean = false

    private const val TAG = "ScavengerHuntManager"

    /**
     * Starts a new scavenger hunt by loading items from the repository.
     * Can filter items based on difficulty, location, etc.
     *
     * @param player The player participating in the scavenger hunt.
     * @param scavengerHuntRepository Repository to fetch scavenger hunt items from.
     * @param difficulty Optional difficulty filter (e.g., "Easy", "Hard").
     */
    fun startScavengerHunt(player: Player, scavengerHuntRepository: ScavengerHuntRepository, difficulty: String? = null) {
        Log.d(TAG, "Starting scavenger hunt for player: ${player.name}, difficulty: $difficulty")

        // Clear previous hunt data and reset completion status
        activeScavengerHuntItems.clear()
        huntCompleted = false

        // Load scavenger hunt items based on difficulty or other conditions
        val allItems = scavengerHuntRepository.getScavengerHuntItems()
        val filteredItems = allItems.filter { item -> difficulty == null || item.difficultyLevel.name == difficulty }

        activeScavengerHuntItems.addAll(filteredItems)
        Log.d(TAG, "${activeScavengerHuntItems.size} items loaded for the scavenger hunt.")
    }

    /**
     * Checks off a scavenger hunt item as found by the player.
     *
     * @param item The scavenger hunt item that was found.
     * @param player The player who found the item.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        Log.d(TAG, "Marking item '${item.name}' as found by player ${player.name}.")

        val foundItem = activeScavengerHuntItems.find { it.name == item.name }
        foundItem?.let {
            it.isFound = true
            Log.d(TAG, "Item '${item.name}' marked as found.")

            // Award points for finding the item
            val pointsAwarded = it.getPoints()
            player.addBasePoints(pointsAwarded)
            Log.d(TAG, "Player ${player.name} awarded $pointsAwarded points for finding item '${item.name}'.")
        } ?: Log.w(TAG, "Item '${item.name}' not found in active scavenger hunt items.")

        // Check if the hunt is completed
        if (activeScavengerHuntItems.all { it.isFound }) {
            huntCompleted = true
            onHuntCompleted(player)
        }
    }

    /**
     * Handles the event when a scavenger hunt is completed.
     *
     * @param player The player who completed the scavenger hunt.
     */
    fun onHuntCompleted(player: Player) {
        Log.d(TAG, "Scavenger hunt completed by player: ${player.name}. Awarding bonus points.")

        // Award bonus points for completing the scavenger hunt
        player.addBonusPoints(100)  // Example of bonus for completing the scavenger hunt
        Log.d(TAG, "Player ${player.name} awarded 100 bonus points for completing the scavenger hunt.")
        // Additional logic, like unlocking achievements or special rewards, can go here
    }

    /**
     * Returns the current progress of the scavenger hunt.
     *
     * @return A percentage value representing the progress (0-100%).
     */
    fun getHuntProgress(): Double {
        val totalItems = activeScavengerHuntItems.size
        val foundItems = activeScavengerHuntItems.count { it.isFound }
        val progress = if (totalItems > 0) (foundItems.toDouble() / totalItems) * 100 else 0.0
        Log.d(TAG, "Scavenger hunt progress: $progress% ($foundItems/$totalItems items found)")
        return progress
    }

    /**
     * Stops the scavenger hunt and clears active items.
     */
    fun stopScavengerHunt() {
        Log.d(TAG, "Stopping scavenger hunt. Clearing all active items.")
        activeScavengerHuntItems.clear()
        huntCompleted = false
    }

    /**
     * Retrieves the list of active scavenger hunt items.
     *
     * @return The list of items currently being hunted.
     */
    fun getActiveScavengerHuntItems(): List<ScavengerHuntItem> {
        Log.d(TAG, "Retrieving ${activeScavengerHuntItems.size} active scavenger hunt items.")
        return activeScavengerHuntItems
    }

    /**
     * Retrieves whether the scavenger hunt is completed.
     *
     * @return True if the scavenger hunt is completed, false otherwise.
     */
    fun isHuntCompleted(): Boolean {
        Log.d(TAG, "Scavenger hunt completed status: $huntCompleted")
        return huntCompleted
    }
}
