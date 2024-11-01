package com.example.cow_cow.handlers

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.controllers.ScavengerHuntController
import com.example.cow_cow.dialogs.WhoCalledItDialogFragment
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem

class WhoCalledItHandler(
    var players: List<Player>,
    private val scoreManager: ScoreManager
) : OnPlayerAndObjectSelectedListener {

    private val TAG = "WhoCalledItHandler"
    private var isScavengerHunt = false
    private var currentScavengerHuntItem: ScavengerHuntItem? = null
    private var scavengerHuntItemPoints: Int = 0
    private var scavengerHuntController: ScavengerHuntController? = null
    private var cowCowController: CowCowController? = null

    /**
     * Links the ScavengerHuntController with the handler.
     */
    fun setScavengerHuntController(controller: ScavengerHuntController) {
        scavengerHuntController = controller
    }

    /**
     * Links the CowCowController with the handler.
     */
    fun setCowCowController(controller: CowCowController) {
        cowCowController = controller
    }

    /**
     * Opens the WhoCalledItDialogFragment for any called object, whether it’s from CowCow, ScavengerHunt, or RainbowCar.
     */
    fun openWhoCalledItDialog(
        fragmentManager: FragmentManager,
        calledObject: Any // Accepts either String or ScavengerHuntItem
    ) {
        if (calledObject is ScavengerHuntItem) {
            isScavengerHunt = true
            currentScavengerHuntItem = calledObject
            scavengerHuntItemPoints = calledObject.points
            WhoCalledItDialogFragment.newInstance(players, listener = this, calledObject = calledObject.name, scoreManager = scoreManager)
                .show(fragmentManager, WhoCalledItDialogFragment.TAG)
        } else if (calledObject is String) {
            isScavengerHunt = false
            WhoCalledItDialogFragment.newInstance(players, listener = this, calledObject = calledObject, scoreManager = scoreManager)
                .show(fragmentManager, WhoCalledItDialogFragment.TAG)
        }
    }

    override fun onPlayerSelected(playerId: String, objectType: String) {
        val player = players.find { it.id == playerId }

        if (player == null) {
            Log.e(TAG, "Player not found with ID: $playerId")
            return
        }

        Log.d(TAG, "Player selected: ${player.name} for object type: $objectType")

        if (isScavengerHunt && currentScavengerHuntItem != null) {
            val points = scavengerHuntItemPoints
            scoreManager.addPointsToPlayer(player, points)
            Log.d(TAG, "Scavenger hunt item '$objectType' awarded $points points to ${player.name}. New score: ${player.basePoints}")

            scavengerHuntController?.markItemAsFound(currentScavengerHuntItem!!, player)

            // Reset flags
            isScavengerHunt = false
            currentScavengerHuntItem = null
        } else {
            val points = getStandardObjectPoints(objectType)
            scoreManager.addPointsToPlayer(player, points)
            Log.d(TAG, "Standard object '$objectType' awarded $points points to ${player.name}. New score: ${player.basePoints}")
        }
    }

    /**
     * Handles color calls from RainbowCarController, awarding points for each color call.
     */
    fun handleColorCall(fragmentManager: FragmentManager, colorName: String) {
        openWhoCalledItDialog(fragmentManager, colorName)
    }

    /**
     * Helper to assign points for standard CowCow objects and colors from RainbowCar.
     */
    private fun getStandardObjectPoints(objectType: String): Int {
        return when (objectType) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet" -> 5
            else -> 0
        }
    }
}
