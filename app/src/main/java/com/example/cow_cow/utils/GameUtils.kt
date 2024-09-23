package com.example.cow_cow.utils

import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

object GameUtils {

    // Function to add counts to a player based on action
    fun addCowToPlayer(player: Player) {
        player.cowCount += 1
    }

    fun addChurchToPlayer(player: Player) {
        player.churchCount += 1
    }

    fun addWaterTowerToPlayer(player: Player) {
        player.waterTowerCount += 1
    }

    // Function to add a player to a team
    fun addPlayerToTeam(player: Player, team: Team) {
        if (!team.members.contains(player)) {
            team.members.add(player)
            updateTeamScore(team)
        }
    }

    // Function to remove a player from a team
    fun removePlayerFromTeam(player: Player, team: Team) {
        if (team.members.contains(player)) {
            team.members.remove(player)
            updateTeamScore(team)
        }
    }

    // Function to update the total team score
    private fun updateTeamScore(team: Team) {
        team.teamScore = team.members.sumOf { it.totalScore }
    }

    // Function to distribute points evenly among team members
    fun distributeTeamPoints(team: Team, totalPoints: Int) {
        val pointsPerPlayer = totalPoints / team.members.size
        for (player in team.members) {
            // Decide how to distribute points to counts
            // For simplicity, we'll add points to cowCount
            player.cowCount += pointsPerPlayer
        }
        updateTeamScore(team)
    }

    // Function to reset a player's counts
    fun resetPlayerCounts(player: Player) {
        player.cowCount = 0
        player.churchCount = 0
        player.waterTowerCount = 0
    }

    // Function to reset all players' counts
    fun resetAllPlayersCounts(players: List<Player>) {
        for (player in players) {
            resetPlayerCounts(player)
        }
    }

    // Function to determine which player called the most "Cows"
    fun getPlayerWithMostCows(players: List<Player>): Player? {
        return players.maxByOrNull { it.cowCount }
    }

    // Function to get the player with the highest total score
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        return players.maxByOrNull { it.totalScore }
    }
}
