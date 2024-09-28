package com.example.cow_cow.dialogs

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class InputDialog {

    fun showInputDialog(
        context: Context,
        title: String,
        onInputSubmitted: (String) -> Unit
    ) {
        val input = EditText(context)

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Submit") { dialog, _ ->
                val userInput = input.text.toString()
                if (userInput.isNotEmpty()) {
                    onInputSubmitted(userInput)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
