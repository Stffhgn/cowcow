package com.example.cow_cow.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository
import java.util.*

class GameViewModel(application: Application, private val repository: GameRepository)
    : AndroidViewModel(application) {



    // ---- Repository and LiveData ----

    // Repository instance for data persistence
    private val playerRepository = PlayerRepository()

    // LiveData for players
    private val _players = MutableLiveData<List<Player>>()
    // Define the rest of your properties and methods here
    val players = repository.getPlayers()

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    // ---- Team and Game Settings ----

    // List to manage team members
    val team = mutableListOf<Player>()

    // Game settings
    var quietGameEnabled: Boolean = false        // No talking if true
    var falseCallPenaltyEnabled: Boolean = true  // Players penalized for false calls
    var noRepeatRuleEnabled: Boolean = true      // No repeated calls allowed

    // ---- Scavenger Hunt Variables ----

    // List of scavenger hunt items currently in play
    val scavengerHuntList: MutableList<ScavengerHuntItem> = mutableListOf()

    // Timer for scavenger hunt duration
    var scavengerHuntTimer: CountDownTimer? = null

    // ---- Other Game Variables ----

    // Track recently called objects to enforce the no-repeat rule
    private val recentlyCalledObjects: MutableSet<String> = mutableSetOf()


    // ---- Initialization ----

    init {

        loadPlayers()
        loadTeam()
        initializeScavengerHuntItems()
    }

    // ---- Player and Team Management ----

    /**
     * Load players from the repository and update LiveData.
     */
    fun loadPlayers() {
        val context = getApplication<Application>().applicationContext
        val playerList = playerRepository.getPlayers(context)
        _players.value = playerList

        // Update team members based on players' team status
        team.clear()
        team.addAll(playerList.filter { it.isOnTeam })
    }

    /**
     * Load team members from the repository.
     */
    fun loadTeam() {
        val context = getApplication<Application>().applicationContext
        val teamList = playerRepository.getTeam(context)
        team.clear()
        team.addAll(teamList)
    }

    /**
     * Add a player to the team.
     */
    fun addToTeam(player: Player) {
        if (!team.contains(player)) {
            team.add(player)
            player.isOnTeam = true
            updatePlayer(player)
            saveTeam()
        }
    }

    /**
     * Remove a player from the team.
     */
    fun removeFromTeam(player: Player) {
        if (team.contains(player)) {
            team.remove(player)
            player.isOnTeam = false
            updatePlayer(player)
            saveTeam()
        }
    }



    /**
     * Calculate the total team score by summing individual player scores.
     */
    fun calculateTeamScore(): Int {
        return team.sumOf { it.calculateTotalPoints() }
    }

    /**
     * Get a list of individual players not on the team.
     */
    fun getIndividualPlayers(): List<Player> {
        return _players.value?.filter { !it.isOnTeam } ?: emptyList()
    }

    /**
     * Save the current list of players to the repository.
     */
    fun savePlayers() {
        val context = getApplication<Application>().applicationContext
        _players.value?.let { playerList ->
            playerRepository.savePlayers(playerList, context)
        }
    }

    /**
     * Save the current team to the repository.
     */
    fun saveTeam() {
        val context = getApplication<Application>().applicationContext
        playerRepository.saveTeam(team, context)
    }

    /**
     * Update a player's information and persist the changes.
     */
    fun updatePlayer(player: Player) {
        val context = getApplication<Application>().applicationContext
        playerRepository.updatePlayer(player, context)

        // Update LiveData with the modified player
        val updatedPlayers = _players.value?.map {
            if (it.id == player.id) player else it
        }
        _players.value = updatedPlayers
    }

    /**
     * Remove a player by their ID.
     */
    fun removePlayerById(playerId: Int) {
        val context = getApplication<Application>().applicationContext
        playerRepository.removePlayerById(playerId, context)

        // Update LiveData by filtering out the removed player
        _players.value = _players.value?.filter { it.id != playerId }
    }

    // ---- Scavenger Hunt Logic ----

    /**
     * Initialize the predefined scavenger hunt items and update LiveData.
     */
    fun initializeScavengerHuntItems() {
        val items = listOf(
            // Add your scavenger hunt items here
            ScavengerHuntItem("Cow", DifficultyLevel.EASY),
            ScavengerHuntItem("Deer", DifficultyLevel.HARD),
            ScavengerHuntItem("School Bus", DifficultyLevel.MEDIUM),
            ScavengerHuntItem("Fire Hydrant", DifficultyLevel.EASY),
            ScavengerHuntItem("Police Car", DifficultyLevel.MEDIUM)
            // ... (Include the rest of your items)
        )
        _scavengerHuntItems.value = items
    }

    /**
     * Get the list of scavenger hunt items.
     */
    fun getScavengerHuntItems(): List<ScavengerHuntItem> {
        return _scavengerHuntItems.value ?: emptyList()
    }

    /**
     * Initialize the scavenger hunt by selecting random items.
     */
    fun initializeScavengerHunt() {
        scavengerHuntList.clear()

        // Shuffle and select random items (e.g., select 5 items)
        val items = _scavengerHuntItems.value?.shuffled()?.take(5) ?: emptyList()
        scavengerHuntList.addAll(items)
    }

    /**
     * Add or edit a scavenger hunt item.
     */
    fun addOrEditScavengerHuntItem(item: ScavengerHuntItem) {
        // Update the item if it exists; otherwise, add it
        val index = scavengerHuntList.indexOfFirst { it.name == item.name }
        if (index >= 0) {
            scavengerHuntList[index] = item
        } else {
            scavengerHuntList.add(item)
        }
    }

    /**
     * Mark a scavenger hunt item as found and award a random reward to the player.
     */
    fun markScavengerItemAsFound(itemName: String, player: Player): Boolean {
        val item = scavengerHuntList.find { it.name == itemName }
        return if (item != null && !item.isFound) {
            item.isFound = true
            // Award a random reward to the player
            awardRandomReward(item, player)
            updatePlayer(player)
            true
        } else {
            false
        }
    }

    /**
     * Check if the scavenger hunt is complete.
     */
    fun isScavengerHuntComplete(): Boolean {
        return scavengerHuntList.all { it.isFound }
    }

    /**
     * Award bonus points and activate a power-up when the scavenger hunt is complete.
     */
    fun awardBonusForCompletion() {
        if (isScavengerHuntComplete()) {
            val currentPlayer = getCurrentPlayer()
            currentPlayer.addBasePoints(10)  // Award bonus points
            activatePowerUp(currentPlayer, PowerUpType.DOUBLE_POINTS, 3600000L)  // Activate 1-hour Double Points
            println("Bonus awarded to ${currentPlayer.name} for completing the scavenger hunt!")
        }
    }

    /**
     * Award a random reward to the player based on the scavenger hunt item.
     */
    fun awardRandomReward(item: ScavengerHuntItem, player: Player) {
        val points = item.getPoints()  // Points based on difficulty

        // Generate a random reward
        when ((1..3).random()) {
            1 -> {
                // Award points to the player
                player.addBasePoints(points)
                println("${player.name} awarded $points points for finding ${item.name}!")
            }
            2 -> {
                // Grant silence power to the player (silence another player)
                grantSilencePower(player, 300000L)  // 5 minutes
                println("${player.name} can silence another player for 5 minutes!")
            }
            3 -> {
                // Activate Double Points for the player
                activatePowerUp(player, PowerUpType.DOUBLE_POINTS, 30000L)  // 30 seconds
                println("Double points activated for ${player.name}!")
            }
        }
        updatePlayer(player)
    }

    /**
     * Grant the ability for a player to silence another player.
     */
    private fun grantSilencePower(player: Player, duration: Long) {
        // Get other players excluding the current player
        val otherPlayers = players.value?.filter { it != player } ?: emptyList()

        // Randomly select a player to silence
        if (otherPlayers.isNotEmpty()) {
            val silencedPlayer = otherPlayers.random()
            applyPenalty(silencedPlayer, PenaltyType.SILENCED, duration)
            println("${silencedPlayer.name} is silenced by ${player.name} for ${duration / 60000} minutes!")
        }
    }

    /**
     * Start the scavenger hunt timer for the specified duration.
     */
    fun startScavengerHuntTimer(duration: Long) {
        scavengerHuntTimer?.cancel()
        scavengerHuntTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update UI if needed
            }

            override fun onFinish() {
                // Handle timer completion (e.g., end scavenger hunt)
                println("Scavenger hunt timer finished!")
            }
        }.start()
    }

    /**
     * Stop the scavenger hunt timer manually.
     */
    fun stopScavengerHuntTimer() {
        scavengerHuntTimer?.cancel()
        scavengerHuntTimer = null
    }

    // ---- Game Logic ----

    /**
     * Apply points for a specific action (e.g., calling a cow).
     */
    fun applyPointsForAction(player: Player, action: String) {
        // Determine base points based on the action
        var points = when (action) {
            "Cow" -> 1
            "Church" -> 2
            "Water Tower" -> 3
            else -> 0
        }

        // Update player's counts
        when (action) {
            "Cow" -> player.cowCount += 1
            "Church" -> player.churchCount += 1
            "Water Tower" -> player.waterTowerCount += 1
        }

        // Check for active power-ups
        if (isPowerUpActive(player, PowerUpType.DOUBLE_POINTS)) {
            points *= 2
        }

        // Add points to the player's base points
        player.addBasePoints(points)
        updatePlayer(player)
    }

    /**
     * Apply points with custom rules based on the object type.
     */
    fun applyPointsWithRules(player: Player, objectType: String) {
        // Apply points and rules (extend this method as needed)
        applyPointsForAction(player, objectType)
    }

    /**
     * Reset called objects and players' counts.
     */
    fun resetCalledObjects() {
        playerViewModel.players.value?.forEach { player ->
            player.cowCount = 0
            player.churchCount = 0
            player.waterTowerCount = 0
            player.basePoints = 0
            playerViewModel.updatePlayer(player)  // Use PlayerViewModel to update players
        }
    }

    // ---- Power-Up Management ----

    /**
     * Activate a power-up for a player.
     */
    private fun activatePowerUp(player: Player, type: PowerUpType, duration: Long) {
        // Check if the power-up is already active
        val powerUp = player.activePowerUps.find { it.type == type }
        if (powerUp != null) {
            powerUp.isActive = true
            powerUp.duration = duration
        } else {
            player.activePowerUps.add(PowerUp(type, isActive = true, duration = duration))
        }

        // Schedule power-up deactivation
        if (duration > 0) schedulePowerUpDeactivation(player, type, duration)
    }

    /**
     * Schedule deactivation of a power-up after a delay.
     */
    private fun schedulePowerUpDeactivation(player: Player, type: PowerUpType, delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                deactivatePowerUp(player, type)
            }
        }, delay)
    }

    /**
     * Deactivate a power-up for a player.
     */
    private fun deactivatePowerUp(player: Player, type: PowerUpType) {
        player.activePowerUps.find { it.type == type }?.isActive = false
        println("$type power-up deactivated for ${player.name}!")
        updatePlayer(player)
    }

    /**
     * Check if a power-up is active for a player.
     */
    fun isPowerUpActive(player: Player, type: PowerUpType): Boolean {
        return player.activePowerUps.any { it.type == type && it.isActive }
    }

    // ---- Penalty Management ----

    /**
     * Apply a penalty to a player.
     */
    fun applyPenalty(player: Player, type: PenaltyType, duration: Long = 0L) {
        val penalty = Penalty(
            id = generateUniquePenaltyId(),
            name = type.name,
            pointsDeducted = calculatePenaltyPoints(type),
            penaltyType = type,
            isActive = true,
            duration = duration,
            startTime = System.currentTimeMillis()
        )

        // Add penalty to the player
        player.applyPenalty(penalty)

        // Schedule penalty removal if duration is set
        if (duration > 0) {
            schedulePenaltyRemoval(player, penalty, duration)
        }
        updatePlayer(player)
    }

    /**
     * Calculate penalty points based on the penalty type.
     */
    private fun calculatePenaltyPoints(type: PenaltyType): Int {
        return when (type) {
            PenaltyType.SILENCED -> 0
            PenaltyType.POINT_DEDUCTION -> 10
            PenaltyType.TEMPORARY_BAN -> 0
            PenaltyType.FALSE_CALL -> 5
            PenaltyType.OTHER -> 1
        }
    }

    /**
     * Generate a unique ID for penalties.
     */
    private fun generateUniquePenaltyId(): Int {
        return (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }

    /**
     * Schedule removal of a penalty after a delay.
     */
    private fun schedulePenaltyRemoval(player: Player, penalty: Penalty, delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                penalty.isActive = false
                player.removeExpiredPenalties()
                updatePlayer(player)
            }
        }, delay)
    }

    /**
     * Check if a player is currently penalized.
     */
    fun isPlayerPenalized(player: Player): Boolean = player.isPenalized()

    // ---- Helper Functions ----

    /**
     * Get the current player (placeholder implementation).
     */
    fun getCurrentPlayer(): Player {
        return players.value?.firstOrNull() ?: Player(
            id = -1,
            name = "Unknown Player",
            cowCount = 0,
            churchCount = 0,
            waterTowerCount = 0,
            isOnTeam = false,
            isPowerUpActive = false,
            basePoints = 0,
            penaltyPoints = 0,
            penalties = mutableListOf(),
            isSilenced = false,
            achievements = mutableListOf(),
            customRules = mutableListOf(),
            timePlayed = 0L,
            distanceTraveled = 0f,
            teamId = null,
            activePowerUps = mutableListOf()
        )
    }

    /**
     * End the current event by deactivating power-ups.
     */
    fun endCurrentEvent(player: Player) {
        deactivatePowerUp(player, PowerUpType.DOUBLE_POINTS)
    }
}
