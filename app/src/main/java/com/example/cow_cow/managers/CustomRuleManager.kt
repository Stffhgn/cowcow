package com.example.cow_cow.managers

import android.content.Context
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.enums.RuleEffectType
import com.example.cow_cow.utils.DataUtils

class CustomRuleManager(private val context: Context) {

    private val activeCustomRules = mutableListOf<CustomRule>()  // Stores active custom rules

    init {
        loadCustomRules()  // Load any saved custom rules when initialized
    }

    // Creates and adds a new custom rule
    fun createCustomRule(
        ruleId: Int,
        name: String,
        description: String,
        effect: RuleEffectType,
        value: Int,
        duration: Long,
        conditionType: RuleConditionType,
        conditionValue: Int
    ) {
        val customRule = CustomRule(
            ruleId = ruleId,
            name = name,
            description = description,
            effect = effect,
            value = value,
            duration = duration,
            conditionType = conditionType,
            conditionValue = conditionValue
        )
        activeCustomRules.add(customRule)
        saveCustomRules()  // Save rules after creation
    }

    // Applies a custom rule to a specific player
    fun applyCustomRule(player: Player, rule: CustomRule) {
        if (checkRuleCondition(player, rule)) {
            when (rule.effect) {
                RuleEffectType.ADD_POINTS -> player.addBasePoints(rule.value)
                RuleEffectType.DEDUCT_POINTS -> player.basePoints -= rule.value
                RuleEffectType.SILENCE_PLAYER -> player.isSilenced = true
                RuleEffectType.CUSTOM_PENALTY -> applyCustomPenalty(player, rule)
            }

            // Notify the player about the applied rule (could use a notification system)
            notifyRuleApplied(player, rule)
        }
    }

    // Check if the rule's condition is satisfied before applying the rule
    private fun checkRuleCondition(player: Player, rule: CustomRule): Boolean {
        return when (rule.conditionType) {
            RuleConditionType.ALWAYS -> true
            RuleConditionType.PLAYER_HAS_LESS_THAN_X_POINTS -> player.basePoints < rule.conditionValue
            RuleConditionType.PLAYER_HAS_MORE_THAN_X_POINTS -> player.basePoints > rule.conditionValue
        }
    }

    // Check active rules and remove expired ones
    fun checkRules() {
        val currentTime = System.currentTimeMillis()
        activeCustomRules.removeAll { rule ->
            rule.duration > 0 && (currentTime - rule.duration) >= rule.duration
        }
        saveCustomRules()  // Save the updated list after checking for expired rules
    }

    // Remove a custom rule manually
    fun removeCustomRule(rule: CustomRule) {
        activeCustomRules.remove(rule)
        saveCustomRules()  // Save after removal
    }

    // Notify the player when a rule is applied
    private fun notifyRuleApplied(player: Player, rule: CustomRule) {
        // Example: Show a notification or alert dialog
        // "Custom rule '${rule.name}' applied to ${player.name}"
    }

    // Custom logic for specific penalties
    private fun applyCustomPenalty(player: Player, rule: CustomRule) {
        // Handle any custom penalties specific to the rule
        // Example: Reducing player's speed, freezing player, etc.
    }

    // Save the current list of custom rules
    private fun saveCustomRules() {
        DataUtils.saveCustomRules(context, activeCustomRules)
    }

    // Load the saved custom rules
    private fun loadCustomRules() {
        activeCustomRules.clear()
        activeCustomRules.addAll(DataUtils.loadCustomRules(context))
    }
}
