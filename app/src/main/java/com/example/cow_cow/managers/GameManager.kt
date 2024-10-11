package com.example.cow_cow.managers

import android.content.Context
import android.util.Log
import com.example.cow_cow.utils.GameTimer
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.RuleConditionType
import com.example.cow_cow.enums.RuleEffectType
import com.example.cow_cow.listeners.GameEventListener
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.utils.GameUtils
import com.example.cow_cow.models.CustomRule

class GameManager(private val playerManager: PlayerManager) {

    var currentGameMode: GameMode? = null
    private var gameTimer: GameTimer? = null
    private var isGamePaused: Boolean = false
    private var gameDurationMillis: Long = 0L  // For tracking time-based game modes

    private val listeners = mutableListOf<GameEventListener>()

    /**
     * Start a new game in the specified game mode.
     */
    fun startGame(gameMode: GameMode, durationMillis: Long? = null) {
        currentGameMode = gameMode
        gameDurationMillis = durationMillis ?: 0L
        isGamePaused = false

        notifyListeners { onGameStarted(gameMode) }

        when (gameMode) {
            GameMode.CLASSIC -> {
                Log.d("GameManager", "Starting game in CLASSIC mode")
            }
            GameMode.SCAVENGER_HUNT, GameMode.TRIVIA -> {
                Log.d("GameManager", "Starting game in $gameMode mode with timer")
                startTimer(durationMillis)
            }
            GameMode.TEAM_BATTLE -> {
                Log.d("GameManager", "Starting game in TEAM_BATTLE mode")
            }
            GameMode.RAINBOW_CAR -> {
                Log.d("GameManager", "Starting game in RAINBOW_CAR mode")
            }
            else -> {
                Log.w("GameManager", "Game mode $gameMode not implemented.")
            }
        }
    }

    /**
     * Stops the current game and resets necessary data.
     */
    fun stopGame() {
        currentGameMode = null
        gameTimer?.stop()
        notifyListeners { onGameStopped() }
        Log.d("GameManager", "Game has been stopped.")
    }

    /**
     * Pauses the current game.
     */
    fun pauseGame() {
        isGamePaused = true
        gameTimer?.pause()
        notifyListeners { onGamePaused() }
        Log.d("GameManager", "Game has been paused.")
    }

    /**
     * Resumes the paused game.
     */
    fun resumeGame() {
        if (isGamePaused && currentGameMode != null) {
            isGamePaused = false
            gameTimer?.resume()
            notifyListeners { onGameResumed() }
            Log.d("GameManager", "Game has been resumed.")
        }
    }

    /**
     * Start a timer for time-based game modes (like scavenger hunt, time trial, trivia).
     */
    private fun startTimer(durationMillis: Long?) {
        durationMillis?.let {
            gameTimer = GameTimer(it, {
                stopGame()  // Auto-stop the game when the timer finishes
                notifyListeners { onGameTimeExpired() }
                Log.d("GameManager", "Game timer expired. Stopping game.")
            })
            gameTimer?.start()
        }
    }

    /**
     * Apply a time penalty to the game timer.
     */
    fun applyTimePenaltyToGame(duration: Long) {
        gameTimer?.applyPenalty(duration)
        Log.d("GameManager", "Game time reduced by $duration milliseconds due to penalty.")
    }

    /**
     * Apply a speed multiplier penalty to the game timer based on penalty level.
     */
    fun applySpeedMultiplierPenalty(penaltyLevel: Int) {
        val multiplier = 1.0 + (penaltyLevel * 0.25)
        gameTimer?.applySpeedMultiplier(multiplier)
        Log.d("GameManager", "Game timer speed increased by multiplier of $multiplier due to penalty level $penaltyLevel.")
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

    /**
     * Apply custom rules based on game mode.
     */
    fun applyCustomRulesForGame(context: Context, gameMode: GameMode) {
        val players = playerManager.getAllPlayers()

        players.forEach { player ->
            val rule = getCustomRuleForGameMode(gameMode)
            rule?.let {
                player.customRule = it
                CustomRuleManager.applyCustomRule(player, rule)
                Log.d("GameManager", "Assigned custom rule '${rule.ruleName}' to player '${player.name}'")
            } ?: Log.w("GameManager", "No custom rule found for player '${player.name}' in mode $gameMode")
        }
    }

    private fun getCustomRuleForGameMode(gameMode: GameMode): CustomRule? {
        return when (gameMode) {
            GameMode.CLASSIC -> CustomRule(
                ruleId = "classic_001",
                playerId = null,  // Applies to all players
                ruleName = "Classic Boost",
                ruleDescription = "Adds 10 points to your score",
                ruleEffect = RuleEffectType.ADD_POINTS,
                value = 10,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.SCAVENGER_HUNT -> CustomRule(
                ruleId = "scavenger_001",
                playerId = null,
                ruleName = "Silence Rule",
                ruleDescription = "Silences a player for 1 round",
                ruleEffect = RuleEffectType.SILENCE_PLAYER,
                value = 0,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.TEAM_BATTLE -> CustomRule(
                ruleId = "team_battle_001",
                playerId = null,
                ruleName = "Double Points Battle",
                ruleDescription = "Doubles points earned for the next round",
                ruleEffect = RuleEffectType.DOUBLE_POINTS,
                value = 5,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.TRIVIA -> CustomRule(
                ruleId = "trivia_001",
                playerId = null,
                ruleName = "Trivia Bonus",
                ruleDescription = "Adds 15 points to your score",
                ruleEffect = RuleEffectType.ADD_POINTS,
                value = 15,
                duration = 0L,
                conditionType = RuleConditionType.ALWAYS,
                conditionValue = 0
            )
            GameMode.RAINBOW_CAR -> CustomRule(
                ruleId = "rainbow_car_001",
                playerId = null,
                ruleName = "Rainbow Silence",
                ruleDescription = "Silences a player for 1 round",
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