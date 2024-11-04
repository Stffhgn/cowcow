package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of entertainment-themed trivia questions.
 */
object TriviaEntertainmentQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "Which movie features the character 'Forrest Gump'?",
            possibleAnswers = listOf("Forrest Gump", "The Green Mile", "Saving Private Ryan", "Cast Away"),
            correctAnswer = "Forrest Gump",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("movies", "classic", "character"),
            imageUrl = null,
            hint = "Life is like a box of chocolates...",
            explanation = "Forrest Gump is a famous movie featuring Tom Hanks as the titular character.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Who directed the movie 'Pulp Fiction'?",
            possibleAnswers = listOf("Quentin Tarantino", "Martin Scorsese", "Steven Spielberg", "James Cameron"),
            correctAnswer = "Quentin Tarantino",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("movies", "directors", "cult classics"),
            imageUrl = null,
            hint = "He also directed 'Kill Bill'.",
            explanation = "Quentin Tarantino is known for his distinctive style and directed 'Pulp Fiction'.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Which actor played Jack Dawson in 'Titanic'?",
            possibleAnswers = listOf("Leonardo DiCaprio", "Brad Pitt", "Tom Cruise", "Johnny Depp"),
            correctAnswer = "Leonardo DiCaprio",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("movies", "actors", "blockbusters"),
            imageUrl = null,
            hint = "He also starred in 'Inception'.",
            explanation = "Leonardo DiCaprio played Jack Dawson in the iconic movie 'Titanic'.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What is the highest-grossing film of all time?",
            possibleAnswers = listOf("Avatar", "Avengers: Endgame", "Titanic", "Star Wars: The Force Awakens"),
            correctAnswer = "Avatar",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.HARD,
            points = 20,
            tags = listOf("movies", "box office", "records"),
            imageUrl = null,
            hint = "It's directed by James Cameron.",
            explanation = "James Cameron's 'Avatar' holds the record as the highest-grossing film.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Who is known as the 'King of Pop'?",
            possibleAnswers = listOf("Michael Jackson", "Elvis Presley", "Prince", "David Bowie"),
            correctAnswer = "Michael Jackson",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("music", "pop culture", "icons"),
            imageUrl = null,
            hint = "He performed 'Thriller'.",
            explanation = "Michael Jackson is widely known as the 'King of Pop'.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which TV show features the characters Ross, Rachel, Monica, Chandler, Joey, and Phoebe?",
            possibleAnswers = listOf("Friends", "Seinfeld", "The Office", "How I Met Your Mother"),
            correctAnswer = "Friends",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("television", "sitcoms", "90s"),
            imageUrl = null,
            hint = "It aired in the 90s and early 2000s.",
            explanation = "'Friends' is a popular sitcom that ran from 1994 to 2004.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which video game franchise features a hero named Link?",
            possibleAnswers = listOf("The Legend of Zelda", "Final Fantasy", "Super Mario", "Metroid"),
            correctAnswer = "The Legend of Zelda",
            category = TriviaCategory.ENTERTAINMENT,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("video games", "Nintendo", "adventure"),
            imageUrl = null,
            hint = "He wields a Master Sword.",
            explanation = "Link is the main character in 'The Legend of Zelda' franchise.",
            isTimed = true
        )
    )
}
