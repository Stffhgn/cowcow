package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.cow_cow.models.TriviaQuestion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TriviaRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TriviaPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val TAG = "TriviaRepository"
    private var travelingSalesman: String = ""

    /**
     * Loads the next unused trivia question from the stored question list.
     */
    fun loadNextUnusedQuestion(): TriviaQuestion? {
        val allQuestionsJson = sharedPreferences.getString("allQuestions", null)
        return if (allQuestionsJson != null) {
            val type = object : TypeToken<MutableList<TriviaQuestion>>() {}.type
            val questionsList: MutableList<TriviaQuestion> = gson.fromJson(allQuestionsJson, type)

            // Find the next unused question in the list
            val nextUnusedQuestion = questionsList.find { !it.used }
            nextUnusedQuestion?.let {
                travelingSalesman += "Found next unused question: ${it.questionText} -> "
                Log.d(TAG, "Loaded next unused question: ${it.questionText}")
            } ?: run {
                travelingSalesman += "No unused questions available -> "
                Log.d(TAG, "No unused questions found.")
            }

            nextUnusedQuestion
        } else {
            travelingSalesman += "No questions available to load -> "
            Log.d(TAG, "No questions found in shared preferences.")
            null
        }
    }

    /**
     * Clears all trivia questions from the repository.
     */
    fun clearAllQuestions() {
        travelingSalesman += "Clearing all questions -> "
        sharedPreferences.edit().remove("allQuestions").apply()
        Log.d(TAG, "Cleared all trivia questions from shared preferences.")
    }

    /**
     * Checks if the repository is empty (i.e., no questions stored).
     */
    fun isRepositoryEmpty(): Boolean {
        travelingSalesman += "Checking if repository is empty -> "
        val allQuestionsJson = sharedPreferences.getString("allQuestions", null)
        return allQuestionsJson == null || allQuestionsJson.isEmpty()
    }

    /**
     * Checks if there are unused questions in the repository.
     */
    fun hasUnusedQuestions(): Boolean {
        val allQuestionsJson = sharedPreferences.getString("allQuestions", null)
        return if (allQuestionsJson != null) {
            val type = object : TypeToken<List<TriviaQuestion>>() {}.type
            val questionsList: List<TriviaQuestion> = gson.fromJson(allQuestionsJson, type)
            questionsList.any { !it.used }
        } else {
            false
        }
    }

    /**
     * Reloads the repository with a new set of trivia questions.
     */
    fun reloadQuestions(newQuestions: List<TriviaQuestion>) {
        travelingSalesman += "Reloading questions -> "
        val questionsJson = gson.toJson(newQuestions)
        sharedPreferences.edit().putString("allQuestions", questionsJson).apply()
        Log.d(TAG, "Reloaded repository with new questions.")
    }

    /**
     * Removes a specific question from the repository after it has been answered correctly.
     */
    fun removeQuestion(triviaQuestion: TriviaQuestion) {
        travelingSalesman += "Removing question: ${triviaQuestion.questionText} -> "
        val allQuestionsJson = sharedPreferences.getString("allQuestions", null)
        if (allQuestionsJson != null) {
            val type = object : TypeToken<MutableList<TriviaQuestion>>() {}.type
            val questionsList: MutableList<TriviaQuestion> = gson.fromJson(allQuestionsJson, type)
            val iterator = questionsList.iterator()
            while (iterator.hasNext()) {
                val question = iterator.next()
                if (question.questionText == triviaQuestion.questionText) {
                    iterator.remove()
                    Log.d(TAG, "Removed question from repository: ${question.questionText}")
                    break
                }
            }
            // Save the updated list back to shared preferences
            sharedPreferences.edit().putString("allQuestions", gson.toJson(questionsList)).apply()
        } else {
            travelingSalesman += "No questions found to remove -> "
            Log.d(TAG, "No questions found to remove.")
        }
    }

    /**
     * Marks a specific question as used in the repository.
     */
    fun markQuestionAsUsed(triviaQuestion: TriviaQuestion) {
        val allQuestionsJson = sharedPreferences.getString("allQuestions", null)
        if (allQuestionsJson != null) {
            val type = object : TypeToken<MutableList<TriviaQuestion>>() {}.type
            val questionsList: MutableList<TriviaQuestion> = gson.fromJson(allQuestionsJson, type)
            for (question in questionsList) {
                if (question.questionText == triviaQuestion.questionText) {
                    question.used = true
                    break
                }
            }
            // Save the updated list back to shared preferences
            sharedPreferences.edit().putString("allQuestions", gson.toJson(questionsList)).apply()
            Log.d(TAG, "Marked question as used: ${triviaQuestion.questionText}")
        }
    }

    /**
     * Logs the journey of the traveling salesman.
     */
    fun getTravelingSalesmanLog(): String {
        return travelingSalesman
    }
}
