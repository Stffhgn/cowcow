package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.CustomRule
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.repositories.PlayerRepository

class PlayerManager(private val playerRepository: PlayerRepository) {
    companion object {
        private val players = mutableListOf<Player>()

        /**
         * Get a list of all players.
         *
         * @return A list of all players currently in the game.
         */
        fun getAllPlayers(): List<Player> {
            return players
        }

        // Other companion methods...
    }

    private val players: MutableList<Player> = mutableListOf()
    private val teams: MutableList<Team> = mutableListOf()
    private val TAG = "PlayerManager"



    /**
     * Add a new player to the game.
     *
     * @param player The player object to be added.
     */
    fun addPlayer(player: Player) {
        playerRepository.addPlayer(player)
        Log.d(TAG, "Player added via PlayerManager: ${player.name}")
    }

    /**
     * Apply a custom rule to the player.
     *
     * @param player The player to whom the rule is being applied.
     * @param rule The rule to apply.
     */
    fun applyCustomRuleToPlayer(player: Player, rule: CustomRule) {
        Log.d(TAG, "Applying custom rule to player: ${player.name}, Rule: ${rule.ruleName}")

        if (!player.customRules.contains(rule)) {
            player.customRules.add(rule)
            Log.d(TAG, "Rule '${rule.ruleName}' successfully applied to ${player.name}")
        } else {
            Log.d(TAG, "Rule '${rule.ruleName}' is already applied to ${player.name}")
        }
    }


    /**
     * Add points to a player's score.
     *
     * @param player The player receiving points.
     * @param points The number of points to add.
     */
    fun addPointsToPlayer(player: Player, points: Int) {
        player.basePoints += points
        Log.d(TAG, "Added $points points to player ${player.name}. Total base points: ${player.basePoints}")

        // Recalculate the player's total points (including penalties, bonuses, etc.)
        calculatePlayerPoints(player)

        // Update the player in the repository if needed
        updatePlayer(player)
    }

    /**
     * Calculate the total points for the player using the ScoreManager.
     *
     * @param player The player whose points are being calculated.
     */
    fun calculatePlayerPoints(player: Player) {
        // Use ScoreManager to calculate the player's score.
        val totalPoints = ScoreManager.calculatePlayerScore(player)
        player.basePoints = totalPoints
        Log.d(TAG, "Calculated total points for player ${player.name}: $totalPoints")
    }

    //*

    fun removePlayer(playerId: String) {
        val player = playerRepository.getPlayers().find { it.id == playerId }
        player?.let {
            playerRepository.removePlayerById(player.id)
            Log.d(TAG, "Player removed via PlayerManager: ${it.name}")
        }
    }

    /**
     * Get a player by their ID.
     *
     * @param playerId The ID of the player to retrieve.
     * @return The player object, or null if not found.
     */
    fun getPlayerById(playerId: String): Player? {
        return players.find { it.id == playerId }
    }

    /**
     * Get a list of all players.
     *
     * @return A list of all players currently in the game.
     */
    fun getAllPlayers(): List<Player> {
        return playerRepository.getPlayers()
    }
    // Save a single player
    fun savePlayer(player: Player) {
        playerRepository.savePlayer(player)
    }

    /**
     * Retrieves all players who are on the team.
     *
     * @return A list of players who are on the team.
     */
    fun getTeamPlayers(): List<Player> {
        // Get all players
        val allPlayers = getAllPlayers()
        // Filter players who are on the team
        return allPlayers.filter { it.isOnTeam }
    }

    /**
     * Update a player's information.
     *
     * @param updatedPlayer The player object with updated information.
     */
    fun updatePlayer(updatedPlayer: Player) {
        Log.d(TAG, "Updating player via PlayerManager: ${updatedPlayer.name}")
        playerRepository.updatePlayer(updatedPlayer)
    }

    /**
     * Clear all players from the game.
     */
    fun clearPlayers() {
        players.clear()
        Log.d(TAG, "All players have been cleared from the game.")
    }

    // --- Team Management Functions ---

    /**
     * Add a new team.
     *
     * @param team The team object to be added.
     */
    fun addTeam(team: Team) {
        if (!teams.contains(team)) {
            teams.add(team)
            Log.d(TAG, "Team added: \${team.name}")
        }
    }

    /**
     * Add a player to a specific team.
     *
     * @param playerId The ID of the player to be added.
     * @param teamId The ID of the team the player is being added to.
     */
    fun addPlayerToTeam(playerId: String, teamId: String) {
        val player = getPlayerById(playerId)
        val team = teams.find { it.id == teamId }

        if (player != null && team != null && !team.members.contains(player)) {
            team.members.add(player)
            player.isOnTeam = true
            Log.d(TAG, "Player \${player.name} added to team \${team.name}")
        }
    }

    /**
     * Remove a player from a specific team.
     *
     * @param playerId The ID of the player to be removed.
     * @param teamId The ID of the team the player is being removed from.
     */
    fun removePlayerFromTeam(playerId: String, teamId: String) {
        val player = getPlayerById(playerId)
        val team = teams.find { it.id == teamId }

        if (player != null && team != null && team.members.contains(player)) {
            team.members.remove(player)
            player.isOnTeam = false
            Log.d(TAG, "Player \${player.name} removed from team \${team.name}")
        }
    }



    /**
     * Clear all teams.
     */
    fun clearTeams() {
        teams.clear()
        Log.d(TAG, "All teams have been cleared from the game.")
    }

    // --- Player Stats Management ---

    /**
     * Reset all player stats.
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
            Log.d(TAG, "Stats reset for player: \${player.name}")
        }
    }

    /**
     * Randomly assign players to teams.
     *
     * @param teamCount The number of teams to divide players into.
     */
    fun randomizeTeams(teamCount: Int) {
        clearTeams()
        val shuffledPlayers = players.shuffled()
        val teamsToCreate = (1..teamCount).map { Team(id = it.toString(), name = "Team \$it", members = mutableListOf()) }

        shuffledPlayers.forEachIndexed { index, player ->
            val team = teamsToCreate[index % teamCount]
            team.members.add(player)
            player.isOnTeam = true
        }
        teams.addAll(teamsToCreate)
        Log.d(TAG, "Players have been randomly assigned to \$teamCount teams.")
    }

    /**
     * Saves a list of players by iterating through each and using the repository.
     *
     * @param players The list of players to save.
     */
    fun savePlayers(players: List<Player>) {
        players.forEach { player ->
            playerRepository.savePlayer(player)
        }
        Log.d("PlayerManager", "All players saved to SharedPreferences.")
    }

    /**
     * Assign points to a player.
     *
     * @param playerId The ID of the player receiving points.
     * @param points The number of points to add.
     */
    fun assignPointsToPlayer(playerId: String, points: Int) {
        getPlayerById(playerId)?.let {
            it.basePoints += points
            Log.d(TAG, "Assigned \$points points to player: \${it.name}")
        }
    }
}