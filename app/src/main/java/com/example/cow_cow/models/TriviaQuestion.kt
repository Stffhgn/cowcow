package com.example.cow_cow.models

data class TriviaQuestion(
    val question: String,
    val possibleAnswers: List<String>,
    val correctAnswer: String,
    val points: Int = 10  // Default points for each question
)