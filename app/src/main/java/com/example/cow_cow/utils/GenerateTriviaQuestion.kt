package com.example.cow_cow.utils

import com.example.cow_cow.enums.TriviaCategory
import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.trivia.*

object TriviaQuestionGenerator {

    private val allQuestions: List<TriviaQuestion> = listOf(
        *TriviaGeographyQuestions.questions.toTypedArray(),
        *TriviaHistoryQuestions.questions.toTypedArray(),
        *TriviaArtsQuestions.questions.toTypedArray(),
        *TriviaScienceQuestions.questions.toTypedArray(),
        *TriviaSportsQuestions.questions.toTypedArray(),
        *TriviaEntertainmentQuestions.questions.toTypedArray(),
        *TriviaRoadTripQuestions.questions.toTypedArray()
    )

    private var travelingSalesmanLog: String = ""

    /**
     * Generates a random trivia question from any category, ensuring it has not been used before.
     * Sets appropriate tags for the question.
     */
    fun generateRandomQuestion(): TriviaQuestion? {
        travelingSalesmanLog += "Starting to generate a random question...\n"
        val unusedQuestions = allQuestions.filter { !it.used }
        travelingSalesmanLog += "Filtered unused questions: ${unusedQuestions.size} available.\n"

        if (unusedQuestions.isEmpty()) {
            travelingSalesmanLog += "No unused questions found. Returning null.\n"
            return null // No unused questions available
        }

        val selectedQuestion = unusedQuestions.random().copy(
            timesUsed = unusedQuestions.random().timesUsed + 1,
            used = true
        ).apply {
            tags = tags + determineTagsForCategory(category)
            travelingSalesmanLog += "Selected question: ${this.questionText}\n"
        }

        return selectedQuestion
    }

    /**
     * Generates a set of questions, sorted by the number of times used (ascending).
     * Ties are randomized to ensure varied order.
     */
    fun generateQuestionSet(): List<TriviaQuestion> {
        travelingSalesmanLog += "Generating question set...\n"
        val sortedQuestions = allQuestions
            .sortedWith(compareBy<TriviaQuestion> { it.timesUsed }
                .thenBy { Math.random() }) // Randomize ties
            .map { it.copy(used = false, timesUsed = it.timesUsed) } // Reset "used" but keep "timesUsed"

        travelingSalesmanLog += "Sorted questions by times used.\n"
        sortedQuestions.forEachIndexed { index, question ->
            travelingSalesmanLog += "Question ${index + 1}: ${question.questionText} (Times used: ${question.timesUsed})\n"
        }

        return sortedQuestions
    }

    /**
     * Determines the appropriate tags for the given category.
     */
    private fun determineTagsForCategory(category: TriviaCategory): List<String> {
        travelingSalesmanLog += "Determining tags for category: $category\n"
        return when (category) {
            TriviaCategory.GEOGRAPHY -> listOf("geography", "landmarks", "travel")
            TriviaCategory.HISTORY -> listOf("history", "events", "figures")
            TriviaCategory.ARTS -> listOf("art", "culture", "literature")
            TriviaCategory.SCIENCE -> listOf("science", "nature", "discovery")
            TriviaCategory.SPORTS -> listOf("sports", "games", "events")
            TriviaCategory.ENTERTAINMENT -> listOf("entertainment", "movies", "music")
        }.also {
            travelingSalesmanLog += "Tags determined: $it\n"
        }
    }

    /**
     * Retrieves the log of the travelingSalesman for debugging.
     */
    fun getTravelingSalesmanLog(): String = travelingSalesmanLog
}
