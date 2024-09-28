package com.example.cow_cow.controllers

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cow_cow.R

class UIController(private val context: Context) {

    /**
     * Displays a simple toast message.
     *
     * @param message The message to be displayed in the toast.
     * @param duration The duration of the toast (Toast.LENGTH_SHORT or Toast.LENGTH_LONG).
     */
    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    /**
     * Displays a confirmation dialog with customizable actions.
     *
     * @param title The title of the dialog.
     * @param message The message in the dialog body.
     * @param positiveAction The action for the "Yes" button.
     * @param negativeAction The action for the "No" button.
     */
    fun showConfirmationDialog(
        title: String,
        message: String,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                positiveAction?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                negativeAction?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Displays a simple information dialog.
     *
     * @param title The title of the dialog.
     * @param message The message in the dialog body.
     */
    fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * Updates a TextView with a new string and sets its visibility to visible.
     *
     * @param textView The TextView to be updated.
     * @param text The text to be set on the TextView.
     */
    fun updateTextView(textView: TextView, text: String) {
        textView.text = text
        textView.visibility = View.VISIBLE
    }

    /**
     * Toggles the visibility of a View.
     *
     * @param view The view to be toggled.
     */
    fun toggleViewVisibility(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    /**
     * Handles showing a loading spinner and hiding it when necessary.
     *
     * @param spinner The spinner view to be shown or hidden.
     * @param show Whether to show or hide the spinner.
     */
    fun toggleLoadingSpinner(spinner: View, show: Boolean) {
        spinner.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Sets a click listener on a View.
     *
     * @param view The view that should handle click events.
     * @param onClickAction The action to be executed when the view is clicked.
     */
    fun setClickListener(view: View, onClickAction: () -> Unit) {
        view.setOnClickListener {
            onClickAction()
        }
    }
}
