package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.viewmodel.GameViewModel

class TeamController(private val gameViewModel: GameViewModel) {

    fun addPlayerToTeam(player: Player) {
        gameViewModel.addToTeam(player)
    }

    fun removePlayerFromTeam(player: Player) {
        gameViewModel.removeFromTeam(player)
    }

    fun updateTeamScore(): Int {
        return gameViewModel.calculateTeamScore()
    }

    fun getTeamPlayers(): List<Player> {
        return gameViewModel.players.value?.filter { it.isOnTeam } ?: emptyList()
    }

    fun getIndividualPlayers(): List<Player> {
        return gameViewModel.getIndividualPlayers()
    }
}