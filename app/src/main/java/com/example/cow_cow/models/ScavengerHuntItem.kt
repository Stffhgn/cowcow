package com.example.cow_cow.models

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.LocationType
import com.example.cow_cow.enums.TimeOfDay
import com.example.cow_cow.enums.AgeGroup
import com.example.cow_cow.enums.WeatherCondition
import com.example.cow_cow.enums.SpecialOccasion
import com.example.cow_cow.enums.Season

data class ScavengerHuntItem(
    val name: String,                          // Name of the scavenger hunt item
    val difficultyLevel: DifficultyLevel,      // Difficulty of the item
    val locationType: LocationType,            // Type of location where the item can be found
    val timeOfDay: TimeOfDay,                  // Best time of day for the hunt
    val ageGroup: AgeGroup,                    // Target age group
    val weather: WeatherCondition?,            // Applicable weather conditions
    val specialOccasion: SpecialOccasion?,     // Special occasions tied to the item
    val season: Season,                        // Season in which the item is relevant
    var isFound: Boolean = false,              // Status: has the item been found
    val tags: List<String> = emptyList()       // Additional tags for flexibility (e.g., "rare", "holiday", etc.)
) {
    // Calculate points based on the difficulty level
    fun getPoints(): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> 5
            DifficultyLevel.MEDIUM -> 10
            DifficultyLevel.HARD -> 20
        }
    }
}
