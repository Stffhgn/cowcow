package com.example.cow_cow.enums

enum class RuleConditionType {
    ALWAYS,                          // Always active
    PLAYER_HAS_LESS_THAN_X_POINTS,    // Condition based on player's points being less than X
    PLAYER_HAS_MORE_THAN_X_POINTS,    // Condition based on player's points being more than X
    TIME_BASED,                       // Active for a certain time period
    SPECIAL_EVENT_ACTIVE,             // Active during special events (e.g., holidays)
    TEAM_BASED                        // Applies only when playing in a team
}
