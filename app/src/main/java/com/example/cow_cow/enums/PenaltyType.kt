package com.example.cow_cow.enums

enum class PenaltyType {
    /**
     * Deducts points from the player as a penalty.
     */
    POINT_DEDUCTION,

    /**
     * Silences the player, preventing them from making calls for a specified duration.
     */
    SILENCED,

    /**
     * Temporarily bans the player from participating for a specified duration.
     */
    TEMPORARY_BAN,

    /**
     * Handles penalties for making a false call (e.g., calling a cow incorrectly).
     */
    FALSE_CALL,

    /**
     * Applies a time penalty, reducing the player's allowed time in a timed game mode.
     */
    TIME_PENALTY,

    /**
     * Used for any other custom penalties that don't fall under the standard categories.
     */
    OTHER
}
