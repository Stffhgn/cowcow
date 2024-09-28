package com.example.cow_cow.models

enum class TimeOfDay {
    EARLY_MORNING,  // Before sunrise, typically 4 AM - 6 AM
    MORNING,        // Sunrise to late morning, typically 6 AM - 12 PM
    LATE_MORNING,   // Just before noon, typically 10 AM - 12 PM
    AFTERNOON,      // Noon to late afternoon, typically 12 PM - 4 PM
    LATE_AFTERNOON, // End of the afternoon, typically 4 PM - 6 PM
    EVENING,        // Sunset to early night, typically 6 PM - 9 PM
    NIGHT,          // Full night, typically 9 PM - Midnight
    LATE_NIGHT,     // Late night to early morning, typically 12 AM - 4 AM
}