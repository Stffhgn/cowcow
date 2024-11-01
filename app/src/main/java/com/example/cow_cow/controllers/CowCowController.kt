package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.handlers.WhoCalledItHandler
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.google.android.flexbox.FlexboxLayout

class CowCowController(
    private val context: Context,
    private val buttonContainer: FlexboxContainer,
    private val gameActivity: GameActivity,
    private val playerManager: PlayerManager,
    private val whoCalledItHandler: WhoCalledItHandler,
    private val scoreManager: ScoreManager
) {

    private val TAG = "CowCowController"

    init {
        // Ensure `WhoCalledItHandler` knows about this controller
        whoCalledItHandler.setCowCowController(this)
    }

    fun loadCowCowButtons(onObjectSelected: (String) -> Unit) {
        Log.d(TAG, "Loading CowCow game buttons")
        buttonContainer.clearButtons()

        // Creating cow-related buttons
        val cowButton = createGameButton(R.string.cow_button, R.drawable.ic_cow_cow_cow, "Cow", onObjectSelected)
        val churchButton = createGameButton(R.string.church_button, R.drawable.ic_cow_cow_church, "Church", onObjectSelected)
        val waterTowerButton = createGameButton(R.string.water_tower_button, R.drawable.ic_cow_cow_water_tower, "Water Tower", onObjectSelected)

        // Add buttons to container
        buttonContainer.addButton(cowButton)
        buttonContainer.addButton(churchButton)
        buttonContainer.addButton(waterTowerButton)
        Log.d(TAG, "CowCow game buttons added to container")
    }

    private fun createGameButton(
        contentDescriptionRes: Int,
        imageRes: Int,
        objectName: String,
        onObjectSelected: ((String) -> Unit)?
    ): ImageButton {
        return ImageButton(context).apply {
            id = View.generateViewId()
            contentDescription = context.getString(contentDescriptionRes)
            setImageResource(imageRes)
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 8, 8, 8) }
            setOnClickListener {
                Log.d(TAG, "Button clicked: $objectName")
                onObjectSelected?.invoke(objectName)
                whoCalledItHandler.openWhoCalledItDialog(gameActivity.supportFragmentManager, objectName)
            }
        }
    }

    // Handles player and score update for the selected object
    fun handlePlayerSelection(playerId: String, objectType: String) {
        val player = playerManager.getPlayerById(playerId)
        val points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }
        player?.let {
            scoreManager.addPointsToPlayer(it, points)
            Log.d(TAG, "Added $points points to player ${it.name} for object $objectType.")
        } ?: Log.e(TAG, "Player not found with ID: $playerId")
    }
}
