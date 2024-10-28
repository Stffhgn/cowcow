package com.example.cow_cow.controllers

import android.util.Log
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.TeamManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.repositories.TeamRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.utils.TeamUtils
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.viewModels.ScoreViewModel

class TeamController(
    private val gameViewModel: GameViewModel,
    private val scoreViewModel: ScoreViewModel,
    private val teamRepository: TeamRepository,
    private val playerManager: PlayerManager,
    private val teamManager: TeamManager,
) {
    private var currentTeam: Team? = null

    /**
     * Initializes game data, including setting up teams and players.
     */
    fun initializeGameData() {
        // Create or retrieve the existing team
        currentTeam = teamManager.createOrGetTeam()

        // Retrieve all players and add those who are marked as being on the team
        playerManager.getAllPlayers().forEach { player ->
            if (player.isOnTeam) {
                teamManager.addPlayerToTeam(player)
                Log.d("TeamController", "Player ${player.name} added to team: ${currentTeam?.name}")
            }
        }

        // Ensure the team is not null before adding it to the ViewModel
        currentTeam?.let {
            gameViewModel.addTeam(it)
            Log.d("TeamController", "Game data initialized with team: ${it.name} and ${it.members.size} players.")
        } ?: Log.e("TeamController", "Failed to initialize game data: currentTeam is null.")
    }

    /**
     * Creates a new team and adds it to the repository.
     */
    private fun createNewTeam(): Team {
        val newTeam = Team(
            id = PlayerIDGenerator.generatePlayerID(),
            name = TeamUtils.generateRandomTeamName()
        )

        // Save to repository and update the game view model
        teamRepository.addTeam(newTeam)
        gameViewModel.addTeam(newTeam)
        Log.d("TeamController", "New team created: ${newTeam.name}")
        return newTeam
    }

    /**
     * Removes the current team from the repository and resets the state.
     */
    private fun removeCurrentTeam() {
        currentTeam?.let {
            teamManager.resetTeam()
            gameViewModel.removeTeam()
            Log.d("TeamController", "Team ${it.name} has been removed as it has no players left.")
        }
        currentTeam = null
    }

    /**
     * Adds a player to the team.
     */
    fun addPlayerToTeam(player: Player): Boolean {
        if (player.isOnTeam) {
            Log.d("TeamController", "Player ${player.name} is already on the team.")
            return false
        }

        // Create a new team if it doesn't exist
        if (currentTeam == null) {
            currentTeam = createNewTeam()
        }

        teamManager.addPlayerToTeam(player)
        gameViewModel.addPlayerToTeam(player)
        Log.d("TeamController", "Player ${player.name} added to team ${currentTeam?.name}")
        updateTeamScore()
        return true
    }

    /**
     * Toggles the player's team status.
     */
    fun togglePlayerTeamStatus(player: Player) {
        if (player.isOnTeam) {
            removePlayerFromTeam(player)
        } else {
            addPlayerToTeam(player)
        }
        updateTeamScore()
    }

    /**
     * Removes a player from the team.
     */
    fun removePlayerFromTeam(player: Player): Boolean {
        if (!player.isOnTeam) {
            Log.d("TeamController", "Player ${player.name} is not on the team.")
            return false
        }

        teamManager.removePlayerFromTeam(player)
        gameViewModel.removePlayerFromTeam(player)
        Log.d("TeamController", "Player ${player.name} removed from team ${currentTeam?.name}")

        // Remove the team if no players are left
        if (teamManager.createOrGetTeam().members.isEmpty()) {
            removeCurrentTeam()
        } else {
            updateTeamScore()
        }
        return true
    }

    /**
     * Update the score for the team.
     */
    fun updateTeamScore(): Int {
        teamManager.updateTeamScore()
        scoreViewModel.calculateTeamScore()
        val teamScore = scoreViewModel.teamScore.value ?: 0
        Log.d("TeamController", "Team score updated: $teamScore")
        return teamScore
    }

    /**
     * Get the list of players who are currently on the team.
     */
    fun getTeamPlayers(): List<Player> {
        return gameViewModel.team.value?.members ?: emptyList()
    }

    /**
     * Get the list of individual players who are not on the team.
     */
    fun getIndividualPlayers(): List<Player> {
        return gameViewModel.players.value?.filter { !it.isOnTeam } ?: emptyList()
    }

    /**
     * Checks if all players are on a team.
     */
    fun areAllPlayersOnTeam(): Boolean {
        return gameViewModel.players.value?.all { it.isOnTeam } ?: false
    }

    /**
     * Checks if a player is on the team.
     */
    fun isPlayerOnTeam(player: Player): Boolean {
        return player.isOnTeam
    }
}
