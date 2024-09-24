package com.example.cow_cow.car

import android.content.Intent
import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.utils.LogTags.TAG
import com.example.cow_cow.models.Player

import com.example.cow_cow.viewmodel.GameViewModel

class MySession : Session() {

    private val gameViewModel = GameViewModel()
    private val TAG="MySession"

    override fun onCreateScreen(intent: Intent): Screen {
        Log.d(TAG, "onCreateScreen called in MySession")
        return GameScreen(carContext)
    }

    private inner class GameScreen(carContext: CarContext) : Screen(carContext) {
        override fun onGetTemplate(): Template {

            val pane = Pane.Builder()
                .addRow(
                    Row.Builder().setTitle("Cow Button")
                        .addText("Press to select Cow (1 point)")
                        .setOnClickListener {
                            // Show player selection to add points for the Cow button
                            showPlayerSelection(1)
                        }.build()
                )
                .addRow(
                    Row.Builder().setTitle("Church Button")
                        .addText("Press to select Church (2 points)")
                        .setOnClickListener {
                            // Show player selection to add points for the Church button
                            showPlayerSelection(2)
                        }.build()
                )
                .addRow(
                    Row.Builder().setTitle("Water Tower Button")
                        .addText("Press to select Water Tower (3 points)")
                        .setOnClickListener {
                            // Show player selection to add points for the Water Tower button
                            showPlayerSelection(3)
                        }.build()
                )
                .build()

            return PaneTemplate.Builder(pane)
                .setTitle("Game Options")
                .setActionStrip(
                    ActionStrip.Builder()
                        .addAction(
                            Action.Builder()
                                .setTitle("Back")
                                .setOnClickListener {
                                    // Handle Back button
                                }
                                .build()
                        )
                        .build()
                )
                .build()
        }

        // Show the list of players to select which player gets the points
        private fun showPlayerSelection(points: Int) {
            val players: List<Player> = mutableListOf() // Assume this returns a list of player names

// Create the rows for each player
            val rows = gameViewModel.players.map { player ->
                Row.Builder()
                    .setTitle(player.name) // Assuming player has a 'name' property
                    .setOnClickListener {
                        // Handle player selection here
                    }
                    .build()
            }

            // Show the player selection template
            carContext.getCarService(ScreenManager::class.java)
                .push(
                    object : Screen(carContext) {
                        override fun onGetTemplate(): Template {
                            return ListTemplate.Builder()
                                .setTitle("Select Player")
                                .setSingleList(
                                    ItemList.Builder().apply {
                                        rows.forEach { addItem(it) }
                                    }.build()
                                )
                                .build()
                        }
                    }
                )
        }

        // Update the player's points display (optional)
        private fun updatePlayerPointsDisplay(playerName: String, points: Int) {
            val totalPoints = gameViewModel.getPlayerPoints(playerName)
            carContext.getCarService(ScreenManager::class.java)
                .push(
                    object : Screen(carContext) {
                        override fun onGetTemplate(): Template {
                            return MessageTemplate.Builder("$playerName now has $totalPoints points.")
                                .setTitle("Player Points Updated")
                                .setActionStrip(
                                    ActionStrip.Builder()
                                        .addAction(
                                            Action.Builder()
                                                .setTitle("OK")
                                                .setOnClickListener {
                                                    // Handle OK button click
                                                }
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        }
                    }
                )
        }
    }
}
