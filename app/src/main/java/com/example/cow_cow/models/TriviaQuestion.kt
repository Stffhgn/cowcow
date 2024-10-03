package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.DifficultyLevel

data class TriviaQuestion(
    val questionText: String,              // The trivia question
    val possibleAnswers: List<String>,     // List of possible answers
    val correctAnswer: String,             // The correct answer
    val difficultyLevel: DifficultyLevel,  // Difficulty level of the question
    val points: Int,                       // Points awarded for a correct answer
    val hint: String? = null               // Optional hint for the question
) : Parcelable {

    /**
     * Validates whether the provided answer is correct.
     *
     * @param selectedAnswer The answer chosen by the user.
     * @return True if the selected answer is correct, false otherwise.
     */
    fun isCorrectAnswer(selectedAnswer: String): Boolean {
        Log.d("TriviaQuestion", "Validating answer: '$selectedAnswer' for question: '$questionText'")
        return selectedAnswer.equals(correctAnswer, ignoreCase = true)
    }

    /**
     * Returns feedback based on whether the answer is correct.
     *
     * @param selectedAnswer The answer chosen by the user.
     * @return A feedback string indicating correctness.
     */
    fun getFeedback(selectedAnswer: String): String {
        return if (isCorrectAnswer(selectedAnswer)) {
            Log.d("TriviaQuestion", "Answer '$selectedAnswer' is correct. Points awarded: $points")
            "Correct! You earned $points points."
        } else {
            Log.d("TriviaQuestion", "Answer '$selectedAnswer' is incorrect. Correct answer: $correctAnswer")
            "Incorrect. The correct answer is $correctAnswer."
        }
    }

    /**
     * Provides a hint for the question, if available.
     *
     * @return A hint string or a default message if no hint is available.
     */
    fun getHint(): String {
        val hintMessage = hint ?: "No hint available."
        Log.d("TriviaQuestion", "Getting hint for question: '$questionText'. Hint: '$hintMessage'")
        return hintMessage
    }

    // Parcelable implementation for passing TriviaQuestion objects between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: "",
        DifficultyLevel.valueOf(parcel.readString() ?: DifficultyLevel.EASY.name),
        parcel.readInt(),
        parcel.readString()
    ) {
        Log.d("TriviaQuestion", "TriviaQuestion created from Parcel. Question: '$questionText'")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Log.d("TriviaQuestion", "Writing TriviaQuestion to Parcel. Question: '$questionText'")
        parcel.writeString(questionText)
        parcel.writeStringList(possibleAnswers)
        parcel.writeString(correctAnswer)
        parcel.writeString(difficultyLevel.name)
        parcel.writeInt(points)
        parcel.writeString(hint)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TriviaQuestion> {
        override fun createFromParcel(parcel: Parcel): TriviaQuestion {
            Log.d("TriviaQuestion", "Creating TriviaQuestion from Parcel")
            return TriviaQuestion(parcel)
        }

        override fun newArray(size: Int): Array<TriviaQuestion?> {
            Log.d("TriviaQuestion", "Creating an array of TriviaQuestions with size: $size")
            return arrayOfNulls(size)
        }
    }
}
