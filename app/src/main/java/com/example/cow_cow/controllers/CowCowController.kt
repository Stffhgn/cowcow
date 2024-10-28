package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.gameFragments.TeamManagementFragmentDialog
import com.example.cow_cow.gameFragments.WhoCalledItDialogFragment
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.google.android.flexbox.FlexboxLayout

class CowCowController(
    private val context: Context,
    private val buttonContainer: FlexboxContainer,
    private val gameActivity: GameActivity,
    private val playerManager: PlayerManager
) : OnPlayerAndObjectSelectedListener {

    private val TAG = "CowCowController"
    private val soundController = SoundController(context)
    private val players: MutableList<Player> = PlayerRepository(gameActivity).getPlayers().toMutableList()

    /**
     * Initializes and loads CowCow game buttons into the provided FlexboxContainer.
     * This function is called at the start of the game activity to set up the gameplay UI.
     */
    fun loadCowCowButtons(onObjectSelected: (String) -> Unit) {
        buttonContainer.clearButtons() // Clear any existing buttons

        // Create game buttons
        val cowButton = createGameButton(R.string.cow_button, R.drawable.ic_cow_cow_cow, "Cow", onObjectSelected)
        val churchButton = createGameButton(R.string.church_button, R.drawable.ic_cow_cow_church, "Church", onObjectSelected)
        val waterTowerButton = createGameButton(R.string.water_tower_button, R.drawable.ic_cow_cow_water_tower, "Water Tower", onObjectSelected)
        val whiteFenceButton = createGameButton(R.string.white_fence_button, R.drawable.ic_cow_cow_white_fence, "White Fence", null)

        // Add buttons to container
        buttonContainer.addButton(cowButton)
        buttonContainer.addButton(churchButton)
        buttonContainer.addButton(waterTowerButton)
        buttonContainer.addButton(whiteFenceButton)

        Log.d(TAG, "[CowCowController] Added ${buttonContainer.childCount} buttons to FlexboxContainer.")
    }

    /**
     * Creates an ImageButton with specified properties for game interactions.
     */
    private fun createGameButton(
        contentDescriptionRes: Int,
        imageRes: Int,
        objectName: String,
        onObjectSelected: ((String) -> Unit)?
    ): ImageButton {
        return ImageButton(context).apply {
            id = View.generateViewId()
            contentDescription = context.getString(contentDescriptionRes)
            setBackgroundResource(android.R.drawable.btn_default)
            setImageResource(imageRes)
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8) // Set margins around buttons
            }
            setOnClickListener {
                Log.d(TAG, "[CowCowController] $objectName button clicked")
                onObjectSelected?.invoke(objectName) // Invoke callback if object is selected

                // Call appropriate dialogs based on the object type
                if (objectName != "White Fence") {
                    openWhoCalledItDialog(objectName)
                } else {
                    openTeamManagementDialog()
                }
            }
        }
    }

    /**
     * Handles player selection from dialogs, updates game state, and assigns scores.
     */
    override fun onPlayerSelected(playerId: String, objectType: String) {
        val selectedPlayer = players.find { it.id == playerId }
        selectedPlayer?.let {
            Log.d(TAG, "[CowCowController] Processing event for player: ${it.name}, object: $objectType")
            processGameEvent(it, objectType)
        } ?: Log.e(TAG, "[CowCowController] Player not found with ID: $playerId")
    }

    /**
     * Processes specific game events, calculates points, and updates player scores.
     */
    private fun processGameEvent(player: Player, objectType: String) {
        playSoundForObject(objectType)
        val points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            "Rainbow Car" -> 10
            else -> {
                Log.w(TAG, "[CowCowController] Unrecognized object type: $objectType")
                0
            }
        }
        Log.d(TAG, "[CowCowController] Player ${player.name} called $objectType for $points points.")

        val newScore = gameActivity.scoreManager.addPointsToPlayer(player, points)
        Log.d(TAG, "[CowCowController] Updated score for ${player.name}: $newScore")
    }

    /**
     * Plays the corresponding sound based on the object type.
     */
    private fun playSoundForObject(objectType: String) {
        val soundRes = when (objectType) {
            "Cow" -> R.raw.cow_sound
            "Church" -> R.raw.church_sound
            "Water Tower" -> R.raw.water_tower_sound
            else -> return.also { Log.e(TAG, "[CowCowController] Unknown object type: $objectType") }
        }
        Log.d(TAG, "[CowCowController] Playing sound for object: $objectType")
        soundController.playSound(soundRes)
    }

    /**
     * Opens the "Who Called It" dialog to manage player calls for specific objects.
     */
    private fun openWhoCalledItDialog(objectType: String) {
        Log.d(TAG, "[CowCowController] Opening Who Called It dialog for object: $objectType")
        WhoCalledItDialogFragment.newInstance(players, objectType).show(gameActivity.supportFragmentManager, WhoCalledItDialogFragment.TAG)
    }

    /**
     * Opens the Team Management dialog for managing team activities.
     */
    private fun openTeamManagementDialog() {
        Log.d(TAG, "[CowCowController] Opening Team Management dialog")
        TeamManagementFragmentDialog.newInstance(players).show(gameActivity.supportFragmentManager, TeamManagementFragmentDialog.TAG_DIALOG)
    }
}
