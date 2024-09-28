package com.example.cow_cow.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.cow_cow.models.Player

class PlayerSelectionDialog {

    fun showPlayerSelectionDialog(
        context: Context,
        players: List<Player>,
        objectType: String,  // Could be "Cow", "Church", "Water Tower", etc.
        onPlayerSelected: (Player) -> Unit
    ) {
        val playerNames = players.map { it.name }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Who called the $objectType?")
            .setItems(playerNames) { _, which ->
                val selectedPlayer = players[which]
                onPlayerSelected(selectedPlayer)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
}
