package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.viewmodel.GameViewModel

class MainGameController(private val gameViewModel: GameViewModel) {

    fun handleCowCalled(player: Player) {
        gameViewModel.applyPointsForAction(player, "Cow")
    }

    fun handleChurchCalled(player: Player) {
        gameViewModel.applyPointsForAction(player, "Church")
    }

    fun handleWaterTowerCalled(player: Player) {
        gameViewModel.applyPointsForAction(player, "Water Tower")
    }

    fun resetMainGame() {
        gameViewModel.resetCalledObjects()
    }
}