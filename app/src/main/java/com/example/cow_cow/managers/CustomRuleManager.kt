package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.enums.*
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils

object CustomRuleManager {

    private lateinit var context: Context
    private val activeCustomRules = mutableListOf<CustomRule>() // Stores active custom rules

    /**
     * Initializes the CustomRuleManager with context and loads rules.
     */
    fun initialize(context: Context) {
        this.context = context
        loadCustomRules()
    }

    /**
     * Creates and adds a new custom rule.
     */
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
        saveCustomRules() // Save rules after creation
    }

    /**
     * Applies custom rules for all players in the current game.
     */
    fun applyCustomRulesForGame(players: List<Player>, gameMode: GameMode) {
        Log.d("CustomRuleManager", "Applying custom rules for game mode: $gameMode")
        players.forEach { player ->
            val applicableRules = activeCustomRules.filter { it.playerId == null || it.playerId == player.id }
            applicableRules.forEach { rule ->
                applyCustomRule(player, rule)
            }
        }
    }

    /**
     * Applies a custom rule to a specific player.
     */
    fun applyCustomRule(player: Player, rule: CustomRule) {
        if (!checkRuleCondition(player, rule)) {
            Log.d("CustomRuleManager", "Rule '${rule.ruleName}' condition not met for player ${player.name}")
            return
        }

        when (rule.ruleEffect) {
            RuleEffectType.ADD_POINTS -> applyAddPoints(player, rule)
            RuleEffectType.DEDUCT_POINTS -> applyPointDeduction(player, rule)
            RuleEffectType.SILENCE_PLAYER -> applySilencePlayer(player, rule)
            RuleEffectType.CUSTOM_PENALTY -> applyCustomPenalty(player, rule)
            RuleEffectType.DOUBLE_POINTS -> applyDoublePoints(player, rule)
            RuleEffectType.POWER_UP_REWARD -> applyPowerUpReward(player, rule)
            else -> Log.d("CustomRuleManager", "Rule effect '${rule.ruleEffect}' is not implemented for player ${player.name}")
        }
    }

    private fun applyAddPoints(player: Player, rule: CustomRule) {
        ScoreManager.addPointsToPlayer(player, rule.value)
        Log.d("CustomRuleManager", "Added ${rule.value} points to ${player.name} due to rule '${rule.ruleName}'")
    }

    private fun applyPointDeduction(player: Player, rule: CustomRule) {
        val penalty = createPenalty(player, rule, PenaltyType.POINT_DEDUCTION, rule.value)
        PenaltyManager.applyPenalty(player, penalty)
        Log.d("CustomRuleManager", "Applied point deduction of ${rule.value} points to ${player.name} due to rule '${rule.ruleName}'")
    }

    private fun applySilencePlayer(player: Player, rule: CustomRule) {
        val penalty = createPenalty(player, rule, PenaltyType.SILENCED, duration = rule.duration)
        PenaltyManager.applyPenalty(player, penalty)
        Log.d("CustomRuleManager", "Silenced ${player.name} due to rule '${rule.ruleName}'")
    }

    private fun applyCustomPenalty(player: Player, rule: CustomRule) {
        player.penaltyPoints += rule.value
        Log.d("CustomRuleManager", "Applied custom penalty of ${rule.value} points to player ${player.name}")
    }

    private fun applyDoublePoints(player: Player, rule: CustomRule) {
        val pointsToAdd = rule.value * 2
        ScoreManager.addPointsToPlayer(player, pointsToAdd)
        Log.d("CustomRuleManager", "Double points rule applied: ${pointsToAdd} points added to ${player.name} due to rule '${rule.ruleName}'")
    }

    private fun applyPowerUpReward(player: Player, rule: CustomRule) {
        val duration = 60 * 60 * 1000L // 60 minutes duration
        PowerUpManager.activatePowerUp(
            player = player,
            powerUpType = PowerUpType.DOUBLE_POINTS,
            duration = duration,
            effectValue = rule.value
        )
        Log.d("CustomRuleManager", "Granted Double Points power-up to ${player.name} for rule '${rule.ruleName}'")
    }

    private fun createPenalty(player: Player, rule: CustomRule, penaltyType: PenaltyType, pointsDeducted: Int = 0, duration: Long = 0L): Penalty {
        return Penalty(
            id = Penalty.generatePenaltyId(player.id, penaltyType).toString(),
            name = "${penaltyType.name} for Rule: ${rule.ruleName}",
            penaltyType = penaltyType,
            pointsDeducted = pointsDeducted,
            duration = duration,
            isActive = true
        )
    }

    private fun checkRuleCondition(player: Player, rule: CustomRule): Boolean {
        val result = when (rule.conditionType) {
            RuleConditionType.ALWAYS -> true
            RuleConditionType.PLAYER_HAS_LESS_THAN_X_POINTS -> player.basePoints < rule.conditionValue
            RuleConditionType.PLAYER_HAS_MORE_THAN_X_POINTS -> player.basePoints > rule.conditionValue
        }
        Log.d("CustomRuleManager", "Rule '${rule.ruleName}' condition check result: $result for player ${player.name}")
        return result
    }

    fun checkRules() {
        val currentTime = System.currentTimeMillis()
        activeCustomRules.removeAll { rule ->
            rule.duration > 0 && (currentTime - rule.duration) >= rule.duration
        }
        saveCustomRules()
        Log.d("CustomRuleManager", "Checked and removed expired rules.")
    }

    fun removeCustomRule(rule: CustomRule) {
        activeCustomRules.remove(rule)
        saveCustomRules()
        Log.d("CustomRuleManager", "Removed custom rule ${rule.ruleName}")
    }

    private fun saveCustomRules() {
        DataUtils.saveCustomRules(context, activeCustomRules)
        Log.d("CustomRuleManager", "Saved custom rules.")
    }

    private fun loadCustomRules() {
        activeCustomRules.clear()
        activeCustomRules.addAll(DataUtils.loadCustomRules(context))
        Log.d("CustomRuleManager", "Loaded custom rules from storage.")
    }
}
