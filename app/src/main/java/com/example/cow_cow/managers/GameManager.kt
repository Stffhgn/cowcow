package com.example.cow_cow.managers

import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.utils.GameUtils
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
            GameMode.TIME_TRIAL -> {
                // Initialize time-trial game logic
                startTimer(durationMillis)
            }
            GameMode.TEAM_BATTLE -> {
                // Initialize team battle logic
            }
            GameMode.CUSTOM -> {
                // Initialize custom rules game mode
            }
            GameMode.COOPERATIVE -> {
                // Initialize cooperative game logic
            }
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

    /**
     * Apply custom rules based on game mode.
     */
    fun applyCustomRulesForGame(players: List<Player>, gameMode: GameMode) {
        for (player in players) {
            CustomRuleManager.applyCustomRule(player, gameMode)
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
