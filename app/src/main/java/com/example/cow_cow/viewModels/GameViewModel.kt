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

    fun loadTeam() {
        val context = getApplication<Application>().applicationContext
        val teamList = PlayerRepository().getTeam(context)
        _team.value = teamList
    }

    fun addPlayerToTeam(player: Player) {
        teamManager.addPlayerToTeam(player, _team.value?.toMutableList() ?: mutableListOf())
        _team.value = teamManager.getTeam()
        saveTeam()
    }

    fun removePlayerFromTeam(player: Player) {
        teamManager.removePlayerFromTeam(player, _team.value?.toMutableList() ?: mutableListOf())
        _team.value = teamManager.getTeam()
        saveTeam()
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
