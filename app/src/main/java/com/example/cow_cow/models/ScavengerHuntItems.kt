package com.example.cow_cow.models



data class ScavengerHuntItem(
    val name: String,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.MEDIUM, // Default difficulty level
    var isFound: Boolean = false
) {

    // Calculate points based on difficulty level
    fun getPoints(): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> 5  // Easy items get 5 points
            DifficultyLevel.MEDIUM -> 10  // Medium items get 10 points
            DifficultyLevel.HARD -> 20  // Hard items get 20 points
        }
    }
}
enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD
}

