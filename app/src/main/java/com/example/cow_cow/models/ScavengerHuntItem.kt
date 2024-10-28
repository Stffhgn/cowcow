package com.example.cow_cow.models

import com.example.cow_cow.enums.*

data class ScavengerHuntItem(
    val name: String,                          // Name of the scavenger hunt item
    val difficultyLevel: DifficultyLevel,      // Difficulty of the item
    val locationType: LocationType,            // Type of location where the item can be found
    val timeOfDay: TimeOfDay,                  // Best time of day for the hunt
    val ageGroup: AgeGroup,                    // Target age group
    val weather: WeatherCondition?,            // Applicable weather conditions (optional)
    val specialOccasion: SpecialOccasion?,     // Special occasions tied to the item (optional)
    val season: Season,                        // Season in which the item is relevant
    var isFound: Boolean = false,              // Status: has the item been found
    val tags: List<String> = emptyList(),      // Additional tags for flexibility (e.g., "rare", "holiday")
    val isPremium: Boolean = false,            // True if part of a premium pack
    val isFree: Boolean = true,                // True if free to access
    val isPurchased: Boolean = false           // True if part of a purchased data pack
) {
    // Calculate points based on the difficulty level and tags (e.g., bonus points for rare items)
    fun getPoints(): Int {
        val basePoints = when (difficultyLevel) {
            DifficultyLevel.EASY -> 5
            DifficultyLevel.MEDIUM -> 10
            DifficultyLevel.HARD -> 20
        }
        val tagBonus = if (tags.contains("rare")) 5 else 0
        return basePoints + tagBonus
    }

    // Check if the item is available based on the player's account status
    fun isAvailableToPlayer(isPremiumAccount: Boolean): Boolean {
        return isFree || isPremiumAccount || isPurchased
    }

    // Display item description based on properties for a better user experience
    fun getDescription(): String {
        val weatherInfo = weather?.let { "Best in $it weather" } ?: "Weather-independent"
        val occasionInfo = specialOccasion?.let { "Great for $it" } ?: "Perfect any time"
        return "$name - Difficulty: $difficultyLevel, $locationType, $timeOfDay. $weatherInfo. $occasionInfo."
    }
}
