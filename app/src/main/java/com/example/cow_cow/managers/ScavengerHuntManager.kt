package com.example.cow_cow.managers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository

object ScavengerHuntManager {

    private val activeScavengerHuntItems = mutableListOf<ScavengerHuntItem>()
    private var huntCompleted: Boolean = false

    /**
     * Starts a new scavenger hunt by loading items from the repository.
     * Can filter items based on difficulty, location, etc.
     */
    fun startScavengerHunt(player: Player, scavengerHuntRepository: ScavengerHuntRepository, difficulty: String? = null) {
        activeScavengerHuntItems.clear()
        huntCompleted = false
        // Load scavenger hunt items based on difficulty or other conditions
        val allItems = scavengerHuntRepository.getScavengerHuntItems()
        activeScavengerHuntItems.addAll(
            allItems.filter { item -> difficulty == null || item.difficultyLevel.name == difficulty }
        )
    }

    /**
     * Checks off a scavenger hunt item as found by the player.
     *
     * @param item The scavenger hunt item that was found.
     * @param player The player who found the item.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        val foundItem = activeScavengerHuntItems.find { it.name == item.name }
        foundItem?.let {
            it.isFound = true
            // Award points for finding the item
            player.addBasePoints(it.getPoints())
        }

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
    private fun onHuntCompleted(player: Player) {
        // Award bonus points for completing the scavenger hunt
        player.addBonusPoints(100)  // Example of bonus for completing the scavenger hunt
        // Additional logic, like unlocking achievements or special rewards
    }

    /**
     * Returns the current progress of the scavenger hunt.
     *
     * @return A percentage value representing the progress.
     */
    fun getHuntProgress(): Double {
        val totalItems = activeScavengerHuntItems.size
        val foundItems = activeScavengerHuntItems.count { it.isFound }
        return if (totalItems > 0) (foundItems.toDouble() / totalItems) * 100 else 0.0
    }

    /**
     * Stops the scavenger hunt and clears active items.
     */
    fun stopScavengerHunt() {
        activeScavengerHuntItems.clear()
        huntCompleted = false
    }

    /**
     * Retrieves the list of active scavenger hunt items.
     *
     * @return The list of items currently being hunted.
     */
    fun getActiveScavengerHuntItems(): List<ScavengerHuntItem> {
        return activeScavengerHuntItems
    }

    /**
     * Retrieves whether the scavenger hunt is completed.
     *
     * @return True if the scavenger hunt is completed, false otherwise.
     */
    fun isHuntCompleted(): Boolean {
        return huntCompleted
    }
}
