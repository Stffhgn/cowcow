package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType
import com.example.cow_cow.utils.DataUtils
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.managers.PowerUpManager

class AchievementManager(private val context: Context) {
    companion object {
        private const val TAG = "AchievementManager"

        /**
         * Track progress for progressive achievements.
         *
         * @param player The player whose progress is being tracked.
         * @param achievementType The type of the achievement.
         * @param progressAmount The amount to increase the progress by.
         */
        fun trackProgress(player: Player, achievementType: AchievementType, progressAmount: Int = 1) {
            Log.d(TAG, "Tracking progress for achievement: $achievementType, Progress amount: $progressAmount")
            val achievement = player.achievements.find { it.type == achievementType && !it.isUnlocked }

            achievement?.let {
                Log.d(TAG, "Found achievement: ${it.name}. Incrementing progress.")
                it.incrementProgress(progressAmount)

                if (it.checkProgress()) {
                    Log.d(TAG, "Achievement progress completed: ${it.name}")
                    unlockAchievement(player, it)
                }
            } ?: Log.d(TAG, "No unlocked achievements found for type: $achievementType")
        }

        /**
         * Unlocks a specific achievement for the player.
         *
         * @param player The player who has earned the achievement.
         * @param achievement The achievement to unlock.
         */
        fun unlockAchievement(player: Player, achievement: Achievement) {
            achievement.isUnlocked = true
            Log.d(TAG, "Player ${player.name} unlocked achievement: ${achievement.name}")
            // Additional logic for handling the unlocked achievement,
            // such as displaying a notification or updating the UI.
        }

        // Other companion methods...
    }

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
        achievements.forEach { achievement ->
            if (!achievement.isUnlocked && achievement.checkProgress()) {
                Log.d(TAG, "Achievement unlocked: ${achievement.name}")
                unlockAchievement(player, achievement)
            }
        }
    }

    /**
     * Check if the player already has an achievement of a certain type.
     *
     * @param player The player to check.
     * @param achievementType The type of the achievement to check for.
     * @return True if the player has already unlocked this achievement, false otherwise.
     */
    fun hasAchievement(player: Player, achievementType: AchievementType): Boolean {
        val hasAchievement = player.achievements.any { it.type == achievementType && it.isUnlocked }
        Log.d(TAG, "Player ${player.name} has achievement $achievementType: $hasAchievement")
        return hasAchievement
    }

    /**
     * Track progress for progressive achievements.
     *
     * @param player The player whose progress is being tracked.
     * @param achievementType The type of the achievement.
     * @param progressAmount The amount to increase the progress by.
     */
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

    /**
     * Unlock achievement and apply rewards.
     *
     * @param player The player receiving the achievement.
     * @param achievement The achievement to unlock.
     */
    fun unlockAchievement(player: Player, achievement: Achievement) {
        Log.d(TAG, "Unlocking achievement: ${achievement.name}")

        achievement.unlockAchievement()
        player.achievements.add(achievement)

        // Apply rewards based on the reward type
        when (achievement.rewardType) {
            RewardType.POINTS -> {
                Log.d(TAG, "Awarding ${achievement.rewardValue} points to ${player.name}")

                // Add the reward value as bonus points to the player
                player.bonusPoints += achievement.rewardValue

                // Recalculate the player's total score using the ScoreManager
                val updatedScore = ScoreManager.calculatePlayerScore(player)
                Log.d(TAG, "New score for ${player.name} after applying reward: $updatedScore")
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

    /**
     * Grants a random power-up to a player as a reward for achieving a specific milestone.
     *
     * @param player The player who will receive the power-up reward.
     * @param achievement The achievement that triggered the power-up reward, which may influence its effect.
     */
    private fun applyPowerUpReward(player: Player, achievement: Achievement) {
        Log.d(TAG, "Applying power-up reward for ${player.name}")

        // Select a random power-up type from the available options.
        val randomPowerUpType = PowerUpType.values().random()

        // Set the duration of the power-up to 60 minutes (in milliseconds).
        val duration = 60 * 60 * 1000L

        // Activate the randomly selected power-up for the player with the specified effect value.
        PowerUpManager.activatePowerUp(
            player = player,
            powerUpType = randomPowerUpType,
            duration = duration,
            effectValue = achievement.rewardValue
        )

        Log.d(TAG, "Granted random power-up: $randomPowerUpType to player ${player.name}")
    }

    /**
     * Unlock an item reward for the player.
     *
     * @param player The player receiving the unlocked item.
     * @param achievement The achievement associated with the reward.
     */
    private fun unlockItemReward(player: Player, achievement: Achievement) {
        Log.d(TAG, "Unlocking custom item for ${player.name}")
        // Logic to unlock custom items can go here, such as adding the item to the player's inventory.
    }

    /**
     * Grant a badge to the player.
     *
     * @param player The player receiving the badge.
     * @param achievement The achievement associated with the badge.
     */
    private fun grantBadge(player: Player, achievement: Achievement) {
        Log.d(TAG, "Granting badge for achievement: ${achievement.name}")
        // Logic to award a badge, e.g., adding to the player's badge collection.
    }

    /**
     * Notify the player when an achievement is unlocked.
     *
     * @param achievement The achievement that was unlocked.
     */
    private fun notifyAchievementUnlocked(achievement: Achievement) {
        Log.d(TAG, "Notifying player: Achievement unlocked - ${achievement.name}")
        // Example: Show a toast or UI notification
        // Toast.makeText(context, "${achievement.name} Unlocked!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Get all available achievements (with progress).
     *
     * @return A list of all achievements.
     */
    fun getAchievements(): List<Achievement> {
        Log.d(TAG, "Fetching all achievements")
        return achievements
    }

    /**
     * Handle special achievements that are event-driven (e.g., holiday events, high scores, or custom milestones).
     *
     * This method checks the provided event identifier and determines if a special achievement should be unlocked.
     * It allows for dynamic handling of unique achievements, such as those tied to seasonal events or player milestones.
     *
     * @param player The player who might receive the special achievement.
     * @param eventIdentifier A string or identifier describing the event (e.g., "Holiday_2024", "HighScore_Milestone").
     */
    fun handleSpecialAchievement(player: Player, eventIdentifier: String) {
        try {
            Log.d(TAG, "Processing special achievement for event: $eventIdentifier")

            // Define achievements or events that could trigger special achievements.
            val specialAchievements = mapOf(
                "Holiday_2024" to AchievementType.SPECIAL_EVENT,
                "HighScore_Milestone" to AchievementType.POINT_MILESTONE,
                "First100Cows" to AchievementType.ITEM_MASTER,
                "WinterChallenge" to AchievementType.SPECIAL_EVENT,
                "Speedrun_2024" to AchievementType.SPEEDRUN,
                "DailyLogin30Days" to AchievementType.DEDICATION,
                "ScavengerHunt2024" to AchievementType.SCAVENGER_HUNT,
                "PowerUpMastery" to AchievementType.POWER_UP_MASTER
                // Add more mappings as needed.
            )

            // Check if the eventIdentifier corresponds to a known special achievement.
            specialAchievements[eventIdentifier]?.let { achievementType ->
                // Check if the player already has this achievement unlocked.
                if (!hasAchievement(player, achievementType)) {
                    Log.d(TAG, "Unlocking special achievement: $achievementType for player: ${player.name}")

                    // Create a new Achievement instance or retrieve the existing one.
                    val achievement = getAchievementByType(achievementType) ?: return

                    // Unlock the achievement.
                    unlockAchievement(player, achievement)
                } else {
                    Log.d(TAG, "Player ${player.name} already has the achievement: $achievementType")
                }
            } ?: Log.d(TAG, "No special achievement found for event: $eventIdentifier")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to handle special achievement for event: $eventIdentifier. Error: ${e.message}", e)
        }
    }

    /**
     * Retrieves an achievement of the specified type for a player.
     *
     * @param achievementType The type of the achievement to retrieve.
     * @return The achievement of the specified type, or null if not found.
     */
    fun getAchievementByType(achievementType: AchievementType): Achievement? {
        Log.d(TAG, "Fetching achievement by type: $achievementType")

        // Look for the achievement in the list of all achievements.
        return achievements.find { it.type == achievementType }
    }


    /**
     * Save achievements to SharedPreferences or a database.
     */
    private fun saveAchievements() {
        Log.d(TAG, "Saving achievements to storage")
        DataUtils.saveAchievements(context, achievements)
    }

    /**
     * Handle scoring achievements when a player reaches a specific score threshold.
     *
     * This method checks the player's score and awards achievements based on predefined score thresholds.
     * It helps in unlocking milestones such as reaching 100, 500, 1000 points, etc.
     *
     * @param player The player whose score is being checked.
     * @param scoreThreshold The score threshold that triggers potential achievement unlocks.
     */
    fun handleScoringAchievement(player: Player, scoreThreshold: Int) {
        try {
            Log.d(TAG, "Handling scoring achievement for player: ${player.name} with threshold: $scoreThreshold")

            // Define score thresholds and their corresponding achievement types.
            val scoreAchievements = mapOf(
                100 to AchievementType.POINT_MILESTONE,
                500 to AchievementType.POINT_MILESTONE,
                1000 to AchievementType.SCORING,
                2000 to AchievementType.SCORING,
                5000 to AchievementType.SCORING
                // Add more thresholds and achievement types as needed.
            )

            // Check if the player's score matches or surpasses a threshold.
            scoreAchievements.forEach { (threshold, achievementType) ->
                if (player.basePoints >= threshold && !hasAchievement(player, achievementType)) {
                    Log.d(TAG, "Player ${player.name} reached score threshold: $threshold")

                    // Get the achievement by its type or create a new one.
                    val achievement = getAchievementByType(achievementType) ?: return

                    // Unlock the achievement.
                    unlockAchievement(player, achievement)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to handle scoring achievement for player: ${player.name}. Error: ${e.message}", e)
        }
    }


    /**
     * Load achievements from SharedPreferences or a database.
     */
    private fun loadAchievements() {
        Log.d(TAG, "Loading achievements from storage")
        achievements.clear()
        achievements.addAll(DataUtils.loadAchievements(context))
    }
}
