package com.example.cow_cow.models

import com.example.cow_cow.enums.DifficultyLevel

data class ScavengerHuntItem(
    val name: String,
    val difficultyLevel: DifficultyLevel,
    var isFound: Boolean = false,
    var isActive: Boolean = false,
    val tags: List<String> = emptyList()
) {
    // Points are calculated based on difficulty level and tags
    var points: Int = 0
        get() {
            val basePoints = when (difficultyLevel) {
                DifficultyLevel.EASY -> 2
                DifficultyLevel.MEDIUM -> 4
                DifficultyLevel.HARD -> 6
            }
            val tagBonus = if (tags.contains("rare")) 5 else 0
            return basePoints + tagBonus
        }

    // Convenience flags for checking difficulty level
    val isEasy: Boolean
        get() = difficultyLevel == DifficultyLevel.EASY

    val isMedium: Boolean
        get() = difficultyLevel == DifficultyLevel.MEDIUM

    val isHard: Boolean
        get() = difficultyLevel == DifficultyLevel.HARD

    fun getDescription(): String {
        return "$name - Difficulty: $difficultyLevel"
    }
}
