package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of arts-themed trivia questions.
 */
object TriviaArtsQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "Who painted the Mona Lisa?",
            possibleAnswers = listOf("Leonardo da Vinci", "Vincent van Gogh", "Pablo Picasso", "Claude Monet"),
            correctAnswer = "Leonardo da Vinci",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("art", "Renaissance", "paintings"),
            imageUrl = null,
            hint = "He was an Italian polymath of the Renaissance.",
            explanation = "Leonardo da Vinci, an Italian artist and polymath, painted the Mona Lisa.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which composer wrote 'The Four Seasons'?",
            possibleAnswers = listOf("Antonio Vivaldi", "Johann Sebastian Bach", "Ludwig van Beethoven", "Wolfgang Amadeus Mozart"),
            correctAnswer = "Antonio Vivaldi",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("music", "classical", "composers"),
            imageUrl = null,
            hint = "He was known as the 'Red Priest'.",
            explanation = "Antonio Vivaldi, a Baroque composer, wrote 'The Four Seasons', a famous set of violin concertos.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "What art movement is characterized by a focus on light and its changing qualities?",
            possibleAnswers = listOf("Impressionism", "Cubism", "Surrealism", "Expressionism"),
            correctAnswer = "Impressionism",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("art", "movements", "light"),
            imageUrl = null,
            hint = "It was developed in the late 19th century in France.",
            explanation = "Impressionism is an art movement characterized by its focus on light and everyday scenes.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which famous sculpture was created by Michelangelo?",
            possibleAnswers = listOf("David", "The Thinker", "Venus de Milo", "Winged Victory of Samothrace"),
            correctAnswer = "David",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("sculpture", "Renaissance", "Michelangelo"),
            imageUrl = null,
            hint = "It depicts a biblical hero.",
            explanation = "Michelangelo's sculpture 'David' is one of the most famous artworks of the Renaissance.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Who painted 'Starry Night'?",
            possibleAnswers = listOf("Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Salvador Dalí"),
            correctAnswer = "Vincent van Gogh",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("art", "post-impressionism", "paintings"),
            imageUrl = null,
            hint = "He was a Dutch painter who struggled with mental health.",
            explanation = "'Starry Night' is one of Vincent van Gogh's most famous works, painted during his time at an asylum.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What is the term for a painting done on wet plaster?",
            possibleAnswers = listOf("Fresco", "Mosaic", "Tempera", "Oil Painting"),
            correctAnswer = "Fresco",
            category = TriviaCategory.ARTS,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("art techniques", "fresco", "painting"),
            imageUrl = null,
            hint = "It was popular during the Renaissance for mural painting.",
            explanation = "A fresco is a technique of mural painting where water-based pigments are applied to wet plaster.",
            isTimed = true
        )
    )
}
