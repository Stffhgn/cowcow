package com.example.cow_cow.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.managers.AchievementManager

class AchievementViewModel(
    private val achievementManager: AchievementManager
) : ViewModel() {

    private val TAG = "AchievementViewModel"

    // LiveData to observe the player's unlocked achievements
    private val _unlockedAchievements = MutableLiveData<List<Achievement>>()
    val unlockedAchievements: LiveData<List<Achievement>> get() = _unlockedAchievements

    // LiveData to track loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData to handle error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // LiveData to track the overall progress towards achievements
    private val _achievementProgress = MutableLiveData<Map<Achievement, Float>>()
    val achievementProgress: LiveData<Map<Achievement, Float>> get() = _achievementProgress

    // LiveData to notify the UI when a new achievement is unlocked
    private val _newAchievementUnlocked = MutableLiveData<Achievement>()
    val newAchievementUnlocked: LiveData<Achievement> get() = _newAchievementUnlocked

    /**
     * Load the player's achievements using the AchievementManager.
     */
    fun loadAchievements(player: Player) {
        _isLoading.value = true  // Set loading to true before starting the process
        try {
            val achievements = achievementManager.getAchievements()
            _unlockedAchievements.value = achievements.filter { it.isUnlocked }
            checkAchievementProgress(player)  // Update progress for the UI
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load achievements: ${e.message}"
        } finally {
            _isLoading.value = false  // Set loading to false after the process finishes
        }
    }

    /**
     * Check the player's progress towards all achievements.
     */
    fun checkAchievementProgress(player: Player) {
        try {
            val progressMap = mutableMapOf<Achievement, Float>()
            val achievements = achievementManager.getAchievements()

            // Loop through all achievements and calculate progress
            for (achievement in achievements) {
                val progress = (achievement.currentProgress.toFloat() / achievement.goal).coerceAtMost(1f)
                progressMap[achievement] = progress
            }

            _achievementProgress.value = progressMap
        } catch (e: Exception) {
            _errorMessage.value = "Failed to check achievement progress: ${e.message}"
        }
    }

    /**
     * Attempt to unlock an achievement if the player meets the criteria.
     */
    fun unlockAchievement(player: Player, achievement: Achievement) {
        try {
            if (!achievement.isUnlocked && achievement.checkProgress()) {
                achievementManager.unlockAchievement(player, achievement)
                _newAchievementUnlocked.value = achievement  // Notify UI of new unlock
                loadAchievements(player)  // Reload achievements to reflect changes
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to unlock achievement: ${e.message}"
        }
    }

    /**
     * Add progress to a specific achievement and check if it can be unlocked.
     *
     * @param player The player whose achievement progress is being incremented.
     * @param achievementType The type of achievement to be incremented.
     * @param amount The amount of progress to be added (default is 1).
     */
    fun incrementAchievementProgress(player: Player, achievementType: AchievementType, amount: Int = 1) {
        try {
            // Use the trackProgress method from the AchievementManager to handle progress.
            achievementManager.trackProgress(player, achievementType, amount)

            // Fetch the updated achievement for the player.
            val achievement = player.achievements.find { it.type == achievementType && it.isUnlocked }

            achievement?.let {
                // If the achievement is unlocked, notify the player.
                Log.d(TAG, "Achievement unlocked: ${it.name}")
            } ?: Log.d(TAG, "Achievement progress updated but not yet unlocked for type: $achievementType")

        } catch (e: Exception) {
            _errorMessage.value = "Failed to increment achievement progress: ${e.message}"
            Log.e(TAG, "Error incrementing achievement progress: ${e.message}", e)
        }
    }


    /**
     * Reset all achievements for a fresh start (e.g., for a new game or testing).
     */
    fun resetAchievements(player: Player) {
        try {
            achievementManager.getAchievements().forEach { achievement ->
                achievement.isUnlocked = false
                achievement.currentProgress = 0
            }
            _unlockedAchievements.value = emptyList()  // Reset in UI
            _achievementProgress.value = emptyMap()    // Clear progress in UI
        } catch (e: Exception) {
            _errorMessage.value = "Failed to reset achievements: ${e.message}"
        }
    }

    /**
     * Handle special achievements that are event-driven (e.g., holiday events, high scores, or custom milestones).
     *
     * This method serves as a placeholder for handling special achievements that are triggered by
     * specific events or descriptions. For example, it could be used for holiday-themed achievements,
     * player-specific milestones, or unique rewards that aren't covered by regular progression.
     *
     * @param player The player who might receive the special achievement.
     * @param eventIdentifier A string or identifier describing the event (e.g., "Holiday_2024", "HighScore_Milestone").
     */
    fun handleSpecialAchievement(player: Player, eventIdentifier: String) {
        try {
            // Placeholder logic for handling special achievements
            Log.d(TAG, "Handling special achievement for event: $eventIdentifier")

            // Example: Call a method in the AchievementManager to handle the event.
            // This could be further expanded with logic to handle specific event types.
            achievementManager.handleSpecialAchievement(player, eventIdentifier)

            // Reload achievements to reflect any new additions or changes.
            loadAchievements(player)

        } catch (e: Exception) {
            _errorMessage.value = "Failed to add special achievement: ${e.message}"
            Log.e(TAG, "Error handling special achievement for event: $eventIdentifier", e)
        }
    }


    /**
     * Handle scoring achievements when a player reaches a specific score threshold.
     */
    fun handleScoringAchievement(player: Player, scoreThreshold: Int) {
        try {
            achievementManager.handleScoringAchievement(player, scoreThreshold)
            loadAchievements(player)  // Reload achievements to reflect new additions
        } catch (e: Exception) {
            _errorMessage.value = "Failed to add scoring achievement: ${e.message}"
        }
    }

    /**
     * Clear any error messages in the UI.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
