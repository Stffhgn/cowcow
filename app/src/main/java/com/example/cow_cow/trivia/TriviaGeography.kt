package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of geography-themed trivia questions.
 */
object TriviaGeographyQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "What is the longest highway in the United States?",
            possibleAnswers = listOf("U.S. Route 20", "U.S. Route 6", "Interstate 90", "Route 66"),
            correctAnswer = "U.S. Route 20",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("road", "USA", "travel"),
            timeLimit = 30,
            bonusPoints = 5,
            penaltyPoints = -5,
            hint = "It's known for spanning across many states.",
            imageUrl = "https://example.com/highway_image.jpg",
            explanation = "U.S. Route 20 is the longest road in the United States, stretching from coast to coast.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Which U.S. state is famous for its 'Road to Hana' drive?",
            possibleAnswers = listOf("Hawaii", "California", "Florida", "Texas"),
            correctAnswer = "Hawaii",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("state", "scenic", "USA"),
            timeLimit = 20,
            hint = "It's an island state.",
            imageUrl = "https://example.com/hana_drive.jpg",
            explanation = "The Road to Hana is a famous scenic drive located in Hawaii, offering breathtaking views.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What is the name of the famous coastal highway in California?",
            possibleAnswers = listOf("Pacific Coast Highway", "Sunset Boulevard", "Ocean Drive", "Seaside Road"),
            correctAnswer = "Pacific Coast Highway",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("California", "coast", "USA"),
            hint = "It runs along the Pacific Ocean.",
            imageUrl = "https://example.com/pch.jpg",
            explanation = "The Pacific Coast Highway, also known as PCH, is famous for its scenic ocean views.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which national park is known for the scenic 'Going-to-the-Sun Road'?",
            possibleAnswers = listOf("Glacier National Park", "Yosemite National Park", "Yellowstone National Park", "Zion National Park"),
            correctAnswer = "Glacier National Park",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("national park", "USA", "scenic"),
            timeLimit = 25,
            hint = "It's located in Montana.",
            imageUrl = "https://example.com/going_to_the_sun.jpg",
            explanation = "The Going-to-the-Sun Road is a must-visit highlight in Glacier National Park.",
            isTimed = true,
            isMultipleChoice = true
        ),
        TriviaQuestion(
            questionText = "Which country is home to the 'Great Ocean Road'?",
            possibleAnswers = listOf("Australia", "New Zealand", "Canada", "South Africa"),
            correctAnswer = "Australia",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("Australia", "road", "travel"),
            timeLimit = 15,
            hint = "It's known for its unique wildlife.",
            imageUrl = "https://example.com/great_ocean_road.jpg",
            explanation = "The Great Ocean Road is a world-famous scenic route in Australia.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which U.S. state features the 'Blue Ridge Parkway'?",
            possibleAnswers = listOf("North Carolina", "Colorado", "Utah", "Oregon"),
            correctAnswer = "North Carolina",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("state", "scenic", "USA"),
            timeLimit = 20,
            hint = "It's part of the Appalachian region.",
            imageUrl = "https://example.com/blue_ridge.jpg",
            explanation = "The Blue Ridge Parkway is famous for its stunning mountain views and is located in North Carolina.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "The 'Ring Road' is a famous route circling which country?",
            possibleAnswers = listOf("Iceland", "Ireland", "Greenland", "Scotland"),
            correctAnswer = "Iceland",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("Iceland", "road", "travel"),
            timeLimit = 25,
            hint = "It's known for its volcanic landscape.",
            imageUrl = "https://example.com/ring_road.jpg",
            explanation = "Iceland's Ring Road takes travelers around the island and showcases breathtaking scenery.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What is the name of the famous mountain pass on Colorado's 'Million Dollar Highway'?",
            possibleAnswers = listOf("Red Mountain Pass", "Wolf Creek Pass", "Monarch Pass", "Loveland Pass"),
            correctAnswer = "Red Mountain Pass",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("Colorado", "mountain pass", "USA"),
            hint = "It's part of a highway known for its dangerous curves.",
            imageUrl = "https://example.com/red_mountain_pass.jpg",
            explanation = "Red Mountain Pass on the Million Dollar Highway is famous for its steep, winding path.",
            isTimed = true,
            bonusPoints = 10
        ),
        TriviaQuestion(
            questionText = "Which U.S. state is home to 'Monument Valley'?",
            possibleAnswers = listOf("Arizona", "Utah", "Nevada", "New Mexico"),
            correctAnswer = "Arizona",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("Monument Valley", "USA", "landmarks"),
            hint = "It's part of the Navajo Nation.",
            imageUrl = "https://example.com/monument_valley.jpg",
            explanation = "Monument Valley, known for its iconic rock formations, is located in Arizona.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which Canadian highway stretches from St. John's to Victoria?",
            possibleAnswers = listOf("Trans-Canada Highway", "Yellowhead Highway", "Crowsnest Highway", "Sea to Sky Highway"),
            correctAnswer = "Trans-Canada Highway",
            category = TriviaCategory.GEOGRAPHY,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("Canada", "highway", "travel"),
            timeLimit = 30,
            hint = "It's the longest national highway in the world.",
            imageUrl = "https://example.com/trans_canada.jpg",
            explanation = "The Trans-Canada Highway spans from the Atlantic to the Pacific, making it the world's longest national highway.",
            isTimed = true
        )
    )
}
