package com.example.cow_cow.managers

import android.util.Log
import androidx.core.view.children
import com.example.cow_cow.controllers.TriviaController
import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.TriviaRepository
import com.example.cow_cow.utils.TriviaQuestionGenerator

class TriviaManager(
    private val repository: TriviaRepository,
    private val scoreManager: ScoreManager,
    private val players: List<Player>,
    private var triviaController: TriviaController? = null
) {

    private var currentQuestion: TriviaQuestion? = null
    private var totalCorrectAnswers: Int = 0
    private var totalIncorrectAnswers: Int = 0
    private var playerProgress: MutableMap<Player, Int> = mutableMapOf()
    private var gameActive: Boolean = true // Set to true to indicate the game is active
    private var travelingSalesman: String = "Starting journey"
    private val TAG = "TriviaManager"

    init {
        updateSalesmanRoute("Init called")
        Log.d(TAG, "Initializing TriviaManager and loading the first question. Salesman route: $travelingSalesman")
        loadNextQuestion() // Load the first question on initialization
    }

    // Method to set the triviaController after initialization
    fun setTriviaController(controller: TriviaController) {
        this.triviaController = controller
    }

    /**
     * Load the next trivia question.
     * If there are no more questions, end the game.
     */
    fun loadNextQuestion() {
        updateSalesmanRoute("loadNextQuestion called")
        if (!gameActive) {
            Log.d(TAG, "Game is not active. Cannot load next question.")
            return
        }

        // Check if there are unused questions in the repository
        if (!repository.hasUnusedQuestions()) {
            Log.d(TAG, "No unused questions available. Reloading the question set.")
            repository.reloadQuestions(TriviaQuestionGenerator.generateQuestionSet())
        }

        // Load the next unused question from the repository
        val nextQuestion = repository.loadNextUnusedQuestion()
        nextQuestion?.let {
            currentQuestion = it
            Log.d(TAG, "Loaded new trivia question: ${it.questionText}. Salesman route: $travelingSalesman")
        } ?: run {
            Log.d(TAG, "No new questions could be loaded. Ending game.")
            endTriviaGame()
        }
    }

    /**
     * Get the current question.
     */
    fun getCurrentQuestion(): TriviaQuestion? {
        updateSalesmanRoute("getCurrentQuestion called")
        Log.d(
            TAG,
            "Fetching current question: ${currentQuestion?.questionText ?: "No question available"}. Salesman route: $travelingSalesman"
        )
        return currentQuestion
    }

    /**
     * Validate the answer and update the players' scores based on the result.
     */
    fun validateAnswer(selectedAnswer: String): Boolean {
        updateSalesmanRoute("validateAnswer called")
        currentQuestion?.let { question ->
            val isCorrect = question.correctAnswer.trim().equals(selectedAnswer.trim(), ignoreCase = true)

            Log.d(
                TAG,
                "Validating answer. Selected: '$selectedAnswer', Correct: '${question.correctAnswer}', Is correct: $isCorrect. Salesman route: $travelingSalesman"
            )

            if (isCorrect) {
                handleCorrectAnswer(question)
            } else {
                handleIncorrectAnswer(question)
            }

            return isCorrect
        }
        updateSalesmanRoute("No current question to validate")
        Log.d(TAG, "No current question to validate against. Salesman route: $travelingSalesman")
        return false
    }

    /**
     * Handle logic for a correct answer.
     */
    private fun handleCorrectAnswer(question: TriviaQuestion) {
        updateSalesmanRoute("Answer correct")
        players.forEach { player ->
            scoreManager.addPointsToPlayer(player, question.points)
            Log.d(
                TAG,
                "Added ${question.points} points to player: ${player.name}. Salesman route: $travelingSalesman"
            )
        }
        totalCorrectAnswers++
        question.used = true // Mark the question as used

        repository.removeQuestion(question) // Remove the question from the repository
        currentQuestion = null // Clear currentQuestion to avoid reuse

        // Update the UI
        triviaController?.let {
            it.clearTriviaButton()
            loadNextQuestion() // Load the next question
            val newQuestion = getCurrentQuestion()
            if (newQuestion != null) {
                it.loadTriviaButton()
            } else {
                Log.d(TAG, "No more questions available. Ending game.")
                endTriviaGame()
            }
        }
    }

    /**
     * Handle logic for an incorrect answer.
     */
    private fun handleIncorrectAnswer(question: TriviaQuestion) {
        updateSalesmanRoute("Answer incorrect")
        totalIncorrectAnswers++
        Log.d(
            TAG,
            "Answer was incorrect. Showing explanation: ${question.explanation}. Salesman route: $travelingSalesman"
        )

        if (question.timesUsed == 0) {
            question.timesUsed++
            Log.d(
                TAG,
                "Incremented timesUsed for the question and saved to repository. Salesman route: $travelingSalesman"
            )
        } else {
            question.hint?.let {
                Log.d(TAG, "Hint for next attempt: $it. Salesman route: $travelingSalesman")
            }
        }
    }

    /**
     * End the trivia game and reset game state.
     */
    fun endTriviaGame() {
        gameActive = false
        currentQuestion = null
        triviaController?.clearTriviaButton() // Clear the trivia button from UI
        Log.d(TAG, "Trivia game ended.")
    }

    /**
     * Check if the trivia game is active.
     */
    fun isGameActive(): Boolean = gameActive

    /**
     * Reset the trivia game and load a new set of questions.
     */
    fun resetTriviaGame() {
        totalCorrectAnswers = 0
        totalIncorrectAnswers = 0
        playerProgress.clear()
        gameActive = true
        repository.clearAllQuestions() // Clear existing questions
        repository.reloadQuestions(TriviaQuestionGenerator.generateQuestionSet()) // Load new questions
        Log.d(TAG, "Trivia game reset. Loading new questions.")
        loadNextQuestion()
    }

    private fun updateSalesmanRoute(step: String) {
        travelingSalesman += " -> $step"
    }

    fun getTravelingSalesmenLog(): String {
        Log.d(TAG, "Retrieving travelingSalesman log: $travelingSalesman")
        return travelingSalesman
    }
}
