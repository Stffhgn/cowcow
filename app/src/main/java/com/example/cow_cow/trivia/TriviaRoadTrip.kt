package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of road trip-themed trivia questions.
 */
object TriviaRoadTripQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "What is the most famous road in the United States for cross-country road trips?",
            possibleAnswers = listOf("Route 66", "Pacific Coast Highway", "U.S. Route 20", "Interstate 90"),
            correctAnswer = "Route 66",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("USA", "road trips", "famous roads"),
            imageUrl = null,
            hint = "It is also known as 'The Mother Road'.",
            explanation = "Route 66 is one of the most famous roads in the U.S., stretching from Chicago to Santa Monica.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which U.S. state is home to the scenic 'Overseas Highway'?",
            possibleAnswers = listOf("Florida", "California", "Texas", "Hawaii"),
            correctAnswer = "Florida",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("USA", "highways", "scenic routes"),
            imageUrl = null,
            hint = "It connects the mainland to Key West.",
            explanation = "The Overseas Highway in Florida is a 113-mile highway connecting the mainland to the Florida Keys.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What is the name of the famous scenic drive along California's coast?",
            possibleAnswers = listOf("Pacific Coast Highway", "Big Sur Road", "Sunset Boulevard", "Ocean Drive"),
            correctAnswer = "Pacific Coast Highway",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("California", "coastal drives", "USA"),
            imageUrl = null,
            hint = "It's known for stunning ocean views.",
            explanation = "The Pacific Coast Highway (PCH) offers breathtaking views along California's coastline.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What scenic road is famous for its picturesque views of the Smoky Mountains?",
            possibleAnswers = listOf("Blue Ridge Parkway", "Skyline Drive", "Natchez Trace Parkway", "Great River Road"),
            correctAnswer = "Blue Ridge Parkway",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("mountains", "scenic routes", "USA"),
            imageUrl = null,
            hint = "It stretches between North Carolina and Virginia.",
            explanation = "The Blue Ridge Parkway is known for its stunning views of the Appalachian Highlands.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Which iconic Australian road trip route is known for its ocean views?",
            possibleAnswers = listOf("Great Ocean Road", "Stuart Highway", "Bruce Highway", "Eyre Highway"),
            correctAnswer = "Great Ocean Road",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("Australia", "ocean views", "road trips"),
            imageUrl = null,
            hint = "It runs along Australia's southern coast.",
            explanation = "The Great Ocean Road is a popular tourist attraction, offering dramatic ocean views and landmarks.",
            isTimed = false
        )
    )
}
