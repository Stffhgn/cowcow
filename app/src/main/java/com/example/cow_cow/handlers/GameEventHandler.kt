// GameEventHandler.kt

package com.example.cow_cow.handlers

import android.util.Log
import android.content.Context
import com.example.cow_cow.models.Player
import com.example.cow_cow.managers.*
import com.example.cow_cow.enums.*
import com.example.cow_cow.models.*

class GameEventHandler(
    private val context: Context,
    private val playerManager: PlayerManager,
    private val teamManager: TeamManager,
    private val scoreManager: ScoreManager,
    private val scavengerHuntManager: ScavengerHuntManager,
    private val soundManager: SoundManager,
    private val conditionManager: ConditionManager,
    private val achievementManager: AchievementManager,
    private val customRuleManager: CustomRuleManager,
    private val gameManager: GameManager,
    private val gameNewsManager: GameNewsManager,
    private val penaltyManager: PenaltyManager,
    private val powerUpManager: PowerUpManager,
    private val triviaManager: TriviaManager
) {
    // Log tag
    private val TAG = "GameEventHandler"

    // Handle unclaimed object event
    fun handleUnclaimedObjectEvent(objectType: String) {
        logAndAddNews("Handling unclaimed object event: Object type: $objectType", "An unclaimed object ($objectType) was spotted but no one claimed it!")
        playSoundForEvent(SoundType.COW_SOUND)
    }

    // Handle player selection for claiming an object
    fun handlePlayerSelected(player: Player, objectType: String) {
        logAndAddNews("Handling player selection: Player: \${player.name}, Object type: $objectType")
        val updatedScore = scoreManager.calculatePlayerScoreAfterEvent(player, objectType)
        Log.d(TAG, "Player: \${player.name}, New score after claiming $objectType: $updatedScore")

        updateTeamScoreIfMember(player)
        checkConditionsAfterEvent(player, objectType)
        achievementManager.checkAchievements(player)
        applyCustomRulesAndPenalties(player)

        gameNewsManager.addNewsMessage("Player \${player.name} has claimed $objectType and earned $updatedScore points!")
    }

    // Assign player to a team
    fun assignPlayerToTeam(player: Player, teamId: Int) {
        teamManager.getAllTeams().find { it.id == teamId }?.let { team ->
            teamManager.addPlayerToTeam(player, team)
            logAndAddNews("Player \${player.name} successfully assigned to team \${team.name}", "Player \${player.name} has joined team \${team.name}!")
        } ?: Log.e(TAG, "Team with ID $teamId not found. Player \${player.name} could not be assigned.")
    }

    // Handle scavenger hunt item found
    fun handleScavengerHuntItemFound(player: Player, item: ScavengerHuntItem) {
        scavengerHuntManager.markItemAsFound(item, player)
        achievementManager.trackProgress(player, AchievementType.SCAVENGER_HUNT, 1)
        logAndAddNews("Player \${player.name} found scavenger hunt item: \${item.name}", "Player \${player.name} found the scavenger hunt item: \${item.name}!")
    }

    // Apply penalty to player
    fun applyPenaltyToPlayer(player: Player, penalty: Penalty) {
        penaltyManager.applyPenalty(player, penalty)
        if (penalty.penaltyType == PenaltyType.SILENCED || penalty.penaltyType == PenaltyType.TEMPORARY_BAN) {
            if (player.isSilenced) Log.d(TAG, "Player \${player.name} is now silenced due to penalty: \${penalty.name}")
        }
        gameNewsManager.addNewsMessage("Player \${player.name} has received a penalty: \${penalty.name}.")
    }

    // Activate power-up for player
    fun activatePowerUpForPlayer(player: Player, powerUpType: PowerUpType, duration: Long, effectValue: Int = 0) {
        powerUpManager.activatePowerUp(player, powerUpType, duration, effectValue)
        gameNewsManager.addNewsMessage("Player \${player.name} activated power-up: \${powerUpType.name}!")
    }

    // Handle trivia question answer
    fun handleTriviaQuestionAnswer(player: Player, selectedAnswer: String) {
        val isCorrect = triviaManager.validateAnswer(player, selectedAnswer)
        val message = if (isCorrect) {
            "Player \${player.name} answered the trivia question correctly and earned points!"
        } else {
            "Player \${player.name} answered the trivia question incorrectly."
        }
        gameNewsManager.addNewsMessage(message)
    }

    // Reset player stats
    fun resetPlayerStats(player: Player) {
        playerManager.resetAllPlayerStats()
        penaltyManager.clearPlayerPenalties(player)
        powerUpManager.clearActivePowerUps(player)
    }

    // Handle custom rule application
    fun applyCustomRuleToPlayer(player: Player, rule: CustomRule) {
        playerManager.applyCustomRuleToPlayer(player, rule)
    }

    // Handle game reset
    fun handleGameReset(players: List<Player>) {
        players.forEach { resetPlayerStats(it) }
        scoreManager.resetPlayerScores(players)
        gameNewsManager.addNewsMessage("The game has been reset. All player stats have been cleared!")
    }

    // Apply custom rules for the game based on the game mode
    fun applyCustomRulesForGame(players: List<Player>, gameMode: GameMode) {
        customRuleManager.applyCustomRulesForGame(players, gameMode)
    }

    // Start a new game with the specified game mode
    fun startGame(gameMode: GameMode, durationMillis: Long? = null) {
        gameManager.startGame(gameMode, durationMillis)
        gameNewsManager.addNewsMessage("A new game has started in $gameMode mode!")
    }

    // Stop the current game
    fun stopGame() {
        gameManager.stopGame()
        gameNewsManager.addNewsMessage("The current game has been stopped.")
    }

    // Pause the current game
    fun pauseGame() {
        gameManager.pauseGame()
        gameNewsManager.addNewsMessage("The current game has been paused.")
    }

    // Utility function to play a sound for an event
    private fun playSoundForEvent(soundType: SoundType) {
        soundManager.playSound(context, soundType)
    }

    // Utility function to log and add news messages
    private fun logAndAddNews(logMessage: String, newsMessage: String? = null) {
        Log.d(TAG, logMessage)
        newsMessage?.let { gameNewsManager.addNewsMessage(it) }
    }

    // Check conditions after an event
    private fun checkConditionsAfterEvent(player: Player, objectType: String) {
        if (conditionManager.evaluateAllConditions(listOf(Condition(ConditionType.SCORE_THRESHOLD, 10, "Player must reach a score of at least 10")), player)) {
            Log.d(TAG, "Player \${player.name} has met the condition after claiming $objectType.")
        }
    }

    // Update team score if player is a member
    private fun updateTeamScoreIfMember(player: Player) {
        teamManager.getAllTeams().find { it.members.contains(player) }?.let { team ->
            Log.d(TAG, "\${player.name} is part of team: \${team.name}. Updating team score.")
            teamManager.updateTeamScore(team)
        }
    }

    // Apply custom rules and remove expired penalties/power-ups
    private fun applyCustomRulesAndPenalties(player: Player) {
        player.customRule?.let { customRuleManager.applyCustomRule(player, it) }
        penaltyManager.removeExpiredPenalties(player)
        powerUpManager.checkForExpiredPowerUps(player)
    }
}