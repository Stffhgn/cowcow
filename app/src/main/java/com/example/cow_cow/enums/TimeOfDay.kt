package com.example.cow_cow.enums

enum class TimeOfDay {
    EARLY_MORNING,  // Before sunrise, typically 4 AM - 6 AM
    MORNING,        // Sunrise to late morning, typically 6 AM - 10 AM
    LATE_MORNING,   // Just before noon, typically 10 AM - 12 PM
    AFTERNOON,      // Noon to late afternoon, typically 12 PM - 4 PM
    LATE_AFTERNOON, // End of the afternoon, typically 4 PM - 6 PM
    EVENING,        // Sunset to early night, typically 6 PM - 9 PM
    NIGHT,          // Full night, typically 9 PM - Midnight
    LATE_NIGHT;     // Late night to early morning, typically 12 AM - 4 AM

    companion object {
        /**
         * Determines the time of day based on the provided hour.
         *
         * @param hour The current hour (0-23).
         * @return The corresponding TimeOfDay value.
         */
        fun getCurrentTimeOfDay(hour: Int): TimeOfDay {
            return when (hour) {
                in 4..5 -> EARLY_MORNING
                in 6..9 -> MORNING
                in 10..11 -> LATE_MORNING
                in 12..15 -> AFTERNOON
                in 16..17 -> LATE_AFTERNOON
                in 18..20 -> EVENING
                in 21..23 -> NIGHT
                else -> LATE_NIGHT
            }
        }
    }
}
