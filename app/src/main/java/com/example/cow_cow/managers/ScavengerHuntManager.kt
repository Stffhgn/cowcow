package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.utils.ScavengerHuntItemGenerator

object ScavengerHuntManager {

    private val activeScavengerHuntItems = mutableListOf<ScavengerHuntItem>()
    private val generatedItems = mutableSetOf<String>()  // Track names of generated items
    private var huntCompleted: Boolean = false
    private lateinit var scavengerHuntRepository: ScavengerHuntRepository
    private const val TAG = "ScavengerHuntManager"

    /**
     * Initializes the ScavengerHuntManager with the repository, called once by the ViewModel.
     */
    fun initialize(repository: ScavengerHuntRepository) {
        scavengerHuntRepository = repository
        Log.d(TAG, "ScavengerHuntManager initialized with repository.")
    }

    /**
     * Starts a new scavenger hunt by generating items for players.
     */
    fun startScavengerHunt(players: List<Player>) {
        Log.d(TAG, "Starting scavenger hunt for ${players.size} players.")

        // Clear previous items and states
        activeScavengerHuntItems.clear()
        generatedItems.clear()
        huntCompleted = false

        // Generate initial unique items for the scavenger hunt
        repeat(5) {
            val player = players.random()
            addNewScavengerHuntItem(player)
        }
        Log.d(TAG, "Scavenger hunt started with ${activeScavengerHuntItems.size} initial items.")
    }

    /**
     * Marks an item as found, awards points, and refreshes items.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        Log.d(TAG, "Attempting to mark item '${item.name}' as found by player '${player.name}'.")

        activeScavengerHuntItems.find { it.name == item.name }?.let { foundItem ->
            foundItem.isFound = true
            val pointsAwarded = foundItem.getPoints()
            ScoreManager.addPointsToPlayer(player, pointsAwarded)
            Log.d(TAG, "Player '${player.name}' awarded $pointsAwarded points for finding item '${foundItem.name}'.")

            activeScavengerHuntItems.remove(foundItem)
            addNewScavengerHuntItem(player)
            saveActiveItemsToRepository(player)

            // Check for hunt completion
            if (isHuntCompleted()) onHuntCompleted(player)
        } ?: Log.w(TAG, "Item '${item.name}' not found in active scavenger hunt items.")
    }

    /**
     * Adds a new, unique scavenger hunt item for a player.
     */
    private fun addNewScavengerHuntItem(player: Player) {
        var newItem: ScavengerHuntItem
        var attempts = 0
        val maxAttempts = 10

        do {
            newItem = ScavengerHuntItemGenerator.generateScavengerHuntItem(player)
            attempts++
            if (attempts > maxAttempts) {
                Log.e(TAG, "Exceeded maximum attempts to generate a unique scavenger hunt item for ${player.name}")
                return
            }
        } while (newItem.name in generatedItems)

        activeScavengerHuntItems.add(newItem)
        generatedItems.add(newItem.name)
        Log.d(TAG, "Generated new item '${newItem.name}' for player '${player.name}'.")

        // Limit active items to a maximum of 5 by removing the oldest if necessary
        if (activeScavengerHuntItems.size > 5) {
            val removedItem = activeScavengerHuntItems.removeAt(0)
            generatedItems.remove(removedItem.name)
            Log.d(TAG, "Removed oldest item '${removedItem.name}' to maintain item limit.")
        }
    }

    /**
     * Saves the current active items for a player to the repository.
     */
    private fun saveActiveItemsToRepository(player: Player) {
        Log.d(TAG, "Saving active scavenger hunt items for player '${player.name}' to repository.")
        scavengerHuntRepository.saveScavengerHuntItems(player, activeScavengerHuntItems)
    }

    /**
     * Handles actions to perform upon scavenger hunt completion, such as awarding achievements.
     */
    private fun onHuntCompleted(player: Player) {
        Log.d(TAG, "Scavenger hunt completed! Awarding rewards to all players.")

        PlayerManager.getAllPlayers().forEach {
            ScoreManager.addPointsToPlayer(it, 100)
            AchievementManager.trackProgress(it, AchievementType.SCAVENGER_HUNT, 1)
            Log.d(TAG, "Player '${it.name}' awarded completion bonus and achievement.")
        }
    }

    /**
     * Retrieves the current active scavenger hunt items.
     */
    fun getActiveScavengerHuntItems(): List<ScavengerHuntItem> {
        Log.d(TAG, "Retrieving ${activeScavengerHuntItems.size} active scavenger hunt items.")
        return activeScavengerHuntItems
    }

    /**
     * Checks if the scavenger hunt is complete.
     */
    fun isHuntCompleted(): Boolean {
        huntCompleted = activeScavengerHuntItems.all { it.isFound }
        Log.d(TAG, "Hunt completion status: $huntCompleted")
        return huntCompleted
    }
}
