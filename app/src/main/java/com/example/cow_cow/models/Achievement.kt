package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

enum class AchievementType {
    PROGRESSIVE,  // Achievement with progress tracking (e.g., "Collect 100 cows")
    SINGLE,       // One-time achievement (e.g., "First Call Made")
    TIMED,        // Time-based achievements (e.g., "Call 10 items in 1 hour")
    MILESTONE     // Specific milestones in the game (e.g., "10,000 Points Scored")
}

enum class RewardType {
    POINTS,       // Award points as a reward
    POWER_UP,     // Award a power-up (e.g., Double Points, Extra Time)
    UNLOCK_ITEM,  // Unlock new items (e.g., badges, custom skins, etc.)
    BADGE         // Award a badge to show on profile
}

data class Achievement(
    val id: Int,  // Unique ID for the achievement
    val name: String,  // Achievement name
    val description: String,  // Description explaining the achievement
    val type: AchievementType,  // Type of achievement (single, progressive, timed, etc.)
    var isUnlocked: Boolean = false,  // Whether the achievement is unlocked
    var currentProgress: Int = 0,  // Current progress towards the achievement (for progressive types)
    val goal: Int = 1,  // Goal to reach for unlocking (default is 1 for non-progressive achievements)
    val rewardType: RewardType,  // Type of reward (points, power-ups, etc.)
    val rewardValue: Int,  // Amount or value of the reward (points, duration for power-ups, etc.)
    val timestampUnlocked: Long? = null  // Time when the achievement was unlocked (null if not unlocked yet)
) : Parcelable {

    // Function to check if the achievement is completed
    fun checkProgress(): Boolean {
        return currentProgress >= goal
    }

    // Function to unlock the achievement
    fun unlockAchievement() {
        if (!isUnlocked && checkProgress()) {
            isUnlocked = true
        }
    }

    // Function to increment progress (for progressive achievements)
    fun incrementProgress(amount: Int = 1) {
        if (!isUnlocked && type == AchievementType.PROGRESSIVE) {
            currentProgress += amount
            if (currentProgress >= goal) {
                unlockAchievement()
            }
        }
    }

    // Parcelable implementation to allow passing Achievement between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readInt(),  // ID
        parcel.readString() ?: "",  // Name
        parcel.readString() ?: "",  // Description
        AchievementType.valueOf(parcel.readString() ?: AchievementType.SINGLE.name),  // Achievement type
        parcel.readByte() != 0.toByte(),  // isUnlocked
        parcel.readInt(),  // Current progress
        parcel.readInt(),  // Goal
        RewardType.valueOf(parcel.readString() ?: RewardType.POINTS.name),  // Reward type
        parcel.readInt(),  // Reward value
        parcel.readValue(Long::class.java.classLoader) as? Long  // Timestamp unlocked
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
