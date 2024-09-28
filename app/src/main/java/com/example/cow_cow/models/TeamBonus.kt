package com.example.cow_cow.models

import com.example.cow_cow.enums.TeamBonusType

data class TeamBonus(
    val type: TeamBonusType,      // Type of bonus (e.g., extra points, speed boost)
    val effectValue: Int,         // The value of the bonus (e.g., +50 points)
    var isActive: Boolean = true, // Whether the bonus is currently active
    val duration: Long = 0L       // Duration of the bonus (optional)
)