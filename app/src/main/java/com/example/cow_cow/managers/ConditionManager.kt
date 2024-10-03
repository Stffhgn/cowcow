package com.example.cow_cow.managers

import com.example.cow_cow.enums.ConditionType
import com.example.cow_cow.models.Condition
import com.example.cow_cow.models.Player

object ConditionManager {

    /**
     * Evaluates if a condition has been met.
     *
     * @param condition The condition to evaluate.
     * @param player The player whose data is used to evaluate the condition.
     * @return True if the condition is met, false otherwise.
     */
    fun evaluateCondition(condition: Condition, player: Player): Boolean {
        return when (condition.conditionType) {
            ConditionType.SCORE_THRESHOLD -> player.basePoints >= condition.value
            ConditionType.TIME_LIMIT -> player.timeSpent <= condition.value // Example: Time-based condition
            ConditionType.WIN_STREAK -> player.winStreak >= condition.value
            ConditionType.OBJECTIVES_COMPLETED -> player.objectivesCompleted >= condition.value
        }
    }

    /**
     * Check if all conditions for a list are met.
     */
    fun evaluateAllConditions(conditions: List<Condition>, player: Player): Boolean {
        return conditions.all { condition -> evaluateCondition(condition, player) }
    }
}