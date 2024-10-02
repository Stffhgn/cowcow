package com.example.cow_cow.models

import com.example.cow_cow.enums.DifficultyLevel

data class TriviaQuestion(
    val questionText: String,
    val possibleAnswers: List<String>,
    val correctAnswer: String,
    val difficultyLevel: DifficultyLevel,
    val points: Int // Ensure that this is an Int for points value
){

    /**
     * Validates whether the provided answer is correct.
     *
     * @param selectedAnswer The answer chosen by the user.
     * @return True if the selected answer is correct, false otherwise.
     */
    fun isCorrectAnswer(selectedAnswer: String): Boolean {
        return selectedAnswer.equals(correctAnswer, ignoreCase = true)
    }

    /**
     * Returns feedback based on whether the answer is correct.
     *
     * @param selectedAnswer The answer chosen by the user.
     * @return A feedback string indicating correctness.
     */
    fun getFeedback(selectedAnswer: String): String {
        return if (isCorrectAnswer(selectedAnswer)) {
            "Correct! You earned $points points."
        } else {
            "Incorrect. The correct answer is $correctAnswer."
        }
    }

    fun getHint(): String? {
        return hint ?: "No hint available."
    }
}