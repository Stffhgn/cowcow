package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.repositories.PlayerRepository

class PlayerManager(private val playerRepository: PlayerRepository) {

    private val players = mutableListOf<Player>()
    private val teams = mutableListOf<Team>()
    private val TAG = "PlayerManager"

    /**
     * Initializes players by loading them from the repository.
     */
    fun loadAllPlayers() {
        players.clear()
        players.addAll(playerRepository.getPlayers())
        Log.d(TAG, "Loaded ${players.size} players from repository.")
    }

    /**
     * Gets a list of all players.
     */
    fun getAllPlayers(): List<Player> = players

    /**
     * Retrieves a player by ID.
     */
    fun getPlayerById(playerId: String): Player? = players.find { it.id == playerId }

    /**
     * Saves a single player to the repository.
     */
    fun savePlayer(player: Player) {
        playerRepository.savePlayer(player)
        Log.d(TAG, "Saved player ${player.name} to repository.")
    }

    // Function to remove a player from the repository and the current list
    fun removePlayer(player: Player) {
        // Remove the player from the repository
        playerRepository.deletePlayer(player)

        // Log the removal
        Log.d(TAG, "Player ${player.name} removed from repository and player list.")
    }

    /**
     * Clears all players.
     */
    fun clearPlayers() {
        players.clear()
        Log.d(TAG, "All players have been cleared from the game.")
    }

    // --- Instance Methods ---

    /**
     * Adds a player and saves it to the repository.
     */
    fun addPlayer(player: Player) {
        players.add(player)
        savePlayer(player)
        Log.d(TAG, "Player added and saved: ${player.name}")
    }

    /**
     * Updates player data and saves it to the repository.
     */
    fun updatePlayer(player: Player) {
        savePlayer(player)
        Log.d(TAG, "Player updated and saved: ${player.name}")
    }

    /**
     * Resets all player stats and saves the reset state.
     */
    fun resetAllPlayerStats() {
        players.forEach { player ->
            player.apply {
                cowCount = 0
                churchCount = 0
                waterTowerCount = 0
                bonusPoints = 0
                penaltyPoints = 0
                basePoints = 0
                penalties.clear()
                achievements.clear()
                customRules.clear()
            }
            savePlayer(player)
            Log.d(TAG, "Stats reset for player: ${player.name}")
        }
    }

    // --- Team Management Functions ---

    /**
     * Adds a new team if it doesn't already exist.
     */
    fun addTeam(team: Team) {
        if (!teams.contains(team)) {
            teams.add(team)
            Log.d(TAG, "Team added: ${team.name}")
        }
    }

    /**
     * Adds a player to a specific team and updates their team status.
     */
    fun addPlayerToTeam(playerId: String, teamId: String) {
        val player = getPlayerById(playerId)
        val team = teams.find { it.id == teamId }
        if (player != null && team != null && !team.members.contains(player)) {
            team.members.add(player)
            player.isOnTeam = true
            savePlayer(player)
            Log.d(TAG, "Player ${player.name} added to team ${team.name}")
        }
    }

    /**
     * Retrieves a list of players who are part of a team.
     *
     * @return List of players on the team.
     */
    fun getTeamPlayers(): List<Player> {
        val allPlayers = playerRepository.getPlayers() // Retrieve all players from the repository
        val teamPlayers = allPlayers.filter { it.isOnTeam } // Filter players who are marked as being on a team

        Log.d("PlayerManager", "Retrieved ${teamPlayers.size} players on the team.")
        return teamPlayers
    }


    /**
     * Randomly assigns players to a specified number of teams.
     */
    fun randomizeTeams(teamCount: Int) {
        clearTeams()
        val shuffledPlayers = players.shuffled()
        val teamsToCreate = (1..teamCount).map { Team(id = it.toString(), name = "Team $it", members = mutableListOf()) }

        shuffledPlayers.forEachIndexed { index, player ->
            val team = teamsToCreate[index % teamCount]
            team.members.add(player)
            player.isOnTeam = true
            savePlayer(player)
        }
        teams.addAll(teamsToCreate)
        Log.d(TAG, "Players have been randomly assigned to $teamCount teams.")
    }

    /**
     * Clears all teams.
     */
    fun clearTeams() {
        teams.clear()
        Log.d(TAG, "All teams have been cleared from the game.")
    }
}
