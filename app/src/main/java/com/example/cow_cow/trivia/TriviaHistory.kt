package com.example.cow_cow.trivia

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion

/**
 * Object holding a predefined list of history-themed trivia questions.
 */
object TriviaHistoryQuestions {

    val questions: List<TriviaQuestion> = listOf(
        TriviaQuestion(
            questionText = "Who was the first President of the United States?",
            possibleAnswers = listOf("George Washington", "John Adams", "Thomas Jefferson", "James Madison"),
            correctAnswer = "George Washington",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("US history", "presidents", "founding fathers"),
            imageUrl = null,
            hint = "He is often called the 'Father of His Country'.",
            explanation = "George Washington was the first President of the United States, serving from 1789 to 1797.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What year did World War II end?",
            possibleAnswers = listOf("1945", "1944", "1946", "1947"),
            correctAnswer = "1945",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("WWII", "20th century", "global events"),
            imageUrl = null,
            hint = "It ended with the surrender of Germany and Japan.",
            explanation = "World War II ended in 1945 with the unconditional surrender of both Nazi Germany and Imperial Japan.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Who was the first man to step on the moon?",
            possibleAnswers = listOf("Neil Armstrong", "Buzz Aldrin", "Yuri Gagarin", "Michael Collins"),
            correctAnswer = "Neil Armstrong",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("space", "NASA", "moon landing"),
            imageUrl = null,
            hint = "He said 'That's one small step for man, one giant leap for mankind'.",
            explanation = "Neil Armstrong was the first human to step on the moon during the Apollo 11 mission in 1969.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "What ancient civilization built the pyramids of Giza?",
            possibleAnswers = listOf("Egyptians", "Mayans", "Romans", "Aztecs"),
            correctAnswer = "Egyptians",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.EASY,
            points = 10,
            tags = listOf("ancient history", "Egypt", "architecture"),
            imageUrl = null,
            hint = "They were one of the earliest known civilizations.",
            explanation = "The pyramids of Giza were constructed by the ancient Egyptians during the Old Kingdom period.",
            isTimed = false
        ),
        TriviaQuestion(
            questionText = "Which document begins with 'We the People'?",
            possibleAnswers = listOf("The United States Constitution", "The Declaration of Independence", "The Bill of Rights", "The Articles of Confederation"),
            correctAnswer = "The United States Constitution",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("US history", "foundational documents", "politics"),
            imageUrl = null,
            hint = "It is the supreme law of the United States.",
            explanation = "The United States Constitution starts with 'We the People' and was adopted in 1787.",
            isTimed = true
        ),
        TriviaQuestion(
            questionText = "Who was the British Prime Minister during most of World War II?",
            possibleAnswers = listOf("Winston Churchill", "Neville Chamberlain", "Clement Attlee", "Harold Macmillan"),
            correctAnswer = "Winston Churchill",
            category = TriviaCategory.HISTORY,
            difficultyLevel = DifficultyLevel.MEDIUM,
            points = 15,
            tags = listOf("WWII", "leaders", "Britain"),
            imageUrl = null,
            hint = "He famously said 'We shall never surrender'.",
            explanation = "Winston Churchill was the British Prime Minister from 1940 to 1945 and again from 1951 to 1955.",
            isTimed = false
        )
    )
}
