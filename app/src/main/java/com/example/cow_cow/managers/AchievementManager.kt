package com.example.cow_cow.managers

import android.content.Context
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType
import com.example.cow_cow.utils.DataUtils

class AchievementManager(private val context: Context) {

    private val achievements = mutableListOf<Achievement>() // Local list of achievements

    init {
        loadAchievements()  // Load achievements when the manager is initialized
    }

    // Track progress for progressive achievements
    fun trackProgress(player: Player, achievementType: AchievementType, progressAmount: Int = 1) {
        val achievement = achievements.find { it.type == achievementType && !it.isUnlocked }
        achievement?.let {
            it.incrementProgress(progressAmount)
            if (it.checkProgress()) {
                unlockAchievement(player, it)
            }
        }
    }

    // Unlock achievement and apply rewards
    private fun unlockAchievement(player: Player, achievement: Achievement) {
        achievement.unlockAchievement()

        // Apply rewards based on the reward type
        when (achievement.rewardType) {
            RewardType.POINTS -> player.addBonusPoints(achievement.rewardValue)
            RewardType.POWER_UP -> applyPowerUpReward(player, achievement)
            RewardType.UNLOCK_ITEM -> unlockItemReward(player, achievement)
            RewardType.BADGE -> grantBadge(player, achievement)
        }

        // Notify the player
        notifyAchievementUnlocked(achievement)

        // Save the updated achievements
        saveAchievements()
    }

    // Apply a power-up reward
    private fun applyPowerUpReward(player: Player, achievement: Achievement) {
        // Logic to give the player a power-up
        // Example: player.activePowerUps.add(powerUp)
    }

    // Unlock an item (e.g., skin or custom reward)
    private fun unlockItemReward(player: Player, achievement: Achievement) {
        // Logic to unlock custom items
    }

    // Grant a badge to the player
    private fun grantBadge(player: Player, achievement: Achievement) {
        // Logic to award a badge
    }

    // Notify the player when an achievement is unlocked
    private fun notifyAchievementUnlocked(achievement: Achievement) {
        // Example: Show a toast or UI notification
        // Toast.makeText(context, "${achievement.name} Unlocked!", Toast.LENGTH_SHORT).show()
    }

    // Get all available achievements (with progress)
    fun getAchievements(): List<Achievement> {
        return achievements
    }

    // Save achievements to SharedPreferences or a database
    private fun saveAchievements() {
        DataUtils.saveAchievements(context, achievements)
    }

    // Load achievements from SharedPreferences or a database
    private fun loadAchievements() {
        achievements.clear()
        achievements.addAll(DataUtils.loadAchievements(context))
    }
}
