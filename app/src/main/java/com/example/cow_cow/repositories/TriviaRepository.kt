package com.example.cow_cow.repositories

package com.example.cow_cow.repositories

import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.enums.DifficultyLevel

class TriviaRepository {

    /**
     * Loads a list of trivia questions.
     * This can be replaced with database or API calls in the future.
     */
    fun loadTriviaQuestions(): List<TriviaQuestion> {
        return listOf(
            TriviaQuestion(
                questionText = "What is the capital of France?",
                possibleAnswers = listOf("Paris", "London", "Berlin", "Madrid"),
                correctAnswer = "Paris",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "Which planet is known as the Red Planet?",
                possibleAnswers = listOf("Earth", "Mars", "Jupiter", "Venus"),
                correctAnswer = "Mars",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "Who wrote 'Hamlet'?",
                possibleAnswers = listOf("Shakespeare", "Dickens", "Chaucer", "Hemingway"),
                correctAnswer = "Shakespeare",
                difficultyLevel = DifficultyLevel.HARD,
                points = 20
            ),
            TriviaQuestion(
                questionText = "What is the largest ocean on Earth?",
                possibleAnswers = listOf("Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean"),
                correctAnswer = "Pacific Ocean",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "Which country is home to the kangaroo?",
                possibleAnswers = listOf("Australia", "South Africa", "New Zealand", "Brazil"),
                correctAnswer = "Australia",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "What is the smallest country in the world?",
                possibleAnswers = listOf("Vatican City", "Monaco", "Malta", "San Marino"),
                correctAnswer = "Vatican City",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "Which element has the chemical symbol 'O'?",
                possibleAnswers = listOf("Oxygen", "Gold", "Osmium", "Oganesson"),
                correctAnswer = "Oxygen",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "In what year did the Titanic sink?",
                possibleAnswers = listOf("1912", "1905", "1899", "1923"),
                correctAnswer = "1912",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "Which famous artist painted the ceiling of the Sistine Chapel?",
                possibleAnswers = listOf("Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello"),
                correctAnswer = "Michelangelo",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "Which planet is closest to the sun?",
                possibleAnswers = listOf("Mercury", "Venus", "Earth", "Mars"),
                correctAnswer = "Mercury",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "What is the longest river in the world?",
                possibleAnswers = listOf("Nile", "Amazon", "Yangtze", "Mississippi"),
                correctAnswer = "Nile",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "In which year did World War II end?",
                possibleAnswers = listOf("1945", "1939", "1941", "1948"),
                correctAnswer = "1945",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "Which artist painted 'The Starry Night'?",
                possibleAnswers = listOf("Vincent van Gogh", "Claude Monet", "Pablo Picasso", "Salvador Dali"),
                correctAnswer = "Vincent van Gogh",
                difficultyLevel = DifficultyLevel.HARD,
                points = 20
            ),
            TriviaQuestion(
                questionText = "Which scientist developed the theory of relativity?",
                possibleAnswers = listOf("Albert Einstein", "Isaac Newton", "Marie Curie", "Nikola Tesla"),
                correctAnswer = "Albert Einstein",
                difficultyLevel = DifficultyLevel.HARD,
                points = 20
            ),
            TriviaQuestion(
                questionText = "What is the largest desert in the world?",
                possibleAnswers = listOf("Sahara Desert", "Arabian Desert", "Gobi Desert", "Antarctic Desert"),
                correctAnswer = "Antarctic Desert",
                difficultyLevel = DifficultyLevel.HARD,
                points = 20
            ),
            TriviaQuestion(
                questionText = "Who was the first president of the United States?",
                possibleAnswers = listOf("George Washington", "Abraham Lincoln", "John Adams", "Thomas Jefferson"),
                correctAnswer = "George Washington",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "Which country hosted the 2016 Summer Olympics?",
                possibleAnswers = listOf("Brazil", "United Kingdom", "China", "Japan"),
                correctAnswer = "Brazil",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            ),
            TriviaQuestion(
                questionText = "What is the hardest natural substance on Earth?",
                possibleAnswers = listOf("Diamond", "Quartz", "Graphite", "Topaz"),
                correctAnswer = "Diamond",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "In which city was the Titanic built?",
                possibleAnswers = listOf("Belfast", "London", "Liverpool", "Glasgow"),
                correctAnswer = "Belfast",
                difficultyLevel = DifficultyLevel.HARD,
                points = 20
            ),
            TriviaQuestion(
                questionText = "Who was the first woman to win a Nobel Prize?",
                possibleAnswers = listOf("Marie Curie", "Rosalind Franklin", "Jane Goodall", "Ada Lovelace"),
                correctAnswer = "Marie Curie",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "In what year did humans first land on the moon?",
                possibleAnswers = listOf("1969", "1959", "1972", "1980"),
                correctAnswer = "1969",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "Who painted the Mona Lisa?",
                possibleAnswers = listOf("Leonardo da Vinci", "Michelangelo", "Raphael", "Titian"),
                correctAnswer = "Leonardo da Vinci",
                difficultyLevel = DifficultyLevel.MEDIUM,
                points = 15
            ),
            TriviaQuestion(
                questionText = "What is the largest mammal in the world?",
                possibleAnswers = listOf("Blue Whale", "Elephant", "Giraffe", "Great White Shark"),
                correctAnswer = "Blue Whale",
                difficultyLevel = DifficultyLevel.EASY,
                points = 10
            )
            // Add more trivia questions as needed
        )
    }
}

