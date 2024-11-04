package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of science-themed trivia questions.
 */
object TriviaScienceQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "What planet is known as the 'Red Planet'?",
            possibleAnswers = listOf("Mars", "Jupiter", "Venus", "Saturn"),
            correctAnswer = "Mars",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("space", "planets", "astronomy"),
            imageUrl = null,
            hint = "It's the fourth planet from the Sun.",
            explanation = "Mars is known as the 'Red Planet' due to its reddish appearance, caused by iron oxide on its surface.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What is the chemical symbol for water?",
            possibleAnswers = listOf("H2O", "CO2", "O2", "NaCl"),
            correctAnswer = "H2O",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("chemistry", "molecules", "symbols"),
            imageUrl = null,
            hint = "It consists of hydrogen and oxygen.",
            explanation = "H2O is the chemical formula for water, consisting of two hydrogen atoms and one oxygen atom.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What force keeps us anchored to the Earth?",
            possibleAnswers = listOf("Gravity", "Magnetism", "Inertia", "Friction"),
            correctAnswer = "Gravity",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("physics", "forces", "earth"),
            imageUrl = null,
            hint = "It's a force that attracts two bodies toward each other.",
            explanation = "Gravity is the force that pulls objects toward the center of the Earth.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What part of the cell is known as the 'powerhouse'?",
            possibleAnswers = listOf("Mitochondria", "Nucleus", "Ribosome", "Golgi apparatus"),
            correctAnswer = "Mitochondria",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("biology", "cell structure", "organelles"),
            imageUrl = null,
            hint = "It generates most of the cell's supply of energy.",
            explanation = "Mitochondria are known as the 'powerhouses' of the cell because they produce energy.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What is the speed of light in a vacuum?",
            possibleAnswers = listOf("299,792 km/s", "150,000 km/s", "1,000,000 km/s", "100,000 km/s"),
            correctAnswer = "299,792 km/s",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("physics", "light", "speed"),
            imageUrl = null,
            hint = "It's just under 300,000 km/s.",
            explanation = "The speed of light in a vacuum is approximately 299,792 kilometers per second.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What gas do plants absorb from the atmosphere?",
            possibleAnswers = listOf("Carbon Dioxide", "Oxygen", "Nitrogen", "Helium"),
            correctAnswer = "Carbon Dioxide",
            category = TriviaCategory.SCIENCE,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("biology", "plants", "photosynthesis"),
            imageUrl = null,
            hint = "It's essential for photosynthesis.",
            explanation = "Plants absorb carbon dioxide from the atmosphere to perform photosynthesis and produce oxygen.",
            isTimed = false
        )
    )
}