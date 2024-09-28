package com.example.cow_cow.utils

import com.example.cow_cow.models.Player

object PlayerUtils {

    /**
     * Adds to the player's cow count.
     */
    fun addCowToPlayer(player: Player) {
        player.cowCount += 1
    }

    /**
     * Adds to the player's church count.
     */
    fun addChurchToPlayer(player: Player) {
        player.churchCount += 1
    }

    /**
     * Adds to the player's water tower count.
     */
    fun addWaterTowerToPlayer(player: Player) {
        player.waterTowerCount += 1
    }

    /**
     * Resets a player's counts (cow, church, water tower).
     */
    fun resetPlayerCounts(player: Player) {
        player.cowCount = 0
        player.churchCount = 0
        player.waterTowerCount = 0
    }

    /**
     * Resets all players' counts.
     */
    fun resetAllPlayersCounts(players: List<Player>) {
        players.forEach { resetPlayerCounts(it) }
    }

    /**
     * Returns the player with the most cows.
     */
    fun getPlayerWithMostCows(players: List<Player>): Player? {
        return players.maxByOrNull { it.cowCount }
    }

    /**
     * Returns the player with the highest total score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        return players.maxByOrNull { it.calculateTotalPoints() }
    }
}
