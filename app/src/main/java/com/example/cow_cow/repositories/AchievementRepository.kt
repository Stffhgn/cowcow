package com.example.cow_cow.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
            try {
                val type = object : TypeToken<List<Achievement>>() {}.type
                val loadedAchievements: List<Achievement> = gson.fromJson(json, type)
                _achievements.postValue(loadedAchievements)
                Log.d("AchievementRepository", "Achievements loaded successfully. Total loaded: \${loadedAchievements.size}")
            } catch (e: JsonSyntaxException) {
                Log.e("AchievementRepository", "Failed to parse achievements JSON: \${e.message}")
                _achievements.postValue(emptyList())
            }
        } else {
            _achievements.postValue(emptyList())  // Set empty if no achievements are saved
            Log.d("AchievementRepository", "No achievements found in storage")
        }
    }

    /**
     * Save achievements to SharedPreferences.
     */
    private fun saveAchievements() {
        val achievementsList = _achievements.value ?: emptyList()
        val json = gson.toJson(achievementsList)
        val editor = sharedPreferences.edit()
        editor.putString(ACHIEVEMENTS_KEY, json)
        val success = editor.commit()
        if (success) {
            Log.d("AchievementRepository", "Achievements saved successfully")
        } else {
            Log.e("AchievementRepository", "Failed to save achievements")
        }
    }

    /**
     * Unlock a new achievement, update progress, and save the updated list for a specific player.
     *
     * @param player The player unlocking the achievement.
     * @param achievement The achievement to be unlocked.
     */
    fun unlockAchievement(player: Player, achievement: Achievement) {
        Log.d("AchievementRepository", "Attempting to unlock achievement '\${achievement.name}' for player \${player.name}")

        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        // Find the existing achievement to unlock
        val existingAchievement = currentAchievements.find { it.id == achievement.id }

        existingAchievement?.let {
            if (!it.isUnlocked && it.checkProgress()) {
                Log.d("AchievementRepository", "Achievement '\${it.name}' unlocked for player \${player.name}")
                it.unlockAchievement()
                _achievements.postValue(currentAchievements)
                saveAchievements()
            } else {
                Log.d("AchievementRepository", "Achievement '\${it.name}' is either already unlocked or progress not met.")
            }
        } ?: run {
            Log.w("AchievementRepository", "Achievement with ID '\${achievement.id}' not found.")
        }
    }

    /**
     * Check if an achievement is unlocked based on its ID.
     */
    fun isAchievementUnlocked(achievementId: String): Boolean {
        val unlocked = _achievements.value?.any { it.id == achievementId && it.isUnlocked } == true
        Log.d("AchievementRepository", "Achievement with ID $achievementId unlocked status: $unlocked")
        return unlocked
    }

    /**
     * Get the list of achievements for a player.
     */
    fun getAchievementsForPlayer(playerId: String): List<Achievement> {
        val playerAchievements = _achievements.value?.filter { it.playerId == playerId } ?: emptyList()
        Log.d("AchievementRepository", "Achievements fetched for player ID: $playerId. Total: \${playerAchievements.size}")
        return playerAchievements
    }

    /**
     * Increment achievement progress, check for unlocking.
     */
    fun incrementAchievementProgress(achievementId: String, progress: Int) {
        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        currentAchievements.find { it.id == achievementId }?.let { achievement ->
            achievement.incrementProgress(progress)
            Log.d("AchievementRepository", "Incremented progress for achievement ID: $achievementId by $progress")
            _achievements.postValue(currentAchievements)
            saveAchievements()
        } ?: Log.w("AchievementRepository", "Achievement with ID $achievementId not found for progress increment")
    }

    /**
     * Add dynamic or event-based achievements (e.g., special events, scoring milestones).
     */
    fun addAchievement(achievement: Achievement) {
        val currentAchievements = _achievements.value?.toMutableList() ?: mutableListOf()

        // Add the new achievement if it doesn't already exist
        if (currentAchievements.none { it.id == achievement.id }) {
            currentAchievements.add(achievement)
            _achievements.postValue(currentAchievements)
            saveAchievements()
            Log.d("AchievementRepository", "Added new achievement: \${achievement.name}")
        } else {
            Log.w("AchievementRepository", "Achievement '\${achievement.name}' already exists, not adding")
        }
    }

    /**
     * Clear all achievements (e.g., for a new game/reset).
     */
    fun clearAchievements() {
        _achievements.postValue(emptyList())
        saveAchievements()
        Log.d("AchievementRepository", "All achievements cleared")
    }

    /**
     * Add special event achievements dynamically, such as for holidays or game events.
     */
    fun addSpecialEventAchievement(playerId: String, eventDescription: String) {
        addAchievement(
            Achievement(
                id = (playerId.hashCode() * 1000).toString() + "-event",  // Example ID generation
                name = "Special Event",
                description = eventDescription,
                type = AchievementType.SPECIAL_EVENT,
                rewardType = RewardType.BADGE,
                rewardValue = 0,
                playerId = playerId
            )
        )
        Log.d("AchievementRepository", "Special event achievement added for player ID: $playerId")
    }

    /**
     * Add scoring-based achievements dynamically based on performance.
     */
    fun addScoringAchievement(playerId: String, scoreThreshold: Int) {
        val description = "Score exceeded $scoreThreshold points"
        addAchievement(
            Achievement(
                id = (playerId.hashCode() * 1000 + scoreThreshold).toString() + "-score",  // Example ID generation
                name = "High Scorer",
                description = description,
                type = AchievementType.SCORING,
                rewardType = RewardType.POINTS,
                rewardValue = scoreThreshold,
                playerId = playerId
            )
        )
        Log.d("AchievementRepository", "Scoring achievement added for player ID: $playerId with threshold: $scoreThreshold")
    }
}