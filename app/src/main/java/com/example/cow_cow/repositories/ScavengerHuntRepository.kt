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

    // Holds the current player's scavenger hunt items.
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    /**
     * Load scavenger hunt items for a specific player and update LiveData.
     *
     * @param player The player for whom the scavenger hunt items are being loaded.
     */
    fun loadScavengerHuntItems(player: Player) {
        val items = getScavengerHuntItems(player)
        _scavengerHuntItems.value = items
        Log.d("ScavengerHuntRepository", "Loaded ${items.size} scavenger hunt items for player: ${player.name}")
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
            val type = object : TypeToken<List<ScavengerHuntItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            Log.d("ScavengerHuntRepository", "No items found for player ${player.name}, loading default scavenger hunt items.")
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
        val playerKey = "${SCAVENGER_HUNT_KEY}_${player.id}"
        val json = gson.toJson(items)
        sharedPreferences.edit().putString(playerKey, json).apply()

        // Update LiveData
        _scavengerHuntItems.value = items
        Log.d("ScavengerHuntRepository", "Saved ${items.size} scavenger hunt items for player: ${player.name}")
    }

    /**
     * Retrieve the default scavenger hunt items for a specific player.
     *
     * @param player The player for whom the items are being generated.
     * @param context The context required for generating scavenger hunt items.
     * @return A list of generated scavenger hunt items.
     */
    private fun getDefaultScavengerHuntItems(player: Player): List<ScavengerHuntItem> {
        return List(5) {
            ScavengerHuntItemGenerator.generateScavengerHuntItem(player)
        }
    }
}
