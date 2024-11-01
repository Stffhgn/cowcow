package com.example.cow_cow.controllers

import android.content.Context
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.cow_cow.R
import com.example.cow_cow.handlers.WhoCalledItHandler
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.google.android.flexbox.FlexboxLayout

class RainbowCarController(
    private val context: Context,
    private val buttonContainer: FlexboxContainer,
    private val whoCalledItHandler: WhoCalledItHandler
) {

    private val rainbowColors = listOf(
        R.color.red,
        R.color.orange,
        R.color.yellow,
        R.color.green,
        R.color.blue,
        R.color.indigo,
        R.color.violet
    )
    private var currentColorIndex = 0
    private val TAG = "RainbowCarController"

    init {
        logTeamPlayers()
        loadRainbowCarButtons()
    }

    /**
     * Logs all team players in the game.
     */
    private fun logTeamPlayers() {
        val teamPlayers = whoCalledItHandler.players.filter { it.isOnTeam }
        Log.d(TAG, "Total Players on the team: ${teamPlayers.size}")
        teamPlayers.forEach { player -> Log.d(TAG, "Player ${player.name} is on the team") }
    }

    /**
     * Loads and displays the Rainbow Car button with the current color in the sequence.
     */
    fun loadRainbowCarButtons() {
        clearRainbowCarButton()
        TransitionManager.beginDelayedTransition(buttonContainer)

        val colorRes = rainbowColors.getOrNull(currentColorIndex) ?: return
        val colorName = getColorName(colorRes)
        val colorButton = createButton(colorName, colorRes)

        buttonContainer.addView(colorButton)
        buttonContainer.requestLayout()
        buttonContainer.invalidate()

        Log.d(TAG, "RainbowCar button added for color: $colorName")
    }

    /**
     * Creates an ImageButton for the current color in the sequence.
     */
    private fun createButton(colorName: String, colorRes: Int): ImageButton {
        return ImageButton(context).apply {
            id = View.generateViewId()
            setBackgroundColor(ContextCompat.getColor(context, colorRes))
            setImageResource(R.drawable.ic_cow_cow_cow)
            contentDescription = "Find a $colorName car"
            tag = "rainbowCarButton"

            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(16, 16, 16, 16) }

            background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
            backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
            setPadding(32, 32, 32, 32)

            scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageTintList = ContextCompat.getColorStateList(context, android.R.color.white)

            setOnClickListener {
                Log.d(TAG, "$colorName button clicked")
                handleColorSelection(colorName)
            }
        }
    }

    /**
     * Handles the player's selection of the current color button and advances to the next color.
     */
    private fun handleColorSelection(colorName: String) {
        // Send the selected color and point value to WhoCalledItHandler
        val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
        fragmentManager?.let {
            whoCalledItHandler.handleColorCall(it, colorName)
        }

        if (currentColorIndex < rainbowColors.size - 1) {
            currentColorIndex++
            loadRainbowCarButtons()
        } else {
            handleGameCompletion()
        }
    }

    /**
     * Returns the name of the color based on the resource ID.
     */
    private fun getColorName(colorRes: Int): String {
        return when (colorRes) {
            R.color.red -> "Red"
            R.color.orange -> "Orange"
            R.color.yellow -> "Yellow"
            R.color.green -> "Green"
            R.color.blue -> "Blue"
            R.color.indigo -> "Indigo"
            R.color.violet -> "Violet"
            else -> "Unknown Color"
        }
    }

    /**
     * Handles the end of the Rainbow Car game by resetting the game state.
     */
    private fun handleGameCompletion() {
        Log.d(TAG, "Rainbow Car game completed.")
        clearRainbowCarButton()
        currentColorIndex = 0
    }

    /**
     * Clears the current Rainbow Car button from the container.
     */
    fun clearRainbowCarButton() {
        val rainbowCarButton = buttonContainer.findViewWithTag<View>("rainbowCarButton")
        if (rainbowCarButton != null) {
            buttonContainer.removeView(rainbowCarButton)
            Log.d(TAG, "Rainbow Car button removed from the FlexboxContainer.")
        } else {
            Log.d(TAG, "No Rainbow Car button found to remove.")
        }
    }
}
