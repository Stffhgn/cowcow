package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.example.cow_cow.R
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
    private val playerManager: PlayerManager,
    private val scoreManager: ScoreManager,
    private val scavengerHuntViewModel: ScavengerHuntViewModel
) : OnPlayerAndObjectSelectedListener {

    private val TAG = "ScavengerHuntController"

    init {
        Log.d(TAG, "Initializing ScavengerHuntController.")
        observeScavengerHuntItems()
    }

    // Observes the scavenger hunt items in the ViewModel
    private fun observeScavengerHuntItems() {
        scavengerHuntViewModel.scavengerHuntItems.observe(activity as LifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                Log.w(TAG, "No scavenger hunt items available to display.")
            } else {
                Log.d(TAG, "Scavenger hunt items updated: ${items.size} items found.")
                updateScavengerHuntButtons(items)
            }
        }
    }

    override fun onPlayerSelected(playerId: String, objectType: String) {
        Log.d(TAG, "Player selected with ID: $playerId for object type: $objectType")
        val player = playerManager.getAllPlayers().find { it.id == playerId }
        val item = scavengerHuntViewModel.scavengerHuntItems.value?.find { it.name == objectType }

        if (player != null && item != null) {
            Log.d(TAG, "Found player ${player.name} and item '${item.name}' - processing.")
            processScavengerHuntItem(player, item)
        } else {
            Log.e(TAG, "Error: Player or item not found - Player: $player, Item: $item")
        }
    }

    // Processes when an item is found
    private fun processScavengerHuntItem(player: Player, item: ScavengerHuntItem) {
        Log.d(TAG, "Marking '${item.name}' as found for player ${player.name}")
        scavengerHuntViewModel.markItemAsFound(item, player)

        val points = item.getPoints()
        val updatedScore = scoreManager.addPointsToPlayer(player, points)

        Log.d(TAG, "Awarded $points points to ${player.name}. New total score: $updatedScore")
        updateScavengerHuntButtons(scavengerHuntViewModel.scavengerHuntItems.value ?: emptyList())
    }

    private fun updateScavengerHuntButtons(items: List<ScavengerHuntItem>) {
        Log.d(TAG, "Updating scavenger hunt buttons with ${items.size} items.")

        // Clear only scavenger hunt buttons by finding views tagged specifically for scavenger hunt
        flexboxContainer.children.filter { it.tag == "scavengerHuntButton" }.forEach { flexboxContainer.removeView(it) }
        items.forEach { item ->
            val itemButton = createScavengerHuntButton(item)
            flexboxContainer.addView(itemButton)
            Log.d(TAG, "Button created for item '${item.name}' with isFound status: ${item.isFound}")
        }

        flexboxContainer.requestLayout()
        flexboxContainer.invalidate()
    }

    private fun createScavengerHuntButton(item: ScavengerHuntItem): Button {
        return Button(context).apply {
            text = item.name
            tag = "scavengerHuntButton"  // Tag to identify scavenger hunt buttons
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            setBackgroundColor(ContextCompat.getColor(context, getColorForDifficulty(item.difficultyLevel)))
            isEnabled = !item.isFound
            setOnClickListener {
                Log.d(TAG, "Scavenger hunt item '${item.name}' button clicked.")
                openWhoCalledItDialog(item)
            }
        }
    }

    // Starts the scavenger hunt through the ViewModel
    fun loadScavengerHuntItems() {
        Log.d(TAG, "Requesting scavenger hunt items from ViewModel.")
        scavengerHuntViewModel.startScavengerHunt()
    }

    // Opens the Who Called It dialog for the selected scavenger hunt item
    private fun openWhoCalledItDialog(item: ScavengerHuntItem) {
        val players = playerManager.getAllPlayers()
        if (players.isEmpty()) {
            Log.e(TAG, "No players available for dialog opening.")
            return
        }

        Log.d(TAG, "Opening Who Called It dialog for item '${item.name}'.")
        WhoCalledItDialogFragment.newInstance(players, item.name)
            .show(activity.supportFragmentManager, WhoCalledItDialogFragment.TAG)
    }

    // Maps item difficulty levels to colors for button background
    private fun getColorForDifficulty(level: DifficultyLevel): Int {
        return when (level) {
            DifficultyLevel.EASY -> R.color.easy_color
            DifficultyLevel.MEDIUM -> R.color.medium_color
            DifficultyLevel.HARD -> R.color.hard_color
        }
    }
}
