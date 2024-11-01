package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team
import com.example.cow_cow.repositories.PlayerRepository

class TeamManager(
    private val playerRepository: PlayerRepository,
    private val scoreManager: ScoreManager
) {

    private val TAG = "TeamManager"
    private var team: Team? = null  // Single instance of the team

    /**
     * Create a new team or retrieve the existing one.
     *
     * @param teamName Name of the team (used if creating a new team).
     * @return The existing or newly created Team object.
     */
    fun createOrGetTeam(teamName: String = "Default Team"): Team {
        if (team == null) {
            team = Team(id = "1", name = teamName, members = mutableListOf())
            Log.d(TAG, "Created new team with name: $teamName")
        }
        return team!!
    }

    /**
     * Add a player to the team.
     *
     * @param player The player to be added to the team.
     */
    fun addPlayerToTeam(player: Player) {
        val currentTeam = createOrGetTeam()
        if (!currentTeam.members.contains(player)) {
            currentTeam.addMember(player)
            player.isOnTeam = true
            updateTeamScore()
            Log.d(TAG, "Player ${player.name} added to team: ${currentTeam.name}")
        } else {
            Log.d(TAG, "Player ${player.name} is already in the team.")
        }
    }

    /**
     * Remove a player from the team.
     *
     * @param player The player to be removed.
     */
    fun removePlayerFromTeam(player: Player) {
        val currentTeam = createOrGetTeam()
        if (currentTeam.members.contains(player)) {
            currentTeam.removeMember(player)
            player.isOnTeam = false
            updateTeamScore()
            Log.d(TAG, "Player ${player.name} removed from team: ${currentTeam.name}")
        } else {
            Log.d(TAG, "Player ${player.name} is not in the team.")
        }
    }

    /**
     * Update the team score by calculating the sum of all member scores.
     */
    fun updateTeamScore() {
        team?.let {
            it.teamScore = it.members.sumOf { player -> scoreManager.calculatePlayerScore(player) }
            Log.d(TAG, "Updated team score: ${it.teamScore}")
        }
    }

    /**
     * Distribute points evenly across team members using ScoreManager.
     *
     * @param points The total points to be distributed.
     */
    fun distributeTeamPoints(points: Int) {
        val currentTeam = createOrGetTeam()
        if (currentTeam.members.isNotEmpty()) {
            val pointsPerPlayer = points / currentTeam.members.size
            Log.d(TAG, "Distributing $points points among ${currentTeam.members.size} players ($pointsPerPlayer points each).")
            currentTeam.members.forEach { player ->
                scoreManager.addPointsToPlayer(player, pointsPerPlayer)
            }
            updateTeamScore()
        } else {
            Log.d(TAG, "No players in the team to distribute points.")
        }
    }

    /**
     * Reset the team by clearing members and resetting the score.
     */
    fun resetTeam() {
        team?.let {
            it.members.clear()
            it.teamScore = 0
            Log.d(TAG, "Team ${it.name} has been reset.")
        }
    }

    /**
     * Check if a player is part of the team.
     *
     * @param player The player to check.
     * @return True if the player is part of the team, false otherwise.
     */
    fun isPlayerInTeam(player: Player): Boolean {
        return team?.members?.contains(player) ?: false
    }
}
