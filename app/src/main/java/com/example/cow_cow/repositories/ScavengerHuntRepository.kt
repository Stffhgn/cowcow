package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.utils.ScavengerHuntItemGenerator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScavengerHuntRepository(context: Context) {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val SCAVENGER_HUNT_KEY = "SCAVENGER_HUNT_KEY"
    private val gson = Gson()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems
    private val TAG = "ScavengerHuntRepository"

    /**
     * Load scavenger hunt items for a specific player and update LiveData.
     *
     * @param player The player for whom the scavenger hunt items are being loaded.
     */
    fun loadScavengerHuntItems(player: Player) {
        Log.d(TAG, "Loading scavenger hunt items for player: ${player.name} with ID: ${player.id}")
        val items = getScavengerHuntItems(player)
        _scavengerHuntItems.value = items
        Log.d(TAG, "Loaded ${items.size} scavenger hunt items for player: ${player.name}")
    }

    /**
     * Retrieve scavenger hunt items from SharedPreferences for a specific player.
     *
     * @param player The player for whom the scavenger hunt items are being retrieved.
     * @return A list of scavenger hunt items for the given player.
     */
    private fun getScavengerHuntItems(player: Player): List<ScavengerHuntItem> {
        val playerKey = "${SCAVENGER_HUNT_KEY}_${player.id}"
        val json = sharedPreferences.getString(playerKey, null)

        return if (json != null) {
            Log.d(TAG, "Found saved scavenger hunt items for player: ${player.name}")
            val type = object : TypeToken<List<ScavengerHuntItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            Log.d(TAG, "No saved items found for player ${player.name}. Loading default scavenger hunt items.")
            getDefaultScavengerHuntItems(player)
        }
    }

    /**
     * Save scavenger hunt items for a specific player.
     *
     * @param player The player for whom the items are being saved.
     * @param items The list of items to be saved.
     */
    fun saveScavengerHuntItems(player: Player, items: List<ScavengerHuntItem>) {
        Log.d(TAG, "Saving ${items.size} scavenger hunt items for player: ${player.name}")
        val playerKey = "${SCAVENGER_HUNT_KEY}_${player.id}"
        val json = gson.toJson(items)

        sharedPreferences.edit().putString(playerKey, json).apply()
        _scavengerHuntItems.value = items

        Log.d(TAG, "Successfully saved ${items.size} items for player: ${player.name}")
    }

    /**
     * Generate default scavenger hunt items for a specific player.
     *
     * @param player The player for whom the items are being generated.
     * @return A list of generated scavenger hunt items.
     */
    private fun getDefaultScavengerHuntItems(player: Player): List<ScavengerHuntItem> {
        Log.d(TAG, "Generating default scavenger hunt items for player: ${player.name}")
        return List(5) {
            ScavengerHuntItemGenerator.generateScavengerHuntItem(player)
        }.also {
            Log.d(TAG, "Generated ${it.size} default items for player: ${player.name}")
        }
    }
}
