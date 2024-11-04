package com.example.cow_cow.enums

import androidx.annotation.ColorRes
import com.example.cow_cow.R

enum class TriviaCategory(
    val displayName: String,
    @ColorRes val colorResId: Int // Store color as a resource ID
) {
    GEOGRAPHY("Geography", R.color.geography_blue),          // Blue
    ENTERTAINMENT("Entertainment", R.color.entertainment_pink),   // Pink
    HISTORY("History", R.color.history_yellow),               // Yellow
    ARTS("Arts & Literature", R.color.arts_brown),            // Brown
    SCIENCE("Science & Nature", R.color.science_green),       // Green
    SPORTS("Sports & Leisure", R.color.sports_orange);        // Orange

    companion object {
        fun fromString(name: String): TriviaCategory? {
            return values().find {
                it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true)
            }
        }

        fun getRandomCategory(): TriviaCategory = values().random()
    }
}
