package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.cow_cow.R
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.handlers.WhoCalledItHandler
import com.example.cow_cow.managers.ScavengerHuntManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.google.android.flexbox.FlexboxLayout

class ScavengerHuntController(
    private val context: Context,
    private val flexboxContainer: FlexboxContainer,
    private val players: List<Player>,
    private val whoCalledItHandler: WhoCalledItHandler,
    private val scavengerHuntManager: ScavengerHuntManager,
) {

    private val TAG = "ScavengerHuntController"

    init {
        Log.d(TAG, "Initializing ScavengerHuntController.")
        loadScavengerHuntButtons()
    }

    /**
     * Loads the scavenger hunt buttons on the board based on active items.
     */
    fun loadScavengerHuntButtons() {
        clearScavengerHuntButtons()
        val activeItems = scavengerHuntManager.getActiveItemsForBoard()
        updateScavengerHuntButtons(activeItems)
    }

    /**
     * Marks an item as found and reloads buttons to update the board.
     */
    fun markItemAsFound(item: ScavengerHuntItem, player: Player) {
        Log.d(TAG, "Marking item '${item.name}' as found for player '${player.name}'")
        scavengerHuntManager.markItemAsFound(item, player)
        loadScavengerHuntButtons() // Refresh buttons after marking item as found
    }

    /**
     * Updates the scavenger hunt buttons based on active items.
     */
    private fun updateScavengerHuntButtons(items: List<ScavengerHuntItem>) {
        items.forEachIndexed { index, item ->
            val itemButton = createScavengerHuntButton(item, index + 1)
            flexboxContainer.addView(itemButton)
        }
        flexboxContainer.requestLayout()
    }

    /**
     * Creates a button for each scavenger hunt item with an onClick listener.
     */
    private fun createScavengerHuntButton(item: ScavengerHuntItem, index: Int): Button {
        return Button(context).apply {
            text = "${item.name} - ${item.points}pts"
            tag = "scavengerHuntButton$index"
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 16, 16, 16) }
            setBackgroundColor(ContextCompat.getColor(context, getColorForDifficulty(item.difficultyLevel)))
            isEnabled = !item.isFound
            setOnClickListener {
                val fragmentActivity = it.context as? FragmentActivity
                fragmentActivity?.supportFragmentManager?.let { fragmentManager ->
                    whoCalledItHandler.openWhoCalledItDialog(fragmentManager, item)
                }
            }
        }
    }


    /**
     * Clears the scavenger hunt buttons from the container.
     */
    private fun clearScavengerHuntButtons() {
        for (i in flexboxContainer.childCount - 1 downTo 0) {
            val view = flexboxContainer.getChildAt(i)
            if (view.tag?.toString()?.startsWith("scavengerHuntButton") == true) {
                flexboxContainer.removeViewAt(i)
            }
        }
    }

    /**
     * Returns the color resource based on the difficulty level.
     */
    private fun getColorForDifficulty(level: DifficultyLevel): Int {
        return when (level) {
            DifficultyLevel.EASY -> R.color.easy_color
            DifficultyLevel.MEDIUM -> R.color.medium_color
            DifficultyLevel.HARD -> R.color.hard_color
        }
    }
}
