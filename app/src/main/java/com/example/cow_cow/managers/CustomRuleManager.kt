package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.enums.RuleEffectType
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils

class CustomRuleManager(private val context: Context) {

    private val activeCustomRules = mutableListOf<CustomRule>()  // Stores active custom rules

    init {
        loadCustomRules()  // Load saved custom rules when initialized
    }

    // Creates and adds a new custom rule
    fun createCustomRule(
        ruleId: String,
        playerId: String? = null,
        ruleName: String,
        ruleDescription: String,
        ruleEffect: RuleEffectType,
        value: Int,
        duration: Long,
        conditionType: RuleConditionType,
        conditionValue: Int
    ) {
        val customRule = CustomRule(
            ruleId = ruleId,
            playerId = playerId,
            ruleName = ruleName,
            ruleDescription = ruleDescription,
            ruleEffect = ruleEffect,
            value = value,
            duration = duration,
            conditionType = conditionType,
            conditionValue = conditionValue
        )
        activeCustomRules.add(customRule)
        Log.d("CustomRuleManager", "Created new rule: $ruleName with effect $ruleEffect.")
        saveCustomRules()  // Save rules after creation
    }

    // Apply custom rules for all players in the current game
    fun applyCustomRulesForGame(players: List<Player>, gameMode: GameMode) {
        Log.d("CustomRuleManager", "Applying custom rules for game mode: $gameMode")
        players.forEach { player ->
            val applicableRules = activeCustomRules.filter { it.playerId == null || it.playerId == player.id }
            applicableRules.forEach { rule ->
                applyCustomRule(player, rule)
            }
        }
    }

    // Function to apply a custom rule to a specific player
    fun applyCustomRule(player: Player, rule: CustomRule) {
        if (!checkRuleCondition(player, rule)) {
            Log.d("CustomRuleManager", "Rule ${rule.ruleName} condition not met for player ${player.name}")
            return
        }
        // Apply the rule effect
        when (rule.ruleEffect) {
            RuleEffectType.ADD_POINTS -> player.addBasePoints(rule.value)
            RuleEffectType.DEDUCT_POINTS -> player.basePoints -= rule.value
            RuleEffectType.SILENCE_PLAYER -> player.isSilenced = true
            RuleEffectType.CUSTOM_PENALTY -> applyCustomPenalty(player, rule)
            RuleEffectType.DOUBLE_POINTS -> player.addBasePoints(rule.value * 2)
        }
        Log.d("CustomRuleManager", "Applied rule '${rule.ruleName}' to player ${player.name}")
        PlayerManager.updatePlayer(player) // Optionally update player state
    }

    // Check if the rule's condition is satisfied before applying it
    private fun checkRuleCondition(player: Player, rule: CustomRule): Boolean {
        return when (rule.conditionType) {
            RuleConditionType.ALWAYS -> true
            RuleConditionType.PLAYER_HAS_LESS_THAN_X_POINTS -> player.basePoints < rule.conditionValue
            RuleConditionType.PLAYER_HAS_MORE_THAN_X_POINTS -> player.basePoints > rule.conditionValue
        }.also { result ->
            Log.d("CustomRuleManager", "Rule ${rule.ruleName} condition check result: $result for player ${player.name}")
        }
    }

    // Custom logic for specific penalties
    private fun applyCustomPenalty(player: Player, rule: CustomRule) {
        player.penaltyPoints += rule.value
        Log.d("CustomRuleManager", "Applied custom penalty of ${rule.value} points to player ${player.name}")
    }

    // Check active rules and remove expired ones
    fun checkRules() {
        val currentTime = System.currentTimeMillis()
        activeCustomRules.removeAll { rule ->
            rule.duration > 0 && (currentTime - rule.duration) >= rule.duration
        }
        saveCustomRules()  // Save updated rules after removing expired ones
        Log.d("CustomRuleManager", "Checked and removed expired rules.")
    }

    // Remove a custom rule manually
    fun removeCustomRule(rule: CustomRule) {
        activeCustomRules.remove(rule)
        saveCustomRules()  // Save after removal
        Log.d("CustomRuleManager", "Removed custom rule ${rule.ruleName}")
    }

    // Save the current list of custom rules
    private fun saveCustomRules() {
        DataUtils.saveCustomRules(context, activeCustomRules)
        Log.d("CustomRuleManager", "Saved custom rules.")
    }

    // Load the saved custom rules
    private fun loadCustomRules() {
        activeCustomRules.clear()
        activeCustomRules.addAll(DataUtils.loadCustomRules(context))
        Log.d("CustomRuleManager", "Loaded custom rules from storage.")
    }

    companion object {
        // Static method for applying custom rules (optional if needed elsewhere)
        fun applyCustomRule(player: Player, customRule: CustomRule) {
            // Implementation if needed statically
        }
    }
}
