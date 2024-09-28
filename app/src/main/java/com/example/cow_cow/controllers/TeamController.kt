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
        gameViewModel.addToTeam(player)
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
        gameViewModel.removeFromTeam(player)
        return true
    }

    /**
     * Calculate and return the current team score.
     */
    fun updateTeamScore(): Int {
        return gameViewModel.calculateTeamScore()
    }

    /**
     * Get the list of players who are currently on the team.
     */
    fun getTeamPlayers(): List<Player> {
        return gameViewModel.players.value?.filter { it.isOnTeam } ?: emptyList()
    }

    /**
     * Get the list of individual players who are not on the team.
     */
    fun getIndividualPlayers(): List<Player> {
        return gameViewModel.getIndividualPlayers()
    }

    /**
     * Checks if a player is on the team.
     */
    fun isPlayerOnTeam(player: Player): Boolean {
        return player.isOnTeam
    }
}
