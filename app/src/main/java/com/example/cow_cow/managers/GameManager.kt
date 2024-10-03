package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.enums.RuleEffectType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.utils.GameUtils
import com.example.cow_cow.managers.CustomRuleManager
import com.example.cow_cow.models.CustomRule
import java.util.Timer
import kotlin.concurrent.schedule

object GameManager {

    var currentGameMode: GameMode? = null
    private var gameTimer: Timer? = null
    private var isGamePaused: Boolean = false
    private var gameDurationMillis: Long = 0L  // For tracking time-based game modes

    private val listeners = mutableListOf<GameEventListener>()

    /**
     * Start a new game in the specified game mode.
     */
    fun startGame(gameMode: GameMode, durationMillis: Long? = null) {
        currentGameMode = gameMode
        gameDurationMillis = durationMillis ?: 0L  // Optional time limit for the game mode
        isGamePaused = false

        notifyListeners { onGameStarted(gameMode) }

        when (gameMode) {
            GameMode.CLASSIC -> {
                // Initialize classic game logic
            }
            GameMode.SCAVENGER_HUNT -> {
                // Initialize scavenger hunt game logic
                startTimer(durationMillis)
            }
            GameMode.TEAM_BATTLE -> {
                // Initialize team battle logic
            }
            /*
            GameMode.TIME_TRIAL -> {
                // Initialize time-trial game logic
                startTimer(durationMillis)
            }
            GameMode.CUSTOM -> {
                // Initialize custom rules game mode
            }
            GameMode.COOPERATIVE -> {
                // Initialize cooperative game logic
            }
            */
            GameMode.TRIVIA -> {
                // Initialize trivia game logic
                startTimer(durationMillis)
            }
            GameMode.RAINBOW_CAR -> {
                // Initialize rainbow car game logic
            }
        }
    }

    /**
     * Stops the current game and resets necessary data.
     */
    fun stopGame() {
        currentGameMode = null
        gameTimer?.cancel()
        notifyListeners { onGameStopped() }
        // Perform any necessary cleanup for the current game mode
    }

    /**
     * Pauses the current game.
     */
    fun pauseGame() {
        isGamePaused = true
        gameTimer?.cancel()  // Stop the game timer if applicable
        notifyListeners { onGamePaused() }
    }

    /**
     * Resumes the paused game.
     */
    fun resumeGame() {
        if (isGamePaused && currentGameMode != null) {
            isGamePaused = false
            startTimer(gameDurationMillis)  // Resume the timer
            notifyListeners { onGameResumed() }
        }
    }

    /**
     * Start a timer for time-based game modes (like scavenger hunt, time trial, trivia).
     */
    private fun startTimer(durationMillis: Long?) {
        durationMillis?.let {
            gameTimer = Timer().apply {
                schedule(it) {
                    stopGame()  // Auto-stop the game when the timer finishes
                    notifyListeners { onGameTimeExpired() }
                }
            }
        }
    }

    // ----- Game Event Listeners -----

    fun addGameEventListener(listener: GameEventListener) {
        listeners.add(listener)
    }

    fun removeGameEventListener(listener: GameEventListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(action: GameEventListener.() -> Unit) {
        for (listener in listeners) {
            listener.action()
        }
    }

    // ----- Helper Methods -----

    /**
     * Example method to get the current game's leading player.
     */
    fun getLeadingPlayer(players: List<Player>): Player? {
        return GameUtils.getPlayerWithHighestScore(players)
    }

    /**
     * Example method to reset all players at the end of the game.
     */
    fun resetPlayers(players: List<Player>) {
        GameUtils.resetAllPlayersCounts(players)
    }

    /**
     * Distribute points to the players in a team.
     */
    fun distributePointsToTeam(team: Team, points: Int) {
        GameUtils.distributeTeamPoints(team, points)
    }
/*
    /**
     * Trigger game-specific achievements based on the mode.
     */
    fun triggerAchievementsForMode(players: List<Player>, gameMode: GameMode) {
        when (gameMode) {
            GameMode.SCAVENGER_HUNT -> AchievementManager.unlockAchievementsForPlayers(players, "Scavenger Hunt Master")
            GameMode.TRIVIA -> AchievementManager.unlockAchievementsForPlayers(players, "Trivia Genius")
            GameMode.RAINBOW_CAR -> AchievementManager.unlockAchievementsForPlayers(players, "Rainbow Car Champ")
            GameMode.CLASSIC -> AchievementManager.unlockAchievementsForPlayers(players, "Classic Game Champion")
        }
    }
*/
    /**
     * Apply custom rules based on game mode.
     */
    /**
     * Apply custom rules based on game mode.
     */
    fun applyCustomRulesForGame(context: Context, gameMode: GameMode) {
        // Fetch players from PlayerManager dynamically
        val players = PlayerManager.getAllPlayers()

        players.forEach { player ->
            val rule = getCustomRuleForGameMode(gameMode)
            rule?.let {
                player.customRule = it
                CustomRuleManager.applyCustomRule(player, rule)
                Log.d("GameManager", "Assigned custom rule ${rule.ruleName} to player ${player.name}")
            } ?: Log.w("GameManager", "No custom rule found for player ${player.name}")
        }
    }

    private fun getCustomRuleForGameMode(gameMode: GameMode): CustomRule? {
        return when (gameMode) {
            GameMode.CLASSIC -> CustomRule(
                ruleId = 1,
                ruleName = "Classic Rule",
                ruleDescription = "Adds 10 points to your score",
                ruleEffect = RuleEffectType.ADD_POINTS,
                value = 10,
                duration = 0L, // Set duration if applicable
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.SCAVENGER_HUNT -> CustomRule(
                ruleId = 2,
                ruleName = "Scavenger Hunt Rule",
                ruleDescription = "Silences a player",
                ruleEffect = RuleEffectType.SILENCE_PLAYER,
                value = 0,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.TEAM_BATTLE -> CustomRule(
                ruleId = 3,
                ruleName = "Team Battle Rule",
                ruleDescription = "Doubles points earned",
                ruleEffect = RuleEffectType.DOUBLE_POINTS,
                value = 5,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.TRIVIA -> CustomRule(
                ruleId = 4,
                ruleName = "Trivia Rule",
                ruleDescription = "Adds 15 points to your score",
                ruleEffect = RuleEffectType.ADD_POINTS,
                value = 15,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.RAINBOW_CAR -> CustomRule(
                ruleId = 5,
                ruleName = "Rainbow Car Rule",
                ruleDescription = "Silences a player",
                ruleEffect = RuleEffectType.SILENCE_PLAYER,
                value = 0,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            else -> null // Handle other game modes or return null if no rule applies
        }
    }
    }

// Interface for game event listeners
interface GameEventListener {
    fun onGameStarted(gameMode: GameMode)
    fun onGameStopped()
    fun onGamePaused()
    fun onGameResumed()
    fun onGameTimeExpired()  // For time-based modes
}
