package com.example.cow_cow.controllers

import android.content.Context
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.cow_cow.R
import com.example.cow_cow.managers.TeamManager
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.managers.AchievementManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.google.android.flexbox.FlexboxLayout

class RainbowCarController(
    private val context: Context,
    private val flexboxContainer: FlexboxContainer,
    private val gameActivity: GameActivity,
    private val playerManager: PlayerManager,
    private val scoreManager: ScoreManager
) {

    // List of rainbow colors (in order)
    private val rainbowColors = listOf(
        R.color.red,
        R.color.orange,
        R.color.yellow,
        R.color.green,
        R.color.blue,
        R.color.indigo,
        R.color.violet
    )

    // Keep track of the current color index
    private var currentColorIndex = 0

    // Tag for logging
    private val TAG = "RainbowCarController"

    init {
        // Log the team members at the start of the Rainbow Car game
        logTeamPlayers()

        // Start by loading the buttons into the FlexboxContainer
        loadRainbowCarButtons()
    }

    // Function to log the team members at the start of the Rainbow Car game
    private fun logTeamPlayers() {
        // Retrieve all players from PlayerManager
        val teamPlayers = PlayerManager.getAllPlayers().filter { it.isOnTeam }

        Log.d(TAG, "Total Players on the team: ${teamPlayers.size}")
        teamPlayers.forEach { player ->
            Log.d(TAG, "Player ${player.name} is on the team")
        }
    }

    // Function to load the RainbowCar button into the FlexboxContainer
    fun loadRainbowCarButtons() {
        // Clear previous buttons, ensuring no overlap
        clearRainbowCarButton()

        // Use TransitionManager to animate the change, if needed
        TransitionManager.beginDelayedTransition(flexboxContainer)

        // Create the button for the current color
        val colorRes = rainbowColors[currentColorIndex]
        val colorName = getColorName(colorRes)
        val colorButton = createButton(colorName, colorRes)

        // Add the button to the container
        flexboxContainer.addView(colorButton)
        flexboxContainer.requestLayout()
        flexboxContainer.invalidate()

        Log.d(TAG, "RainbowCar button added for color: $colorName")
    }


    // Function to create a stylish ImageButton for each color in the rainbow
    private fun createButton(colorName: String, colorRes: Int): ImageButton {
        return ImageButton(context).apply {
            id = View.generateViewId()
            setBackgroundColor(ContextCompat.getColor(context, colorRes))
            setImageResource(R.drawable.ic_cow_cow_cow) // Placeholder image for the car
            contentDescription = "Find a $colorName car"
            tag = "rainbowCarButton" // Set a tag to identify this button

            // Set layout parameters with margins
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }

            // Use a dynamic drawable background with rounded corners and the specific color
            background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
            backgroundTintList = ContextCompat.getColorStateList(context, colorRes) // Dynamically apply color

            // Add padding for better touch experience
            setPadding(32, 32, 32, 32)

            // Adjust image scaling and tint for better appearance
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageTintList = ContextCompat.getColorStateList(context, android.R.color.white)

            setOnClickListener {
                Log.d(TAG, "$colorName button clicked")
                handleColorSelection()
            }
        }
    }

    // Function to handle the selection of the current color
    private fun handleColorSelection() {
        // Move to the next color in the rainbow if there is one
        if (currentColorIndex < rainbowColors.size - 1) {
            currentColorIndex++
            val nextColor = getColorName(rainbowColors[currentColorIndex])
            Log.d(TAG, "Next color to find: $nextColor")
            loadRainbowCarButtons() // Refresh the button to the next color
        } else {
            // All colors found, handle game completion
            handleGameCompletion()
        }
    }

    // Function to get the name of the color as a string
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

    // Function to handle game completion
    private fun handleGameCompletion() {
        Log.d(TAG, "Rainbow Car game completed.")
        clearRainbowCarButton()
        updateGameStateAfterCompletion()
        Log.d(TAG, "Game state updated after Rainbow Car completion.")
    }

    // Function to remove the specific Rainbow Car button from the FlexboxContainer
    fun clearRainbowCarButton() {
        // Find the view with the specific tag or ID and remove it
        val rainbowCarButton = flexboxContainer.findViewWithTag<View>("rainbowCarButton")
        if (rainbowCarButton != null) {
            flexboxContainer.removeView(rainbowCarButton)
            Log.d(TAG, "Rainbow Car button removed from the FlexboxContainer.")
        } else {
            Log.d(TAG, "No Rainbow Car button found to remove.")
        }
    }

    // Function to update game state after completion
    private fun updateGameStateAfterCompletion() {
        // Reset the game to start from the first color
        currentColorIndex = 0
        Log.d(TAG, "Rainbow Car game reset to initial state.")

        // Award points to all players in the game using ScoreManager
        gameActivity.players.forEach { player ->
            val points = 10  // Example points for completing the Rainbow Car game
            val totalScore = gameActivity.scoreManager.addPointsToPlayer(player, points)
            Log.d(TAG, "[RainbowCarController] Player ${player.name} awarded $points points for Rainbow Car. Total score: $totalScore")
        }

        // Update the game UI
        gameActivity.updateUI()
        Log.d(TAG, "UI updated after Rainbow Car game completion.")
    }

}
