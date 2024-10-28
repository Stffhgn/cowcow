package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.cow_cow.R
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.PlayerUtils
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.example.cow_cow.activity.GameActivity
import com.google.android.flexbox.FlexboxLayout.LayoutParams
import com.example.cow_cow.gameFragments.TeamManagementFragmentDialog
import com.example.cow_cow.gameFragments.WhoCalledItDialogFragment
import com.example.cow_cow.gameFragments.WhosPlayingDialogFragment
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.repositories.PlayerRepository
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
     * Initializes and loads CowCow buttons into the provided FlexboxContainer.
     *
     * This function is called when the game activity starts to initialize the gameplay UI.
     * @param onObjectSelected Callback to notify when an object is selected.
     */
    fun loadCowCowButtons(onObjectSelected: (String) -> Unit) {
        // Clear the container before adding new buttons
        buttonContainer.clearButtons()

        // Create and add buttons to the container
        val cowButton = createButton(R.string.cow_button, R.drawable.ic_cow_cow_cow, "Cow", onObjectSelected)
        val churchButton = createButton(R.string.church_button, R.drawable.ic_cow_cow_church, "Church", onObjectSelected)
        val waterTowerButton = createButton(R.string.water_tower_button, R.drawable.ic_cow_cow_water_tower, "Water Tower", onObjectSelected)
        val whiteFenceButton = createButton(R.string.white_fence_button, R.drawable.ic_cow_cow_white_fence, "White Fence", null)

        // Add buttons to the container
        buttonContainer.addButton(cowButton)
        buttonContainer.addButton(churchButton)
        buttonContainer.addButton(waterTowerButton)
        buttonContainer.addButton(whiteFenceButton)

        val buttonCount = buttonContainer.childCount
        Log.d(TAG, "[CowCowController] CowCow buttons added to FlexboxContainer: $buttonCount buttons built and sent.")
    }

    /**
     * Creates an ImageButton for the game with the specified attributes.
     *
     * @param contentDescriptionRes The content description resource ID for the button.
     * @param imageRes The image resource ID for the button.
     * @param objectName The name of the object represented by this button.
     * @param onObjectSelected The click listener that will be called with the object name.
     * @return The created ImageButton.
     */
    private fun createButton(contentDescriptionRes: Int, imageRes: Int, objectName: String, onObjectSelected: ((String) -> Unit)?): ImageButton {
        return ImageButton(context).apply {
            id = View.generateViewId()
            contentDescription = context.getString(contentDescriptionRes)
            setBackgroundResource(android.R.drawable.btn_default) // Default button background to show it's clickable
            setImageResource(imageRes)
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8) // Set margins to add spacing around buttons
            }
            setOnClickListener {
                Log.d(TAG, "[CowCowController] $objectName button clicked")

                // If an object is selected, invoke its callback
                onObjectSelected?.invoke(objectName)

                // Update player counts for game-specific objects and call the appropriate dialog
                if (objectName != "White Fence") {
                    openWhoCalledItDialog(objectName) // Open the Who's Playing dialog
                } else {
                    openTeamManagementDialog() // Open Team Management for White Fence
                }
            }
        }
    }

    /**
     * Handles player selection from dialogs and processes game state updates accordingly.
     *
     * @param playerId The ID of the selected player.
     * @param objectType The type of object involved in the game event (e.g., "Cow", "Church").
     */
    override fun onPlayerSelected(playerId: String, objectType: String) {
        Log.d(TAG, "[CowCowController] Player selected: $playerId for object: $objectType")

        // Find the player from the players list
        val selectedPlayer = players.find { it.id == playerId }

        // If the player is found, process the game event
        if (selectedPlayer != null) {
            processGameEvent(selectedPlayer, objectType, playerManager)
        } else {
            Log.e(TAG, "[CowCowController] Player not found with ID: $playerId")
        }
    }

    /**
     * Processes game events like Cow, Church, Water Tower calls, and delegates scoring to ScoreManager.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     */
    private fun processGameEvent(player: Player, objectType: String, playerManager: PlayerManager) {
        // Play appropriate sound based on the objectType
        playSoundForObject(objectType)
        Log.d(TAG, "[CowCowController] Playing sound for object: $objectType")

        // Determine points for the event (e.g., 1 point for "Cow")
        val points = when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            "Rainbow Car" -> 10
            else -> {
                Log.w(TAG, "[CowCowController] Unrecognized object type: $objectType. Defaulting to 0 points.")
                0
            }
        }

        Log.d(TAG, "[CowCowController] Player ${player.name} called $objectType and earned $points points.")

        // Send points to ScoreManager to update the player's score
        val newScore = gameActivity.scoreManager.addPointsToPlayer(player, points)
        Log.d(TAG, "[CowCowController] Player ${player.name} now has total score $newScore.")
    }


    /**
     * Plays the appropriate sound for the given object type.
     *
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     */
    private fun playSoundForObject(objectType: String) {
        val soundRes = when (objectType) {
            "Cow" -> R.raw.cow_sound
            "Church" -> R.raw.church_sound
            "Water Tower" -> R.raw.water_tower_sound
            else -> {
                Log.e(TAG, "[CowCowController] Unknown object type: $objectType")
                return
            }
        }
        Log.d(TAG, "[CowCowController] Processing $objectType call")
        soundController.playSound(soundRes)
    }

    /**
     * Opens the "Who Called It" dialog, passing the list of players and the object type.
     *
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     */
    fun openWhoCalledItDialog(objectType: String) {
        Log.d(TAG, "[CowCowController] Opening Who Called It dialog for object: $objectType")
        val dialog = WhoCalledItDialogFragment.newInstance(players, objectType)
        dialog.show(gameActivity.supportFragmentManager, WhoCalledItDialogFragment.TAG)
    }

    /**
     * Opens the Team Management Dialog, passing the list of players.
     */
    fun openTeamManagementDialog() {
        Log.d(TAG, "[CowCowController] Team Management Dialog should be opened here")

        // Create an instance of TeamManagementFragmentDialog and pass the list of players
        val dialog = TeamManagementFragmentDialog.newInstance(players)
        dialog.show(gameActivity.supportFragmentManager, TeamManagementFragmentDialog.TAG_DIALOG)
    }
}
