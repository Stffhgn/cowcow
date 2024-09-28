package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.CustomRuleRepository

class CustomRuleViewModel(
    private val customRuleRepository: CustomRuleRepository
) : ViewModel() {

    // LiveData to hold the list of custom rules for a player
    private val _customRules = MutableLiveData<List<CustomRule>>()
    val customRules: LiveData<List<CustomRule>> get() = _customRules

    // LiveData to track the active custom rules currently affecting the player
    private val _activeRules = MutableLiveData<List<CustomRule>>()
    val activeRules: LiveData<List<CustomRule>> get() = _activeRules

    /**
     * Load custom rules for a specific player.
     */
    fun loadCustomRules(player: Player) {
        val rules = customRuleRepository.getCustomRulesForPlayer(player)
        _customRules.value = rules
    }

    /**
     * Apply a custom rule to a player.
     */
    fun applyCustomRule(player: Player, rule: CustomRule) {
        player.applyCustomRule(rule)
        customRuleRepository.saveCustomRules(player, listOf(rule))
        updateActiveRules(player)
    }

    /**
     * Remove a custom rule from a player.
     */
    fun removeCustomRule(player: Player, rule: CustomRule) {
        player.customRules.remove(rule)
        customRuleRepository.saveCustomRules(player, player.customRules)
        updateActiveRules(player)
    }

    /**
     * Check and update the active custom rules for a player.
     */
    fun updateActiveRules(player: Player) {
        val activeRules = player.customRules.filter { rule ->
            // Check if the rule conditions are met (e.g., Player has less than X points)
            checkRuleCondition(player, rule)
        }
        _activeRules.value = activeRules
    }

    /**
     * Check whether a custom rule's conditions are met for a player.
     */
    private fun checkRuleCondition(player: Player, rule: CustomRule): Boolean {
        return when (rule.conditionType) {
            RuleConditionType.ALWAYS -> true
            RuleConditionType.PLAYER_HAS_LESS_THAN_X_POINTS -> player.basePoints < rule.conditionValue
            RuleConditionType.PLAYER_HAS_MORE_THAN_X_POINTS -> player.basePoints > rule.conditionValue
            // Additional conditions can be added here
        }
    }

    /**
     * Reset all custom rules for a player (for testing or restarting).
     */
    fun resetCustomRules(player: Player) {
        player.customRules.clear()
        customRuleRepository.saveCustomRules(player, player.customRules)
        _activeRules.value = emptyList()
    }
}
