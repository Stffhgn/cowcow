package com.example.cow_cow.utils

import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player

object ScoreUtils {

    /**
     * Returns the player with the highest total score.
     */
    fun getPlayerWithHighestScore(players: List<Player>): Player? {
        return players.maxByOrNull { ScoreManager.calculatePlayerScore(it) }
    }

    /**
     * Returns the player with the most cows.
     */
    fun getPlayerWithMostCows(players: List<Player>): Player? {
        return players.maxByOrNull { it.cowCount }
    }
}
