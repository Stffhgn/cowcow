package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScavengerHuntRepository {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val SCAVENGER_HUNT_KEY = "SCAVENGER_HUNT_KEY"
    private val gson = Gson()

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    // Get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Load scavenger hunt items from local storage and update LiveData
    fun loadScavengerHuntItems(context: Context) {
        val items = getScavengerHuntItems(context)
        _scavengerHuntItems.value = items
    }

    // Retrieve scavenger hunt items from local storage (SharedPreferences)
    fun getScavengerHuntItems(context: Context): List<ScavengerHuntItem> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(SCAVENGER_HUNT_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<ScavengerHuntItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            // If no data is found, return the default hardcoded items
            getDefaultScavengerHuntItems()
        }
    }

    // Save scavenger hunt items to SharedPreferences and update LiveData
    fun saveScavengerHuntItems(items: List<ScavengerHuntItem>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(items)
        editor.putString(SCAVENGER_HUNT_KEY, json)
        editor.apply()

        // Update LiveData so observers are notified
        _scavengerHuntItems.value = items
    }

    // Add a new scavenger hunt item to the list and save
    fun addScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        val currentItems = getScavengerHuntItems(context).toMutableList()
        currentItems.add(item)
        saveScavengerHuntItems(currentItems, context)
    }

    // Remove an existing scavenger hunt item from the list and save
    fun removeScavengerHuntItem(item: ScavengerHuntItem, context: Context) {
        val currentItems = getScavengerHuntItems(context).toMutableList()
        currentItems.remove(item)
        saveScavengerHuntItems(currentItems, context)
    }

    // Clear all scavenger hunt items (for reset)
    fun clearScavengerHuntItems(context: Context) {
        saveScavengerHuntItems(emptyList(), context)
    }

    // Filter scavenger hunt items based on tags
    fun getFilteredScavengerHuntItems(tags: List<String>, context: Context): List<ScavengerHuntItem> {
        return getScavengerHuntItems(context).filter { item ->
            tags.any { tag -> item.tags.contains(tag) }
        }
    }

    // Retrieve the default scavenger hunt items (hardcoded list)
    private fun getDefaultScavengerHuntItems(): List<ScavengerHuntItem> {
        return listOf(
            ScavengerHuntItem(
                name = "Find a red car",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "See a yellow taxi",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.ADULTS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            // Add more default items as needed...
        )
    }

    // Method to update scavenger hunt items from server (for OTA updates)
    fun updateScavengerHuntItemsFromServer(items: List<ScavengerHuntItem>, context: Context) {
        saveScavengerHuntItems(items, context)
    }
}
