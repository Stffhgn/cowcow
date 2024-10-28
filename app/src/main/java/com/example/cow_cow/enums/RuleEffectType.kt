package com.example.cow_cow.enums

enum class RuleEffectType {
    ADD_POINTS,              // Add points to the player
    DEDUCT_POINTS,           // Deduct points from the player
    SILENCE_PLAYER,          // Prevent a player from making calls (for a time)
    CUSTOM_PENALTY,          // Apply a custom penalty (e.g., cannot participate for 1 round)
    DOUBLE_POINTS,           // Double the points gained for a period
    POWER_UP_REWARD,         // Double points for now

}
