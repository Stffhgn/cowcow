package com.example.cow_cow.models

import android.util.Log
import com.example.cow_cow.enums.ConditionType

data class Condition(
    val conditionType: ConditionType, // The type of condition (e.g., score threshold)
    val value: Int, // The value or threshold for the condition (e.g., 1000 points)
    val description: String // A user-friendly description of the condition
) {
    // Define logic for checking if the condition is met
    fun isMet(player: Player): Boolean {
        return when (conditionType) {
            ConditionType.TIME_LIMIT -> {
                val result = player.timeSpent <= value
                Log.d("Condition", "Checking TIME_LIMIT: Player timeSpent = ${player.timeSpent}, required <= $value. Result: $result")
                result
            }
            ConditionType.WIN_STREAK -> {
                val result = player.winStreak >= value
                Log.d("Condition", "Checking WIN_STREAK: Player winStreak = ${player.winStreak}, required >= $value. Result: $result")
                result
            }
            ConditionType.OBJECTIVES_COMPLETED -> {
                val result = player.objectivesCompleted >= value
                Log.d("Condition", "Checking OBJECTIVES_COMPLETED: Player objectivesCompleted = ${player.objectivesCompleted}, required >= $value. Result: $result")
                result
            }
            else -> {
                Log.e("Condition", "Unknown condition type: $conditionType")
                false // Return false if condition type is not recognized
            }
        }
    }
}
