package com.example.cow_cow.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.cow_cow.R
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.TriviaQuestion
import android.util.Log
import com.example.cow_cow.controllers.TriviaController

class TriviaDialogFragment(
    private val triviaManager: TriviaManager,
    private val triviaController: TriviaController,
    private val flexboxContainer: ViewGroup,
) : DialogFragment() {
    private var currentQuestion: TriviaQuestion? = null
    private val TAG = "TriviaDialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_trivia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the current question from the manager or request a new one
        currentQuestion = triviaManager.getCurrentQuestion()
        if (currentQuestion == null) {
            triviaManager.loadNextQuestion()
            currentQuestion = triviaManager.getCurrentQuestion()
        }

        if (currentQuestion == null) {
            Toast.makeText(requireContext(), "No more trivia questions available.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // Set question text
        val questionTextView: TextView = view.findViewById(R.id.questionTextView)
        questionTextView.text = currentQuestion!!.questionText
        Log.d(TAG, "Displaying question: ${currentQuestion!!.questionText}")

        // Initialize answer buttons
        val answerButtons = listOf(
            view.findViewById<Button>(R.id.answerButton1),
            view.findViewById<Button>(R.id.answerButton2),
            view.findViewById<Button>(R.id.answerButton3),
            view.findViewById<Button>(R.id.answerButton4)
        )

        // Shuffle and set answers to buttons
        val shuffledAnswers = currentQuestion!!.possibleAnswers.shuffled()
        Log.d(TAG, "Shuffled answers: $shuffledAnswers")

        answerButtons.forEachIndexed { index, button ->
            button.text = shuffledAnswers[index]
            Log.d(TAG, "Setting button ${index + 1} text to: ${shuffledAnswers[index]}")
            button.setOnClickListener { checkAnswer(shuffledAnswers[index]) }
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        Log.d(TAG, "Selected answer: $selectedAnswer")
        currentQuestion?.let { question ->
            val isCorrect = triviaManager.validateAnswer(selectedAnswer)

            if (isCorrect) {
                Toast.makeText(requireContext(), "Correct! +${question.points} points", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Answer is correct.")

            } else {
                Toast.makeText(requireContext(), "Incorrect! The correct answer is ${question.correctAnswer}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Answer is incorrect. Correct answer: ${question.correctAnswer}")
            }

            // Close the dialog to return to the main game activity where the button will be reloaded
            dismiss()
        }
    }


        /**
     * Clears the trivia button from the container.
     */
    private fun clearTriviaButton() {
        for (i in flexboxContainer.childCount - 1 downTo 0) {
            val view = flexboxContainer.getChildAt(i)
            if (view.tag?.toString() == "triviaButton") {
                flexboxContainer.removeViewAt(i)
            }
        }
    }

    companion object {
        const val TAG = "TriviaDialogFragment"

        fun newInstance(triviaManager: TriviaManager,triviaController: TriviaController, flexboxContainer: ViewGroup): TriviaDialogFragment {
            return TriviaDialogFragment(triviaManager, triviaController, flexboxContainer)
        }
    }
}
