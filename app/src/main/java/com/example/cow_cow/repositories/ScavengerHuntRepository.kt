package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.utils.ScavengerHuntItemGenerator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScavengerHuntRepository(context: Context) {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val EASY_KEY = "SCAVENGER_HUNT_EASY"
    private val MEDIUM_KEY = "SCAVENGER_HUNT_MEDIUM"
    private val HARD_KEY = "SCAVENGER_HUNT_HARD"
    private val ALL_ITEMS_KEY = "SCAVENGER_HUNT_ALL_ITEMS"
    private val gson = Gson()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _easyItems = MutableLiveData<List<ScavengerHuntItem>>()
    private val _mediumItems = MutableLiveData<List<ScavengerHuntItem>>()
    private val _hardItems = MutableLiveData<List<ScavengerHuntItem>>()

    val easyItems: LiveData<List<ScavengerHuntItem>> get() = _easyItems
    val mediumItems: LiveData<List<ScavengerHuntItem>> get() = _mediumItems
    val hardItems: LiveData<List<ScavengerHuntItem>> get() = _hardItems

    private val TAG = "ScavengerHuntRepository"

    init {
        loadAllItems()
    }

    /**
     * Loads all scavenger hunt items from SharedPreferences, generating defaults if none are saved.
     */
    private fun loadAllItems() {
        val allItems = getSavedScavengerHuntItems().ifEmpty { generateAndSaveDefaultItems() }
        partitionAndLoadItemsByDifficulty(allItems)
    }

    /**
     * Retrieves all saved scavenger hunt items or returns an empty list if none are found.
     */
    fun getSavedScavengerHuntItems(): List<ScavengerHuntItem> {
        val json = sharedPreferences.getString(ALL_ITEMS_KEY, null)
        return if (json != null) {
            Log.d(TAG, "Loading all saved scavenger hunt items.")
            gson.fromJson(json, object : TypeToken<List<ScavengerHuntItem>>() {}.type)
        } else {
            Log.d(TAG, "No saved scavenger hunt items found. Returning an empty list.")
            emptyList()
        }
    }

    /**
     * Saves all scavenger hunt items to SharedPreferences and updates difficulty-based lists.
     */
    fun saveAllItems(items: List<ScavengerHuntItem>) {
        val json = gson.toJson(items)
        sharedPreferences.edit().putString(ALL_ITEMS_KEY, json).apply()
        Log.d(TAG, "Saved all scavenger hunt items to SharedPreferences.")
        partitionAndLoadItemsByDifficulty(items)
    }

    private fun saveItemsByKey(key: String, items: List<ScavengerHuntItem>) {
        val json = gson.toJson(items)
        sharedPreferences.edit().putString(key, json).apply()
        Log.d(TAG, "Saved ${items.size} items for key: $key")
    }

    private fun partitionAndLoadItemsByDifficulty(items: List<ScavengerHuntItem>) {
        val easyItems = items.filter { it.difficultyLevel == DifficultyLevel.EASY }
        val mediumItems = items.filter { it.difficultyLevel == DifficultyLevel.MEDIUM }
        val hardItems = items.filter { it.difficultyLevel == DifficultyLevel.HARD }

        _easyItems.value = easyItems
        _mediumItems.value = mediumItems
        _hardItems.value = hardItems

        saveItemsByKey(EASY_KEY, easyItems)
        saveItemsByKey(MEDIUM_KEY, mediumItems)
        saveItemsByKey(HARD_KEY, hardItems)

        Log.d(TAG, "Partitioned items into Easy, Medium, and Hard lists and saved them.")
    }

    /**
     * Generates and saves a default list of scavenger hunt items if no saved items exist.
     */
    private fun generateAndSaveDefaultItems(): List<ScavengerHuntItem> {
        val allItems = ScavengerHuntItemGenerator.generateAllItems()
        saveAllItems(allItems)
        Log.d(TAG, "Generated and saved default scavenger hunt items.")
        return allItems
    }

    fun updateScavengerHuntItemsByDifficulty(difficultyLevel: DifficultyLevel, items: List<ScavengerHuntItem>) {
        val key = when (difficultyLevel) {
            DifficultyLevel.EASY -> EASY_KEY
            DifficultyLevel.MEDIUM -> MEDIUM_KEY
            DifficultyLevel.HARD -> HARD_KEY
        }
        saveItemsByKey(key, items)
        when (difficultyLevel) {
            DifficultyLevel.EASY -> _easyItems.postValue(items)
            DifficultyLevel.MEDIUM -> _mediumItems.postValue(items)
            DifficultyLevel.HARD -> _hardItems.postValue(items)
        }
        Log.d(TAG, "Updated and saved items for $difficultyLevel difficulty.")
    }
}
