package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.AchievementType
import com.example.cow_cow.enums.RewardType

data class Achievement(
    val id: String,                       // Unique ID for the achievement
    val name: String,                  // Achievement name
    val description: String,           // Description of what the achievement is for
    val type: AchievementType,         // Type of achievement (e.g., SCORING, SPECIAL_EVENT)
    val rewardType: RewardType,        // Type of reward (e.g., points, badges, etc.)
    val rewardValue: Int,              // Value of the reward (e.g., 100 points)
    var isUnlocked: Boolean = false,   // Whether the achievement has been unlocked
    var currentProgress: Int = 0,      // Current progress towards unlocking the achievement
    val goal: Int = 100,               // Goal progress required to unlock the achievement
    val playerId: String,              // The player to whom the achievement belongs
    val isSecret: Boolean = false      // Whether the achievement is hidden from the player until unlocked
) : Parcelable {

    private val TAG = "Achievement"

    // Function to increment progress
    fun incrementProgress(amount: Int) {
        currentProgress += amount
        if (currentProgress >= goal) {
            unlockAchievement()
        }
    }

    // Function to check if the achievement is completed based on progress
    fun checkProgress(): Boolean {
        return currentProgress >= goal
    }

    // Function to unlock the achievement
    fun unlockAchievement() {
        if (!isUnlocked && checkProgress()) {
            isUnlocked = true
            Log.d(TAG, "Achievement unlocked: $name")
        }
    }

    // Parcelable implementation for passing Achievement objects between activities/fragments
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        type = AchievementType.valueOf(parcel.readString() ?: AchievementType.SCORING.name),
        rewardType = RewardType.valueOf(parcel.readString() ?: RewardType.POINTS.name),
        rewardValue = parcel.readInt(),
        isUnlocked = parcel.readByte() != 0.toByte(),
        currentProgress = parcel.readInt(),
        goal = parcel.readInt(),
        playerId = parcel.readString() ?: "",
        isSecret = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(type.name)
        parcel.writeString(rewardType.name)
        parcel.writeInt(rewardValue)
        parcel.writeByte(if (isUnlocked) 1 else 0)
        parcel.writeInt(currentProgress)
        parcel.writeInt(goal)
        parcel.writeString(playerId)
        parcel.writeByte(if (isSecret) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Achievement> {
        override fun createFromParcel(parcel: Parcel): Achievement = Achievement(parcel)
        override fun newArray(size: Int): Array<Achievement?> = arrayOfNulls(size)
    }
}