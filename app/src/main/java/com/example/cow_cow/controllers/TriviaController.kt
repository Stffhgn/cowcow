package com.example.cow_cow.controllers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.cow_cow.R
import com.example.cow_cow.dialogs.TriviaDialogFragment
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.uiStuff.FlexboxContainer
import com.google.android.flexbox.FlexboxLayout

class TriviaController(
    private val context: Context,
    val flexboxContainer: FlexboxContainer,
    private val triviaManager: TriviaManager
) {
    private val TAG = "TriviaController"
    private var travelingSalesmanStep = 0 // Variable to track the "traveling salesman"

    init {
        Log.d(TAG, "Initializing TriviaController.")
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Initialized TriviaController.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        loadTriviaButton()
    }

    fun loadTriviaButton() {
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Loading Trivia Button.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval

        // Clear only the existing trivia button
        clearTriviaButton()

        // Fetch the current trivia question
        val triviaQuestion = triviaManager.getCurrentQuestion() ?: return
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Current trivia question: ${triviaQuestion.questionText}")

        // Create and add the new trivia button to the container
        val triviaButton = createTriviaButton(triviaQuestion)
        flexboxContainer.addView(triviaButton)
        flexboxContainer.requestLayout()
    }

    /**
     * Creates a trivia ImageButton with a specific category and onClick listener.
     */
    fun createTriviaButton(triviaQuestion: TriviaQuestion): ImageButton {
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Creating Trivia Button.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        return ImageButton(context).apply {
            id = View.generateViewId()
            tag = "triviaButton"
            contentDescription = "Trivia Button: ${triviaQuestion.category.displayName}"
            setImageDrawable(getTriviaIcon())

            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }

            // Consistent button styling
            background = ContextCompat.getDrawable(context, R.drawable.rounded_button_background)
            backgroundTintList = ContextCompat.getColorStateList(context, triviaQuestion.category.colorResId)
            setPadding(32, 32, 32, 32)

            scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageTintList = ContextCompat.getColorStateList(context, android.R.color.white)

            setOnClickListener {
                travelingSalesmanStep++
                Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Trivia Button clicked.")
                triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
                openTriviaDialog(triviaQuestion)
            }
        }
    }

    /**
     * Opens the TriviaDialogFragment with the selected trivia question.
     */
    private fun openTriviaDialog(triviaQuestion: TriviaQuestion) {
        val fragmentActivity = context as? FragmentActivity
        if (fragmentActivity == null) {
            Log.e(TAG, "Context is not a FragmentActivity. Cannot open TriviaDialogFragment.")
            return
        }

        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Opening Trivia Dialog.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        fragmentActivity.supportFragmentManager.let { fragmentManager ->
            TriviaDialogFragment.newInstance(triviaManager, this, flexboxContainer)
                .show(fragmentManager, TriviaDialogFragment.TAG)
        }
    }

    /**
     * Retrieves the trivia icon drawable.
     */
    private fun getTriviaIcon(): Drawable? {
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Getting Trivia Icon.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        return ContextCompat.getDrawable(context, R.drawable.ic_cow_cow_church)
    }

    /**
     * Clears all trivia questions from the container.
     */
    private fun clearAllTrivia() {
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Clearing all trivia questions.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        flexboxContainer.removeAllViews()
    }

    /**
     * Clears the trivia button from the container.
     */
    fun clearTriviaButton() {
        travelingSalesmanStep++
        Log.d(TAG, "Traveling Salesman Step: $travelingSalesmanStep - Clearing Trivia Button.")
        triviaManager.getTravelingSalesmenLog() // Verify the log retrieval
        for (i in flexboxContainer.childCount - 1 downTo 0) {
            val view = flexboxContainer.getChildAt(i)
            if (view.tag?.toString() == "triviaButton") {
                flexboxContainer.removeViewAt(i)
            }
        }
    }
}
