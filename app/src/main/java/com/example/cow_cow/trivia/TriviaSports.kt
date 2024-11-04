package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of sports-themed trivia questions.
 */
object TriviaSportsQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "Which country won the FIFA World Cup in 2018?",
            possibleAnswers = listOf("France", "Croatia", "Brazil", "Germany"),
            correctAnswer = "France",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("soccer", "FIFA", "world cup"),
            imageUrl = null,
            hint = "They won against Croatia in the final.",
            explanation = "France won the 2018 FIFA World Cup, defeating Croatia 4-2 in the final.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What sport is known as 'America's pastime'?",
            possibleAnswers = listOf("Baseball", "Basketball", "Football", "Hockey"),
            correctAnswer = "Baseball",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("USA", "baseball", "history"),
            imageUrl = null,
            hint = "It's played with a bat and a ball.",
            explanation = "Baseball has long been considered America's pastime due to its historical significance and popularity.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "How many points is a touchdown worth in American football?",
            possibleAnswers = listOf("6", "3", "7", "5"),
            correctAnswer = "6",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("football", "scoring", "rules"),
            imageUrl = null,
            hint = "It is more than 3 points.",
            explanation = "A touchdown in American football is worth 6 points.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which tennis tournament is played on a grass surface?",
            possibleAnswers = listOf("Wimbledon", "French Open", "US Open", "Australian Open"),
            correctAnswer = "Wimbledon",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("tennis", "tournaments", "grass"),
            imageUrl = null,
            hint = "It's held in the United Kingdom.",
            explanation = "Wimbledon is the only Grand Slam tennis tournament played on a grass surface.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "In what year were the first modern Olympic Games held?",
            possibleAnswers = listOf("1896", "1900", "1888", "1920"),
            correctAnswer = "1896",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("Olympics", "history", "sports events"),
            imageUrl = null,
            hint = "It was held in Athens, Greece.",
            explanation = "The first modern Olympic Games were held in 1896 in Athens, Greece.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What is the highest possible break in snooker?",
            possibleAnswers = listOf("147", "155", "120", "100"),
            correctAnswer = "147",
            category = TriviaCategory.SPORTS,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("snooker", "scoring", "record"),
            imageUrl = null,
            hint = "It's called a 'maximum break'.",
            explanation = "The highest possible break in snooker is 147, known as a maximum break.",
            isTimed = false
        )
    )
}