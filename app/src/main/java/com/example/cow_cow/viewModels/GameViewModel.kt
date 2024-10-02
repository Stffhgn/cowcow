package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository

class GameViewModel(application: Application, private val repository: GameRepository) : AndroidViewModel(application) {

    // ---- LiveData for players and team ----
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    private val _team = MutableLiveData<List<Player>>()
    val team: LiveData<List<Player>> get() = _team

    // ---- Managers ----
    private val scavengerHuntManager = ScavengerHuntManager()
    private val powerUpManager = PowerUpManager()
    private val penaltyManager = PenaltyManager()
    private val playerManager = PlayerManager()
    private val teamManager = TeamManager()

    // ---- Game settings ----
    var quietGameEnabled: Boolean = false
    var falseCallPenaltyEnabled: Boolean = true
    var noRepeatRuleEnabled: Boolean = true

    // ---- Initialization ----
    init {
        loadPlayers()
        loadTeam()
    }

    // ---- Player and Team Management ----

    fun loadPlayers() {
        val context = getApplication<Application>().applicationContext
        val playerList = PlayerRepository().getPlayers(context)
        _players.value = playerList
    }

    // LiveData to hold news messages for the rotating TextView
    private val _gameNews = MutableLiveData<String>()
    val gameNews: LiveData<String> get() = _gameNews

    // Instance of the GameNewsManager
    private val gameNewsManager = GameNewsManager()

    // Add a new game news message
    fun addGameNewsMessage(message: String) {
        gameNewsManager.addNewsMessage(message)
        // Optionally update the LiveData immediately after adding the news
        _gameNews.value = message
    }

    // Rotate to the next news message and update LiveData
    fun rotateGameNews() {
        _gameNews.value = gameNewsManager.getNextNewsMessage()
    }

    fun loadTeam() {
        val context = getApplication<Application>().applicationContext
        val teamList = PlayerRepository().getTeam(context)
        _team.value = teamList
    }

    fun addPlayerToTeam(player: Player) {
        player.isOnTeam = true
        teamManager.addPlayerToTeam(player, _team.value?.toMutableList() ?: mutableListOf())
        _team.value = teamManager.getTeam()
        saveTeam()
        updatePlayer(player) // Ensure the player's status is updated
    }

    fun removePlayerFromTeam(player: Player) {
        player.isOnTeam = false
        teamManager.removePlayerFromTeam(player, _team.value?.toMutableList() ?: mutableListOf())
        _team.value = teamManager.getTeam()
        saveTeam()
        updatePlayer(player) // Ensure the player's status is updated
    }

    fun savePlayers() {
        val context = getApplication<Application>().applicationContext
        _players.value?.let { playerList ->
            PlayerRepository().savePlayers(playerList, context)
        }
    }

    fun saveTeam() {
        val context = getApplication<Application>().applicationContext
        _team.value?.let { teamList ->
            PlayerRepository().saveTeam(teamList, context)
        }
    }

    fun calculateTeamScore(): Int {
        return _team.value?.sumOf { it.calculateTotalPoints() } ?: 0
    }

    // ---- Game Logic ----

    fun applyPointsForAction(player: Player, action: String) {
        when (action) {
            "Cow" -> player.cowCount += 1
            "Church" -> player.churchCount += 1
            "Water Tower" -> player.waterTowerCount += 1
        }
        playerManager.calculatePlayerPoints(player)
        updatePlayer(player)
    }

    fun updatePlayer(player: Player) {
        _players.value = _players.value?.map {
            if (it.id == player.id) player else it
        }
        savePlayers()
    }

    fun resetCalledObjects() {
        _players.value = _players.value?.map { player ->
            player.copy(cowCount = 0, churchCount = 0, waterTowerCount = 0)
        }
        savePlayers()  // Save updated players to the repository
    }

    // ---- Scavenger Hunt ----

    fun initializeScavengerHunt() {
        scavengerHuntManager.initializeHunt()
    }

    fun markScavengerItemAsFound(itemName: String, player: Player) {
        scavengerHuntManager.markItemAsFound(itemName, player)
        updatePlayer(player)
    }

    fun isScavengerHuntComplete(): Boolean {
        return scavengerHuntManager.isHuntComplete()
    }

    // ---- Power-Up Management ----

    fun activatePowerUp(player: Player, type: PowerUpType, duration: Long) {
        powerUpManager.activatePowerUp(player, type, duration)
        updatePlayer(player)
    }

    fun deactivatePowerUp(player: Player, type: PowerUpType) {
        powerUpManager.deactivatePowerUp(player, type)
        updatePlayer(player)
    }

    // ---- Penalty Management ----

    fun applyPenalty(player: Player, type: PenaltyType, duration: Long) {
        penaltyManager.applyPenalty(player, type, duration)
        updatePlayer(player)
    }

    fun isPlayerPenalized(player: Player): Boolean {
        return penaltyManager.isPlayerPenalized(player)
    }
}
