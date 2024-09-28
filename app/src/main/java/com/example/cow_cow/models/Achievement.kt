package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType

data class Achievement(
    val id: Int,                          // Unique ID for the achievement
    val name: String,                     // Achievement name
    val description: String,              // Description of the achievement
    val type: AchievementType,            // Type of achievement (Scoring, Teamwork, etc.)
    var isUnlocked: Boolean = false,      // Whether the achievement has been unlocked
    var currentProgress: Int = 0,         // Current progress towards the achievement (for progressive types)
    val goal: Int = 1,                    // The goal or target to achieve (default 1 for single achievements)
    val rewardType: RewardType,           // Type of reward (Points, Power-Up, Badge, etc.)
    val rewardValue: Int,                 // Reward value (e.g., amount of points or duration for power-ups)
    val timestampUnlocked: Long? = null,  // Timestamp for when the achievement was unlocked
    val conditions: List<Condition> = emptyList(), // New: Complex conditions to meet for achievement
    val isSecret: Boolean = false         // New: Hidden achievements revealed only after unlocking
) : Parcelable {

    // Function to check if the achievement is completed based on progress and conditions
    fun checkProgress(): Boolean {
        return conditions.all { it.isMet() } && currentProgress >= goal
    }

    // Function to unlock the achievement when progress goal is met
    fun unlockAchievement() {
        if (!isUnlocked && checkProgress()) {
            isUnlocked = true
        }
    }

    // Increment progress for progressive achievements
    fun incrementProgress(amount: Int = 1) {
        if (!isUnlocked && type == AchievementType.COLLECTION) {
            currentProgress += amount
            if (currentProgress >= goal) {
                unlockAchievement()
            }
        }
    }

    // Parcelable implementation to make it transferable between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readInt(),                           // ID
        parcel.readString() ?: "",                  // Name
        parcel.readString() ?: "",                  // Description
        AchievementType.valueOf(parcel.readString() ?: AchievementType.SCORING.name),  // Type
        parcel.readByte() != 0.toByte(),            // isUnlocked
        parcel.readInt(),                           // Current progress
        parcel.readInt(),                           // Goal
        RewardType.valueOf(parcel.readString() ?: RewardType.POINTS.name),  // Reward type
        parcel.readInt(),                           // Reward value
        parcel.readValue(Long::class.java.classLoader) as? Long,  // Timestamp unlocked
        listOf()  // Add default conditions list (if none provided)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(type.name)
        parcel.writeByte(if (isUnlocked) 1 else 0)
        parcel.writeInt(currentProgress)
        parcel.writeInt(goal)
        parcel.writeString(rewardType.name)
        parcel.writeInt(rewardValue)
        parcel.writeValue(timestampUnlocked)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Achievement> {
        override fun createFromParcel(parcel: Parcel): Achievement = Achievement(parcel)
        override fun newArray(size: Int): Array<Achievement?> = arrayOfNulls(size)
    }
}