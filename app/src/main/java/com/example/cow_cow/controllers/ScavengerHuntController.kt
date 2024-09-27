package com.example.cow_cow.controllers

import com.example.cow_cow.models.Player
import com.example.cow_cow.viewmodel.GameViewModel

class ScavengerHuntController(private val gameViewModel: GameViewModel) {

    fun startScavengerHunt() {
        gameViewModel.initializeScavengerHuntItems()
        gameViewModel.startScavengerHuntTimer(300000L) // e.g., 5 minutes
    }

    fun markItemAsFound(itemName: String, player: Player): Boolean {
        return gameViewModel.markScavengerItemAsFound(itemName, player)
    }

    fun resetScavengerHunt() {
        gameViewModel.initializeScavengerHuntItems()  // Reset items for a new scavenger hunt
    }
}