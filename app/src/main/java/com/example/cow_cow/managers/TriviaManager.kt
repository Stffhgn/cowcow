package com.example.cow_cow.managers

import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.TriviaRepository

class TriviaManager(private val repository: TriviaRepository) {

    private var currentQuestionIndex: Int = 0
    private var triviaQuestions: List<TriviaQuestion> = listOf()
    private var totalCorrectAnswers: Int = 0
    private var totalIncorrectAnswers: Int = 0
    private var playerProgress: MutableMap<Player, Int> = mutableMapOf() // Track each player's progress
    private var gameActive: Boolean = false

    init {
        // Load questions from the repository at initialization
        triviaQuestions = TriviaRepository().loadTriviaQuestions()
    }

    /**
     * Load trivia questions from the repository and initialize the game.
     */
    fun loadQuestions(shuffle: Boolean = false) {
        triviaQuestions = repository.loadTriviaQuestions()
        initializeTriviaQuestions(triviaQuestions, shuffle)
    }

    /**
     * Initialize the trivia questions for the game.
     * Optionally, it can shuffle questions to randomize them.
     */
    fun initializeTriviaQuestions(questionList: List<TriviaQuestion>, shuffle: Boolean = false) {
        triviaQuestions = if (shuffle) {
            questionList.shuffled()
        } else {
            questionList
        }
        currentQuestionIndex = 0
        totalCorrectAnswers = 0
        totalIncorrectAnswers = 0
        playerProgress.clear() // Reset progress for all players
        gameActive = true
    }

    /**
     * Get the current question based on the game progress.
     */
    fun getCurrentQuestion(): TriviaQuestion? {
        return if (currentQuestionIndex < triviaQuestions.size) {
            triviaQuestions[currentQuestionIndex]
        } else {
            null  // No more questions
        }
    }

    /**
     * Get the next trivia question in the list.
     */
    fun getNextQuestion(): TriviaQuestion? {
        return if (currentQuestionIndex < triviaQuestions.size) {
            val question = triviaQuestions[currentQuestionIndex]
            currentQuestionIndex++
            question
        } else {
            null  // No more questions available
        }
    }

    /**
     * Validate the answer and update player's score and progress.
     */
    fun validateAnswer(player: Player, selectedAnswer: String): Boolean {
        val currentQuestion = getCurrentQuestion()
        return if (currentQuestion?.correctAnswer == selectedAnswer) {
            player.addBonusPoints(currentQuestion.points)
            true
        } else {
            false
        }
    }

    /**
     * Get the player's progress (e.g., number of questions answered).
     */
    fun getPlayerProgress(player: Player): Int {
        return playerProgress.getOrDefault(player, 0)
    }

    /**
     * Shuffle trivia questions to randomize their order.
     */
    fun shuffleQuestions() {
        triviaQuestions = triviaQuestions.shuffled()
    }

    /**
     * End the current trivia game session and stop accepting answers.
     */
    fun endTriviaGame() {
        gameActive = false
    }

    /**
     * Returns whether the trivia game is still active.
     */
    fun isGameActive(): Boolean {
        return gameActive
    }

    /**
     * Get statistics about the trivia game.
     */
    fun getTriviaStatistics(): Map<String, Any> {
        return mapOf(
            "totalQuestions" to triviaQuestions.size,
            "answeredCorrectly" to totalCorrectAnswers,
            "answeredIncorrectly" to totalIncorrectAnswers,
            "questionsLeft" to triviaQuestions.size - currentQuestionIndex
        )
    }

    /**
     * Reset the game progress, clearing all questions and scores.
     */
    fun resetTriviaGame() {
        currentQuestionIndex = 0
        totalCorrectAnswers = 0
        totalIncorrectAnswers = 0
        gameActive = true
        playerProgress.clear()
    }
}
