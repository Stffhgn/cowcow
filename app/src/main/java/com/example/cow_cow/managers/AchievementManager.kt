package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType
import com.example.cow_cow.utils.DataUtils

class AchievementManager(private val context: Context) {

    private val achievements = mutableListOf<Achievement>() // Local list of achievements
    private val TAG = "AchievementManager"

    init {
        Log.d(TAG, "Initializing AchievementManager and loading achievements")
        loadAchievements()  // Load achievements when the manager is initialized
    }

    /**
     * Check if a player has met the conditions for any achievements.
     *
     * @param player The player whose achievements are being checked.
     */
    fun checkAchievements(player: Player) {
        Log.d(TAG, "Checking achievements for player: ${player.name}")
        player.achievements.forEach { achievement ->
            if (achievement.checkProgress()) {
                Log.d(TAG, "Achievement unlocked: ${achievement.name}")
                unlockAchievement(player, achievement)
            } else {
                Log.d(TAG, "Achievement progress not met for: ${achievement.name}")
            }
        }
    }

    // Track progress for progressive achievements
    fun trackProgress(player: Player, achievementType: AchievementType, progressAmount: Int = 1) {
        Log.d(TAG, "Tracking progress for achievement: $achievementType, Progress amount: $progressAmount")
        val achievement = achievements.find { it.type == achievementType && !it.isUnlocked }

        achievement?.let {
            Log.d(TAG, "Found achievement: ${it.name}. Incrementing progress.")
            it.incrementProgress(progressAmount)

            if (it.checkProgress()) {
                Log.d(TAG, "Achievement progress completed: ${it.name}")
                unlockAchievement(player, it)
            }
        } ?: Log.d(TAG, "No unlocked achievements found for type: $achievementType")
    }


    // Unlock achievement and apply rewards
    private fun unlockAchievement(player: Player, achievement: Achievement) {
        Log.d(TAG, "Unlocking achievement: ${achievement.name}")

        achievement.unlockAchievement()

        // Apply rewards based on the reward type
        when (achievement.rewardType) {
            RewardType.POINTS -> {
                Log.d(TAG, "Awarding ${achievement.rewardValue} points to ${player.name}")
                player.addBonusPoints(achievement.rewardValue)
            }
            RewardType.POWER_UP -> {
                Log.d(TAG, "Awarding power-up to ${player.name}")
                applyPowerUpReward(player, achievement)
            }
            RewardType.UNLOCK_ITEM -> {
                Log.d(TAG, "Unlocking item for ${player.name}")
                unlockItemReward(player, achievement)
            }
            RewardType.BADGE -> {
                Log.d(TAG, "Granting badge to ${player.name}")
                grantBadge(player, achievement)
            }
            else -> Log.d(TAG, "Unknown reward type for achievement: ${achievement.name}")
        }

        // Notify the player
        notifyAchievementUnlocked(achievement)

        // Save the updated achievements
        saveAchievements()
    }

    // Apply a power-up reward
    private fun applyPowerUpReward(player: Player, achievement: Achievement) {
        Log.d(TAG, "Applying power-up reward for ${player.name}")
        // Logic to give the player a power-up
    }

    // Unlock an item (e.g., skin or custom reward)
    private fun unlockItemReward(player: Player, achievement: Achievement) {
        Log.d(TAG, "Unlocking custom item for ${player.name}")
        // Logic to unlock custom items
    }

    // Grant a badge to the player
    private fun grantBadge(player: Player, achievement: Achievement) {
        Log.d(TAG, "Granting badge for achievement: ${achievement.name}")
        // Logic to award a badge
    }

    // Notify the player when an achievement is unlocked
    private fun notifyAchievementUnlocked(achievement: Achievement) {
        Log.d(TAG, "Notifying player: Achievement unlocked - ${achievement.name}")
        // Example: Show a toast or UI notification
        // Toast.makeText(context, "${achievement.name} Unlocked!", Toast.LENGTH_SHORT).show()
    }

    // Get all available achievements (with progress)
    fun getAchievements(): List<Achievement> {
        Log.d(TAG, "Fetching all achievements")
        return achievements
    }

    // Save achievements to SharedPreferences or a database
    private fun saveAchievements() {
        Log.d(TAG, "Saving achievements to storage")
        DataUtils.saveAchievements(context, achievements)
    }

    // Load achievements from SharedPreferences or a database
    private fun loadAchievements() {
        Log.d(TAG, "Loading achievements from storage")
        achievements.clear()
        achievements.addAll(DataUtils.loadAchievements(context))
    }
}
