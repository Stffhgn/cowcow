package com.example.cow_cow.models

import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.TriviaCategory
import android.os.Parcel
import android.os.Parcelable

data class TriviaQuestion(
    val questionText: String,               // The trivia question
    val possibleAnswers: List<String>,      // List of possible answers
    val correctAnswer: String,              // The correct answer
    val category: TriviaCategory,           // Trivia category for color-coding
    val difficultyLevel: DifficultyLevel,   // Difficulty level of the question
    val points: Int,                        // Points awarded for a correct answer
    var used: Boolean = false,              // Indicates if the question has been used
    var timesUsed: Int = 0,                 // Number of times the question has been used
    val hint: String? = null,       // Optional hint for the question
    val timeLimit: Int? = null,             // Time limit to answer the question (in seconds)
    val bonusPoints: Int = 0,               // Bonus points for answering quickly or under specific conditions
    val penaltyPoints: Int = 0,             // Penalty for incorrect or late answers
    var tags: List<String> = listOf(),      // Tags for additional categorization or searchability
    val imageUrl: String? = null,           // URL for an image associated with the question (for visual rounds)
    val explanation: String? = null,        // Explanation to show after answering (useful for learning mode)
    val audioClipUrl: String? = null,       // URL for an audio clip for audio-based questions
    val videoClipUrl: String? = null,       // URL for a video clip for video-based questions
    val isTimed: Boolean = false,           // Flag to indicate if the question is part of a timed round
    val isMultipleChoice: Boolean = true,   // Indicates if the question is multiple choice (vs. true/false, etc.)
    val maxAttempts: Int = 1,               // Maximum number of attempts allowed for the question
    val specialRules: String? = null        // Special rules or notes specific to this question (e.g., sudden death)
) : Parcelable {

    // Parcelable implementation for passing TriviaQuestion objects between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: "",
        TriviaCategory.valueOf(parcel.readString() ?: TriviaCategory.GEOGRAPHY.name),
        DifficultyLevel.valueOf(parcel.readString() ?: DifficultyLevel.EASY.name),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readInt(),
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(questionText)
        parcel.writeStringList(possibleAnswers)
        parcel.writeString(correctAnswer)
        parcel.writeString(category.name)
        parcel.writeString(difficultyLevel.name)
        parcel.writeInt(points)
        parcel.writeByte(if (used) 1 else 0)
        parcel.writeInt(timesUsed)
        parcel.writeString(hint)
        parcel.writeValue(timeLimit)
        parcel.writeInt(bonusPoints)
        parcel.writeInt(penaltyPoints)
        parcel.writeStringList(tags)
        parcel.writeString(imageUrl)
        parcel.writeString(explanation)
        parcel.writeString(audioClipUrl)
        parcel.writeString(videoClipUrl)
        parcel.writeByte(if (isTimed) 1 else 0)
        parcel.writeByte(if (isMultipleChoice) 1 else 0)
        parcel.writeInt(maxAttempts)
        parcel.writeString(specialRules)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TriviaQuestion> {
        override fun createFromParcel(parcel: Parcel): TriviaQuestion {
            return TriviaQuestion(parcel)
        }

        override fun newArray(size: Int): Array<TriviaQuestion?> {
            return arrayOfNulls(size)
        }
    }
}
