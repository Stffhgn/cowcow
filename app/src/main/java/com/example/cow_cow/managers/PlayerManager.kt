package com.example.cow_cow.managers

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import java.util.*

object PlayerManager {

    private val players: MutableList<Player> = mutableListOf()
    private val teams: MutableList<Team> = mutableListOf()

    /**
     * Add a new player to the game.
     *
     * @param player The player object to be added.
     */
    fun addPlayer(player: Player) {
        if (!players.contains(player)) {
            players.add(player)
        }
    }

    /**
     * Remove a player from the game.
     *
     * @param playerId The ID of the player to be removed.
     */
    fun removePlayer(playerId: Int) {
        players.removeIf { it.id == playerId }
    }

    /**
     * Get a player by their ID.
     *
     * @param playerId The ID of the player to retrieve.
     * @return The player object, or null if not found.
     */
    fun getPlayerById(playerId: Int): Player? {
        return players.find { it.id == playerId }
    }

    /**
     * Get a list of all players.
     *
     * @return A list of all players currently in the game.
     */
    fun getAllPlayers(): List<Player> {
        return players
    }

    /**
     * Update a player's information.
     *
     * @param updatedPlayer The player object with updated information.
     */
    fun updatePlayer(updatedPlayer: Player) {
        val index = players.indexOfFirst { it.id == updatedPlayer.id }
        if (index != -1) {
            players[index] = updatedPlayer
        }
    }

    /**
     * Clear all players from the game (useful for resetting).
     */
    fun clearPlayers() {
        players.clear()
    }

    // --- Team Management Functions ---

    /**
     * Create a new team.
     *
     * @param team The team object to be added.
     */
    fun addTeam(team: Team) {
        if (!teams.contains(team)) {
            teams.add(team)
        }
    }

    /**
     * Add a player to a specific team.
     *
     * @param playerId The ID of the player to be added.
     * @param teamId The ID of the team the player is being added to.
     */
    fun addPlayerToTeam(playerId: Int, teamId: Int) {
        val player = getPlayerById(playerId)
        val team = teams.find { it.id == teamId }

        if (player != null && team != null && !team.members.contains(player)) {
            team.members.add(player)
            player.isOnTeam = true
        }
    }

    /**
     * Remove a player from a specific team.
     *
     * @param playerId The ID of the player to be removed.
     * @param teamId The ID of the team the player is being removed from.
     */
    fun removePlayerFromTeam(playerId: Int, teamId: Int) {
        val player = getPlayerById(playerId)
        val team = teams.find { it.id == teamId }

        if (player != null && team != null && team.members.contains(player)) {
            team.members.remove(player)
            player.isOnTeam = false
        }
    }

    /**
     * Get a list of all teams.
     *
     * @return A list of all teams currently in the game.
     */
    fun getAllTeams(): List<Team> {
        return teams
    }

    /**
     * Get a list of all players on a specific team.
     *
     * @param teamId The ID of the team.
     * @return A list of all players on the specified team.
     */
    fun getPlayersOnTeam(teamId: Int): List<Player>? {
        return teams.find { it.id == teamId }?.members
    }

    /**
     * Clear all teams (useful for resetting).
     */
    fun clearTeams() {
        teams.clear()
    }

    // --- Player Stats Management ---

    /**
     * Reset all player stats (useful for game resets).
     */
    fun resetAllPlayerStats() {
        players.forEach { player ->
            player.cowCount = 0
            player.churchCount = 0
            player.waterTowerCount = 0
            player.bonusPoints = 0
            player.penaltyPoints = 0
            player.basePoints = 0
            player.penalties.clear()
            player.achievements.clear()
            player.customRules.clear()
        }
    }

    /**
     * Get the player with the most points.
     *
     * @return The player with the highest total points.
     */
    fun getPlayerWithMostPoints(): Player? {
        return players.maxByOrNull { it.calculateTotalPoints() }
    }

    /**
     * Get the player with the most cows called.
     *
     * @return The player with the most cows called.
     */
    fun getPlayerWithMostCows(): Player? {
        return players.maxByOrNull { it.cowCount }
    }

    /**
     * Randomly assign players to teams.
     * This could be used to shuffle teams during team-based modes.
     *
     * @param teamCount The number of teams to divide players into.
     */
    fun randomizeTeams(teamCount: Int) {
        clearTeams()
        val shuffledPlayers = players.shuffled()
        val teamsToCreate = (1..teamCount).map { Team(id = it, members = mutableListOf()) }

        shuffledPlayers.forEachIndexed { index, player ->
            val team = teamsToCreate[index % teamCount]
            team.members.add(player)
            player.isOnTeam = true
        }
        teams.addAll(teamsToCreate)
    }

    /**
     * Assign points to a player.
     *
     * @param playerId The ID of the player receiving points.
     * @param points The number of points to add.
     */
    fun assignPointsToPlayer(playerId: Int, points: Int) {
        val player = getPlayerById(playerId)
        player?.let {
            it.basePoints += points
        }
    }

    /**
     * Example of triggering an event based on player stats.
     */
    fun triggerPlayerEvent(player: Player, event: String) {
        when (event) {
            "cow_called" -> player.cowCount += 1
            "church_called" -> player.churchCount += 1
            "water_tower_called" -> player.waterTowerCount += 1
            // More events as needed
        }
    }
}
