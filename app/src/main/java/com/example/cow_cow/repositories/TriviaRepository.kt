package com.example.cow_cow.repositories

import com.example.cow_cow.models.TriviaQuestion

class TriviaRepository {

    fun loadTriviaQuestions(): List<TriviaQuestion> {
        // Example static data
        return listOf(
            TriviaQuestion(
                question = "What is the capital of France?",
                possibleAnswers = listOf("Paris", "London", "Berlin", "Madrid"),
                correctAnswer = "Paris",
                points = 10
            ),
            TriviaQuestion(
                question = "Which planet is known as the Red Planet?",
                possibleAnswers = listOf("Earth", "Mars", "Jupiter", "Venus"),
                correctAnswer = "Mars",
                points = 15
            )
            // Add more questions...
        )
    }
}
