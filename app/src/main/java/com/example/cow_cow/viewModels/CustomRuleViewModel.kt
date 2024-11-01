package com.example.cow_cow.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.managers.CustomRuleManager
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.CustomRuleRepository

class CustomRuleViewModel(
    private val customRuleRepository: CustomRuleRepository,
    private val customRuleManager: CustomRuleManager
) : ViewModel() {

    private val TAG = "CustomRuleViewModel"

    // LiveData to hold the list of custom rules for a player
    private val _customRules = MutableLiveData<List<CustomRule>>()
    val customRules: LiveData<List<CustomRule>> get() = _customRules

    // LiveData to track the active custom rules currently affecting the player
    private val _activeRules = MutableLiveData<List<CustomRule>>()
    val activeRules: LiveData<List<CustomRule>> get() = _activeRules

    /**
     * Load custom rules for a specific player.
     * Fetches rules from the repository and updates the LiveData.
     *
     * @param player The player whose rules are being loaded.
     */
    fun loadCustomRules(player: Player) {
        Log.d(TAG, "Loading custom rules for player: ${player.name}")

        try {
            // Fetch the custom rules from the repository for the specific player
            val rules = customRuleRepository.getCustomRulesForPlayer(player.id)

            // Update the LiveData with the retrieved rules
            _customRules.value = rules

            Log.d(TAG, "Successfully loaded ${rules.size} custom rules for player: ${player.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading custom rules for player ${player.name}: ${e.message}")
        }
    }

    /**
     * Apply a custom rule to a player and save it to the repository.
     *
     * @param player The player to whom the rule is applied.
     * @param rule The rule to be applied.
     */
    fun applyCustomRule(player: Player, rule: CustomRule) {
        Log.d(TAG, "Applying custom rule: ${rule.ruleName} to player: ${player.name}")

        player.customRules.add(rule)
        customRuleRepository.saveCustomRules(listOf(rule))
        updateActiveRules(player)

        Log.d(TAG, "Custom rule applied and saved for player: ${player.name}")
    }

    /**
     * Remove a custom rule from a player and update the repository.
     *
     * @param player The player from whom the rule is removed.
     * @param rule The rule to be removed.
     */
    fun removeCustomRule(player: Player, rule: CustomRule) {
        Log.d(TAG, "Removing custom rule: ${rule.ruleName} from player: ${player.name}")

        player.customRules.remove(rule)
        customRuleRepository.saveCustomRules(player.customRules)
        updateActiveRules(player)

        Log.d(TAG, "Custom rule removed and repository updated for player: ${player.name}")
    }

    /**
     * Update the list of active custom rules for the player based on current conditions.
     *
     * @param player The player whose active rules are being updated.
     */
    fun updateActiveRules(player: Player) {
        Log.d(TAG, "Updating active custom rules for player: ${player.name}")

        // Filter the active rules based on conditions being met
        val activeRules = player.customRules.filter { rule ->
            checkRuleCondition(player, rule)
        }

        _activeRules.value = activeRules

        Log.d(TAG, "Active rules updated. ${activeRules.size} active rules for player: ${player.name}")
    }

    /**
     * Check whether a custom rule's conditions are met for a player.
     *
     * @param player The player whose points are being checked.
     * @param rule The custom rule whose conditions need to be validated.
     * @return True if the conditions are met, otherwise false.
     */
    private fun checkRuleCondition(player: Player, rule: CustomRule): Boolean {
        return when (rule.conditionType) {
            RuleConditionType.ALWAYS -> true
            RuleConditionType.PLAYER_HAS_LESS_THAN_X_POINTS -> player.basePoints < rule.conditionValue
            RuleConditionType.PLAYER_HAS_MORE_THAN_X_POINTS -> player.basePoints > rule.conditionValue
            else -> {
                Log.d(TAG, "No specific condition found for rule: ${rule.ruleName}")
                false
            }
        }
    }

    /**
     * Apply a rule directly to the player.
     *
     * @param player The player to whom the rule is applied.
     */
    fun applyRuleToPlayer(player: Player) {
        Log.d(TAG, "Applying rule to player: ${player.name}")

        player.customRule?.let { rule ->
            customRuleManager.applyCustomRule(player, rule)
            Log.d(TAG, "Applied rule: ${rule.ruleName} to player: ${player.name}")
        } ?: Log.d(TAG, "No custom rule found for player: ${player.name}")
    }

    /**
     * Reset all custom rules for a player, clearing the current active rules.
     *
     * @param player The player whose rules are being reset.
     */
    fun resetCustomRules(player: Player) {
        Log.d(TAG, "Resetting custom rules for player: ${player.name}")

        player.customRules.clear()
        customRuleRepository.saveCustomRules(emptyList())
        _activeRules.value = emptyList()

        Log.d(TAG, "Custom rules reset for player: ${player.name}")
    }
}
