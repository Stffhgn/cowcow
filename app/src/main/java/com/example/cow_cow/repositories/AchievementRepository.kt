package com.example.cow_cow.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.enums.AchievementType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AchievementRepository(private val context: Context) {

    // LiveData to store the list of achievements
    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> get() = _achievements

    // Gson instance to handle JSON conversions
    private val gson = Gson()

    // SharedPreferences for persistent storage
    private val sharedPreferences = context.getSharedPreferences("achievements_pref", Context.MODE_PRIVATE)
    private val ACHIEVEMENTS_KEY = "ACHIEVEMENTS_KEY"

    init {
        // Load achievements on initialization
        loadAchievements()
    }

    /**
     * Load achievements from SharedPreferences.
     */
    private fun loadAchievements() {
        val json = sharedPreferences.getString(ACHIEVEMENTS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<List<Achievement>>() {}.type
            val loadedAchievements: List<Achievement> = gson.fromJson(json, type)
            _achievements.value = loadedAchievements
        } else {
            _achievements.value = emptyList()  // Set empty if no achievements are saved
        }
    }

    /**
     * Save achievements to SharedPreferences.
     */
    private fun saveAchievements() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(_achievements.value)
        editor.putString(ACHIEVEMENTS_KEY, json)
        editor.apply()
    }

    /**
     * Unlock a new achievement, update progress, and save the updated list.
     */
    fun unlockAchievement(achievement: Achievement) {
        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        // If not unlocked, unlock the achievement and save progress
        val existingAchievement = currentAchievements.find { it.id == achievement.id }
        existingAchievement?.let {
            if (!it.isUnlocked && it.checkProgress()) {
                it.unlockAchievement()
                _achievements.value = currentAchievements
                saveAchievements()
            }
        }
    }

    /**
     * Check if an achievement is unlocked based on its ID.
     */
    fun isAchievementUnlocked(achievementId: Int): Boolean {
        return _achievements.value?.any { it.id == achievementId && it.isUnlocked } == true
    }

    /**
     * Get the list of achievements for a player.
     */
    fun getAchievementsForPlayer(playerId: Int): List<Achievement> {
        return _achievements.value?.filter { it.playerId == playerId } ?: emptyList()
    }

    /**
     * Increment achievement progress, check for unlocking.
     */
    fun incrementAchievementProgress(achievementId: Int, progress: Int) {
        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        currentAchievements.find { it.id == achievementId }?.let { achievement ->
            achievement.incrementProgress(progress)
            _achievements.value = currentAchievements
            saveAchievements()
        }
    }

    /**
     * Add dynamic or event-based achievements (e.g., special events, scoring milestones).
     */
    fun addAchievement(achievement: Achievement) {
        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        // Add the new achievement if it doesn't already exist
        if (currentAchievements.none { it.id == achievement.id }) {
            currentAchievements.add(achievement)
            _achievements.value = currentAchievements
            saveAchievements()
        }
    }

    /**
     * Clear all achievements (e.g., for a new game/reset).
     */
    fun clearAchievements() {
        _achievements.value = emptyList()
        saveAchievements()
    }

    /**
     * Add special event achievements dynamically, such as for holidays or game events.
     */
    fun addSpecialEventAchievement(playerId: Int, eventDescription: String) {
        addAchievement(
            Achievement(
                id = (playerId * 1000) + 1,  // Example ID generation
                name = "Special Event",
                description = eventDescription,
                type = AchievementType.SPECIAL_EVENT,
                rewardType = RewardType.BADGE,
                rewardValue = 0
            )
        )
    }

    /**
     * Add scoring-based achievements dynamically based on performance.
     */
    fun addScoringAchievement(playerId: Int, scoreThreshold: Int) {
        val description = "Score exceeded $scoreThreshold points"
        addAchievement(
            Achievement(
                id = (playerId * 1000) + scoreThreshold,  // Example ID generation
                name = "High Scorer",
                description = description,
                type = AchievementType.SCORING,
                rewardType = RewardType.POINTS,
                rewardValue = scoreThreshold
            )
        )
    }
}
