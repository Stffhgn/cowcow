package com.example.cowcow.repositories

import com.example.cow_cow.models.Game
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Team

class GameRepository {

    private var game: Game? = null

    fun startNewGame(players: MutableList<Player>) {
        game = Game(players)
    }

    fun getCurrentGame(): Game? {
        return game
    }

    fun setTeam(team: Team) {
        game?.team = team
    }

    fun endGame() {
        game = null
    }
}