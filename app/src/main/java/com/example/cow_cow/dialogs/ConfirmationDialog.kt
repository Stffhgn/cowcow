package com.example.cow_cow.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog

class ConfirmationDialog {

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Confirm") { dialog, _ ->
            positiveAction.invoke()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            negativeAction.invoke()
            dialog.dismiss()
        }
        builder.show()
    }
}
