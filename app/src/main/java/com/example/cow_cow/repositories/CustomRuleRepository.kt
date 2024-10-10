package com.example.cow_cow.repositories

import android.content.Context
import android.util.Log
import com.example.cow_cow.models.CustomRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CustomRuleRepository(private val context: Context) {

    private val TAG = "CustomRuleRepository"
    private val gson = Gson()

    // SharedPreferences for storing custom rules
    private val CUSTOM_RULES_PREFS = "custom_rules_prefs"
    private val CUSTOM_RULES_KEY = "custom_rules_key"

    /**
     * Load all custom rules from SharedPreferences.
     *
     * @return A list of custom rules.
     */
    fun loadCustomRules(): List<CustomRule> {
        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(CUSTOM_RULES_KEY, null)

        return if (json != null) {
            try {
                val type = object : TypeToken<List<CustomRule>>() {}.type
                val customRules: List<CustomRule> = gson.fromJson(json, type)
                Log.d(TAG, "Loaded ${customRules.size} custom rules.")
                customRules
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom rules: ${e.message}")
                emptyList()
            }
        } else {
            Log.d(TAG, "No custom rules found.")
            emptyList()
        }
    }

    /**
     * Get custom rules for a specific player based on their rule ID.
     *
     * @param playerId The player ID to retrieve rules for.
     * @return A list of CustomRule objects associated with the player.
     */
    fun getCustomRulesForPlayer(playerId: String): List<CustomRule> {
        Log.d(TAG, "Fetching custom rules for player ID: $playerId")

        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(CUSTOM_RULES_KEY, null)

        return if (json != null) {
            val type = object : TypeToken<List<CustomRule>>() {}.type
            val customRules: List<CustomRule> = gson.fromJson(json, type)

            // Filter custom rules for this specific player
            val playerCustomRules = customRules.filter { it.playerId == playerId }
            Log.d(TAG, "Found ${playerCustomRules.size} custom rules for player ID: $playerId")
            playerCustomRules
        } else {
            Log.d(TAG, "No custom rules found for player ID: $playerId")
            emptyList()
        }
    }

    /**
     * Save custom rules for a specific player.
     *
     * @param customRules The list of custom rules to save.
     */
    fun saveCustomRules(customRules: List<CustomRule>) {
        Log.d(TAG, "Saving custom rules. Total rules: ${customRules.size}")

        val sharedPreferences = context.getSharedPreferences(CUSTOM_RULES_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val customRulesJson = gson.toJson(customRules)
        editor.putString(CUSTOM_RULES_KEY, customRulesJson)
        editor.apply()

        Log.d(TAG, "Custom rules saved successfully")
    }

    /**
     * Add a new custom rule to the list and save it.
     *
     * @param customRule The custom rule to be added.
     */
    fun addCustomRule(customRule: CustomRule) {
        val currentRules = loadCustomRules().toMutableList()

        // Add the new custom rule to the list
        currentRules.add(customRule)

        // Save the updated list
        saveCustomRules(currentRules)

        Log.d(TAG, "Added new custom rule: ${customRule.ruleName}.")
    }

    /**
     * Remove a custom rule by ID.
     *
     * @param ruleId The ID of the custom rule to remove.
     */
    fun removeCustomRule(ruleId: String) {
        val currentRules = loadCustomRules().toMutableList()

        // Remove the rule with the matching ruleId
        val ruleRemoved = currentRules.removeIf { it.ruleId == ruleId }

        if (ruleRemoved) {
            saveCustomRules(currentRules)
            Log.d(TAG, "Removed custom rule with ID: $ruleId.")
        } else {
            Log.d(TAG, "No custom rule found with ID: $ruleId.")
        }
    }

    /**
     * Update a custom rule in the list and save it.
     *
     * @param updatedRule The updated custom rule.
     */
    fun updateCustomRule(updatedRule: CustomRule) {
        val currentRules = loadCustomRules().toMutableList()

        // Find the rule with the matching ruleId and update it
        val index = currentRules.indexOfFirst { it.ruleId == updatedRule.ruleId }

        if (index != -1) {
            currentRules[index] = updatedRule
            saveCustomRules(currentRules)
            Log.d(TAG, "Updated custom rule with ID: ${updatedRule.ruleId}.")
        } else {
            Log.d(TAG, "No custom rule found with ID: ${updatedRule.ruleId}.")
        }
    }

    /**
     * Clear all custom rules (reset).
     */
    fun clearCustomRules() {
        saveCustomRules(emptyList())
        Log.d(TAG, "Cleared all custom rules.")
    }
}
