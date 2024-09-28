package com.example.cow_cow.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.cow_cow.models.Player

class TeamSelectionDialog {

    fun showTeamSelectionDialog(
        context: Context,
        players: List<Player>,
        onTeamUpdated: (List<Player>) -> Unit
    ) {
        val playerNames = players.map { it.name }.toTypedArray()
        val selectedPlayers = mutableListOf<Player>()
        val checkedItems = BooleanArray(players.size)

        AlertDialog.Builder(context)
            .setTitle("Select Players for the Team")
            .setMultiChoiceItems(playerNames, checkedItems) { _, which, isChecked ->
                val selectedPlayer = players[which]
                if (isChecked) {
                    selectedPlayers.add(selectedPlayer)
                } else {
                    selectedPlayers.remove(selectedPlayer)
                }
            }
            .setPositiveButton("Done") { dialog, _ ->
                onTeamUpdated(selectedPlayers)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
