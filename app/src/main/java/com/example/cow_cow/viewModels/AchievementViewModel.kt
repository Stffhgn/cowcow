package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.AchievementRepository

class AchievementViewModel(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

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

    // LiveData to observe the player's unlocked achievements
    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> get() = _achievements


    // LiveData to notify the UI when a new achievement is unlocked
    private val _newAchievementUnlocked = MutableLiveData<Achievement>()
    val newAchievementUnlocked: LiveData<Achievement> get() = _newAchievementUnlocked

    // LiveData for any errors encountered during achievement operations
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    /**
     * Load the player's unlocked achievements from the repository.
     */
    fun loadAchievements(player: Player) {
        _isLoading.value = true  // Set loading to true before starting the process
        try {
            val achievements = achievementRepository.getAchievementsForPlayer(player.id)
            _achievements.value = achievements
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load achievements: ${e.message}"  // Set error message
        } finally {
            _isLoading.value = false  // Set loading to false after the process finishes
        }
    }

    /**
     * Check the player's progress towards all achievements.
     */
    fun checkAchievementProgress(player: Player) {
        try {
            val achievements = achievementRepository.getAchievementsForPlayer(player.id)
            val progressMap = mutableMapOf<Achievement, Float>()

            // Loop through all achievements and calculate progress
            for (achievement in achievements) {
                val progress = (achievement.currentProgress.toFloat() / achievement.goal).coerceAtMost(1f)
                progressMap[achievement] = progress
            }

            _achievementProgress.value = progressMap
        } catch (e: Exception) {
            _error.value = "Failed to check achievement progress: ${e.message}"
        }
    }

    /**
     * Unlock an achievement if the player meets the criteria.
     */
    fun unlockAchievement(player: Player, achievement: Achievement) {
        try {
            if (!achievement.isUnlocked && achievement.checkProgress()) {
                achievement.unlockAchievement()
                achievementRepository.addAchievement(achievement)
                _newAchievementUnlocked.value = achievement
                loadAchievements(player)  // Reload the achievements to reflect changes
            }
        } catch (e: Exception) {
            _error.value = "Failed to unlock achievement: ${e.message}"
        }
    }

    /**
     * Add progress to a specific achievement and check if it can be unlocked.
     */
    fun incrementAchievementProgress(player: Player, achievement: Achievement, amount: Int = 1) {
        try {
            achievement.incrementProgress(amount)
            if (achievement.checkProgress()) {
                unlockAchievement(player, achievement)
            } else {
                achievementRepository.addAchievement(achievement)
                checkAchievementProgress(player)  // Refresh progress in the UI
            }
        } catch (e: Exception) {
            _error.value = "Failed to increment achievement progress: ${e.message}"
        }
    }

    /**
     * Reset all achievements for a fresh start (e.g., for a new game or testing).
     */
    fun resetAchievements(player: Player) {
        try {
            val achievements = achievementRepository.getAchievementsForPlayer(player.id).toMutableList()

            // Reset each achievement's status
            for (achievement in achievements) {
                achievement.isUnlocked = false
                achievement.currentProgress = 0
            }

            achievementRepository.clearAchievements()  // Clear from repository
            _unlockedAchievements.value = emptyList()  // Reset in UI
            _achievementProgress.value = emptyMap()    // Clear progress in UI

        } catch (e: Exception) {
            _error.value = "Failed to reset achievements: ${e.message}"
        }
    }

    /**
     * Handle special achievements that are event-driven (e.g., holiday events or scoring).
     */
    fun handleSpecialAchievement(player: Player, description: String) {
        try {
            achievementRepository.addSpecialEventAchievement(player.id, description)
            loadAchievements(player)  // Reload achievements to reflect new additions
        } catch (e: Exception) {
            _error.value = "Failed to add special achievement: ${e.message}"
        }
    }

    /**
     * Handle scoring achievements when a player reaches a specific score threshold.
     */
    fun handleScoringAchievement(player: Player, scoreThreshold: Int) {
        try {
            achievementRepository.addScoringAchievement(player.id, scoreThreshold)
            loadAchievements(player)  // Reload achievements to reflect new additions
        } catch (e: Exception) {
            _error.value = "Failed to add scoring achievement: ${e.message}"
        }
    }
}
