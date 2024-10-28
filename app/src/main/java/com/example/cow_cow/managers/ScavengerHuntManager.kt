package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.utils.ScavengerHuntItemGenerator
import com.example.cow_cow.enums.AchievementType

object ScavengerHuntManager {

    private val activeScavengerHuntItems = mutableListOf<ScavengerHuntItem>()
    private var huntCompleted: Boolean = false
    private const val TAG = "ScavengerHuntManager"

    /**
     * Starts a new scavenger hunt by generating items one at a time until 5 items are active.
     *
     * @param player The player for whom the scavenger hunt is generated.
     * @param context The context required for generating scavenger hunt items.
     */
    fun startScavengerHunt(players: List<Player>, context: Context) {
        activeScavengerHuntItems.clear()
        huntCompleted = false

        // Generate the initial set of up to 5 scavenger hunt items.
        repeat(5) {
            // Optionally, you could pick a random player for each item generation or adjust logic as needed.
            val player = players.random() // Example: picking a random player each time
            addNewScavengerHuntItem(player) // Removed the context parameter
        }
        Log.d(TAG, "Scavenger hunt started with ${activeScavengerHuntItems.size} items for ${players.size} players.")

        // Notify the UI that items have been loaded.
        updateScavengerHuntView()
    }
    /**
     * Marks a scavenger hunt item as found by the player.
     * Replaces the found item with a new one.
     *
     * @param item The scavenger hunt item that was found.
     * @param player The player who found the item.
     * @param context The context required for generating new items.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player, context: Context) {
        Log.d(TAG, "Marking item '${item.name}' as found by player ${player.name}.")

        val foundItem = activeScavengerHuntItems.find { it.name == item.name }
        foundItem?.let {
            it.isFound = true
            Log.d(TAG, "Item '${item.name}' marked as found.")

            // Award points for finding the item
            val pointsAwarded = it.getPoints()
            ScoreManager.addPointsToPlayer(player, pointsAwarded)
            Log.d(TAG, "Player ${player.name} awarded $pointsAwarded points for finding item '${item.name}'.")

            // Replace the found item with a new one
            activeScavengerHuntItems.remove(it)
            addNewScavengerHuntItem(player)

            // Save the updated active items
            saveActiveItemsToRepository(player,context)

            // Notify the UI that the list has changed
            updateScavengerHuntView()

            // Check if the hunt is completed
            if (isHuntCompleted()) {
                onHuntCompleted(player)
            }
        } ?: Log.w(TAG, "Item '${item.name}' not found in active scavenger hunt items.")
    }

    /**
     * Generates a new scavenger hunt item and adds it to the list of active items.
     * Keeps only the most recent 5 items.
     *
     * @param player The player for whom the item is being generated.
     * @param context The context required for generating the item.
     */
    private fun addNewScavengerHuntItem(player: Player) {
        // Generate a new scavenger hunt item using the player's context.
        val newItem = ScavengerHuntItemGenerator.generateScavengerHuntItem(player)

        // Add the new item to the list.
        activeScavengerHuntItems.add(newItem)

        // Log the addition for debugging.
        Log.d(TAG, "Generated a new scavenger hunt item: '${newItem.name}' for player: ${player.name}")

        // Ensure the list size does not exceed 5 items.
        if (activeScavengerHuntItems.size > 5) {
            activeScavengerHuntItems.removeAt(0)
        }
    }

    /**
     * Updates the view with the current list of active scavenger hunt items.
     * Implement this according to your UI framework (e.g., RecyclerView).
     */
    private fun updateScavengerHuntView() {
        // This method should notify the UI that the list of active items has changed.
        // For example, if using a RecyclerView adapter, call:
        // scavengerHuntAdapter.submitList(activeScavengerHuntItems.toList())
    }

    /**
     * Saves the updated list of active scavenger hunt items to the repository for a specific player.
     *
     * @param player The player for whom the scavenger hunt items are being saved.
     * @param context The context required for accessing the repository.
     */
    private fun saveActiveItemsToRepository(player: Player, context: Context) {
        val repository = ScavengerHuntRepository(context)
        repository.saveScavengerHuntItems(player, activeScavengerHuntItems)
        Log.d(TAG, "Saved updated scavenger hunt items for player ${player.name} to the repository.")
    }

    /**
     * Handles the event when the scavenger hunt is completed.
     * Awards a special achievement to the team and distributes rewards.
     *
     * @param player The player who triggered the completion.
     */
    private fun onHuntCompleted(player: Player) {
        Log.d(TAG, "Scavenger hunt completed by the team.")

        // Award bonus points and track achievement progress for all players
        val players = PlayerManager.getAllPlayers()
        val bonusPoints = 100

        players.forEach { teamPlayer ->
            // Award bonus points
            ScoreManager.addPointsToPlayer(teamPlayer, bonusPoints)
            Log.d(TAG, "Player ${teamPlayer.name} awarded $bonusPoints bonus points for completing the scavenger hunt.")

            // Track progress towards the Scavenger Hunt Master achievement
            AchievementManager.trackProgress(teamPlayer, AchievementType.SCAVENGER_HUNT, 1)
        }

        Log.d(TAG, "Scavenger hunt completed. All team members rewarded and progress tracked.")
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
     * Checks if the scavenger hunt is completed.
     *
     * @return True if all items are found, false otherwise.
     */
    fun isHuntCompleted(): Boolean {
        huntCompleted = activeScavengerHuntItems.all { it.isFound }
        Log.d(TAG, "Scavenger hunt completed status: $huntCompleted")
        return huntCompleted
    }
}
