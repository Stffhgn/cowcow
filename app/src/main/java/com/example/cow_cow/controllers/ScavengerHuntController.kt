package com.example.cow_cow.controllers

import android.content.Context
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.gameFragments.WhoCalledItDialogFragment
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.viewModels.ScavengerHuntViewModel
import com.google.android.flexbox.FlexboxLayout

class ScavengerHuntController(
    private val activity: FragmentActivity,
    private val context: Context,
    private val flexboxContainer: FlexboxContainer,
    private val gameActivity: GameActivity,
    private val playerManager: PlayerManager,
    private val scoreManager: ScoreManager,
    private val scavengerHuntViewModel: ScavengerHuntViewModel // Changed
) : OnPlayerAndObjectSelectedListener {

    private val TAG = "ScavengerHuntController"

    /**
     * Handles player selection from the dialog and processes the scavenger hunt event.
     *
     * @param playerId The ID of the selected player.
     * @param objectType The name of the scavenger hunt item found.
     */
    override fun onPlayerSelected(playerId: String, objectType: String) {
        Log.d(TAG, "Player selected: $playerId for item: $objectType")

        // Retrieve the list of players
        val players = playerManager.getAllPlayers()

        // Find the selected player using their ID
        players.find { it.id == playerId }?.let { selectedPlayer ->
            processScavengerHuntItem(selectedPlayer, objectType)
        } ?: Log.e(TAG, "Player not found with ID: $playerId")
    }

    /**
     * Process the scavenger hunt item found by the player.
     * Rewards may include points, achievements, or power-ups.
     *
     * @param player The player who found the item.
     * @param itemName The name of the scavenger hunt item.
     */
    private fun processScavengerHuntItem(player: Player, itemName: String) {
        scavengerHuntViewModel.scavengerHuntItems.value?.find { it.name == itemName }?.let { item ->
            // Mark the item as found for the player
            scavengerHuntViewModel.markItemAsFound(item, player)
            Log.d(TAG, "Player ${player.name} found item: ${item.name}")

            // Randomly determine a reward (points, power-up)
            val rewardType = (1..2).random()
            when (rewardType) {
                1 -> {
                    val points = item.getPoints()
                    val totalScore = scoreManager.addPointsToPlayer(player, points)
                    Log.d(TAG, "Player ${player.name} earned $points points. Total score: $totalScore")
                }
                2 -> {
                    // Implement power-up reward logic if needed
                    Log.d(TAG, "Player ${player.name} received a power-up reward.")
                }
            }

            // Update the UI to reflect the found item
            loadScavengerHuntButtons()
        } ?: Log.e(TAG, "Item $itemName not found in the scavenger hunt.")
    }

    /**
     * Start and load scavenger hunt items into the view.
     * Observes changes in the ViewModel to ensure the view stays updated.
     */
    fun loadScavengerHuntItems() {
        // Observe the scavenger hunt items from the ViewModel
        scavengerHuntViewModel.scavengerHuntItems.observe(activity, Observer { items ->
            loadScavengerHuntButtons()
            Log.d(TAG, "Loaded ${items.size} scavenger hunt items from the ViewModel.")
        })
    }

    /**
     * Load scavenger hunt buttons into the FlexboxContainer.
     */
    private fun loadScavengerHuntButtons() {
        val items = scavengerHuntViewModel.scavengerHuntItems.value ?: emptyList()
        TransitionManager.beginDelayedTransition(flexboxContainer)
        flexboxContainer.removeAllViews()
        items.forEach { item ->
            val itemButton = createButton(item)
            flexboxContainer.addView(itemButton)
        }
        flexboxContainer.requestLayout()
        Log.d(TAG, "Loaded ${items.size} scavenger hunt buttons.")
    }

    /**
     * Create a button for a scavenger hunt item.
     */
    private fun createButton(item: ScavengerHuntItem): Button {
        return Button(context).apply {
            text = item.name
            id = View.generateViewId()
            tag = "scavengerHuntButton_${item.name}"

            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }

            // Set button background color based on difficulty level
            val colorRes = when (item.difficultyLevel) {
                DifficultyLevel.EASY -> R.color.easy_color
                DifficultyLevel.MEDIUM -> R.color.medium_color
                DifficultyLevel.HARD -> R.color.hard_color
            }
            setBackgroundColor(ContextCompat.getColor(context, colorRes))

            setOnClickListener {
                Log.d(TAG, "Scavenger hunt item clicked: ${item.name}")
                openWhoCalledItDialog(item)
            }

            // Disable the button if the item is found
            isEnabled = !item.isFound
        }
    }

    /**
     * Opens the "Who Called It" dialog, passing the list of players and the scavenger hunt item.
     */
    private fun openWhoCalledItDialog(item: ScavengerHuntItem) {
        Log.d(TAG, "Opening Who Called It dialog for item: ${item.name}")
        val dialog = WhoCalledItDialogFragment.newInstance(playerManager.getAllPlayers(), item.name)
        dialog.show(activity.supportFragmentManager, WhoCalledItDialogFragment.TAG)
    }
}
