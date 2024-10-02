package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.GameViewModel

class TeamController(private val gameViewModel: GameViewModel) {

    /**
     * Add a player to the team.
     * If the player is already on the team, this method ensures no duplicate entries are added.
     */
    fun addPlayerToTeam(player: Player): Boolean {
        if (player.isOnTeam) {
            return false // Player is already on the team
        }
        gameViewModel.addPlayerToTeam(player)  // Updated to call the correct method
        return true
    }

    /**
     * Remove a player from the team.
     * If the player is not on the team, no action will be taken.
     */
    fun removePlayerFromTeam(player: Player): Boolean {
        if (!player.isOnTeam) {
            return false // Player is not on the team
        }
        gameViewModel.removePlayerFromTeam(player)  // Updated to call the correct method
        return true
    }

    /**
     * Calculate and return the current team score.
     */
    fun updateTeamScore(): Int {
        return gameViewModel.calculateTeamScore()  // Assuming you have this function in your ViewModel
    }

    /**
     * Get the list of players who are currently on the team.
     */
    fun getTeamPlayers(): List<Player> {
        return gameViewModel.team.value ?: emptyList()
    }

    /**
     * Get the list of individual players who are not on the team.
     */
    fun getIndividualPlayers(): List<Player> {
        return gameViewModel.players.value?.filter { !it.isOnTeam } ?: emptyList()
    }

    /**
     * Checks if a player is on the team.
     */
    fun isPlayerOnTeam(player: Player): Boolean {
        return player.isOnTeam
    }
}