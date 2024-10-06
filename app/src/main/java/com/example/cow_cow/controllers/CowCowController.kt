package com.example.cow_cow.controllers

import android.content.Context
import android.util.Log
import com.example.cow_cow.R
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player

class CowCowController(private val context: Context) {

    private val TAG = "CowCowController"
    private val soundController = SoundController(context)

    /**
     * Process game events like Cow, Church, Water Tower calls and delegate scoring to ScoreManager.
     *
     * @param player The player making the call.
     * @param objectType The type of object being called (e.g., "Cow", "Church").
     * @param notifyNews Lambda to send notifications to the GameActivity.
     */
    fun processGameEvent(player: Player, objectType: String, notifyNews: (String) -> Unit) {
        // Play appropriate sound based on the objectType
        when (objectType) {
            "Cow" -> {
                Log.d(TAG, "Processing Cow call")
                soundController.playSound(R.raw.cow_sound)
            }
            "Church" -> {
                Log.d(TAG, "Processing Church call")
                soundController.playSound(R.raw.church_sound)
            }
            "Water Tower" -> {
                Log.d(TAG, "Processing Water Tower call")
                soundController.playSound(R.raw.water_tower_sound)
            }
            else -> {
                Log.e(TAG, "Unknown object type: $objectType")
                return
            }
        }

        // Delegate score calculation to ScoreManager
        val newScore = ScoreManager.calculatePlayerScoreAfterEvent(player, objectType)
        Log.d(TAG, "processGameEvent: Player ${player.name} now has total score $newScore after calling $objectType")

        // Notify the GameActivity about the event
        notifyNews("${player.name} called $objectType and now has $newScore points")
    }
}
