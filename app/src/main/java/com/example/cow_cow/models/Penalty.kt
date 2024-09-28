package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

// Penalty types to specify the different penalty actions
enum class PenaltyType {
    SILENCED,           // Player is silenced for a certain duration
    POINT_DEDUCTION,    // Deduct points as a penalty
    TEMPORARY_BAN,      // Temporarily restricts the player from making calls
    FALSE_CALL,         // Penalty for making a false call
    TIME_PENALTY,       // Add a time penalty (e.g., player loses 30 seconds)
    OTHER               // Any other custom penalty
}

data class Penalty(
    val id: Int,                                // Unique ID for the penalty
    val name: String,                           // Name of the penalty (e.g., "False Call")
    val pointsDeducted: Int = 0,                // Number of points to deduct as part of the penalty
    val penaltyType: PenaltyType,               // Type of the penalty (e.g., Point Deduction, Silenced)
    var isActive: Boolean = true,               // Indicates if the penalty is currently active
    val duration: Long = 0L,                    // Optional: Duration of the penalty (in milliseconds)
    val startTime: Long = System.currentTimeMillis(),  // When the penalty was applied
    var roundsRemaining: Int? = null,           // Optional: Number of rounds the penalty is active for (instead of duration)
    val stackable: Boolean = true,              // Can the penalty be applied multiple times?
    val multiplier: Double = 1.0,               // Optional: Multiplier to make penalties more severe (e.g., 2x points deduction)
    val metadata: Map<String, Any>? = null      // Optional: Additional data or context related to the penalty (for future flexibility)
) : Parcelable {

    // Function to check if a penalty has expired (based on duration or rounds)
    fun hasExpired(currentTime: Long, currentRound: Int?): Boolean {
        return if (duration > 0) {
            // Check if the time-based penalty has expired
            currentTime >= startTime + duration
        } else if (roundsRemaining != null && currentRound != null) {
            // Check if the round-based penalty has expired
            roundsRemaining!! <= 0
        } else {
            // Penalty does not expire
            false
        }
    }

    // Function to reduce the number of rounds remaining for round-based penalties
    fun reduceRounds() {
        roundsRemaining?.let {
            if (it > 0) roundsRemaining = it - 1
        }
    }

    // Function to apply the penalty (with the multiplier)
    fun applyPenalty(player: Player) {
        if (penaltyType == PenaltyType.POINT_DEDUCTION) {
            val effectiveDeduction = (pointsDeducted * multiplier).toInt()
            player.basePoints -= effectiveDeduction
        }
        // Other penalty logic can be added here (e.g., silencing the player)
    }

    // Parcelable implementation for passing Penalty between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readInt(),  // id
        parcel.readString() ?: "",  // name
        parcel.readInt(),  // pointsDeducted
        PenaltyType.valueOf(parcel.readString() ?: PenaltyType.OTHER.name),  // penaltyType
        parcel.readByte() != 0.toByte(),  // isActive
        parcel.readLong(),  // duration
        parcel.readLong(),  // startTime
        parcel.readValue(Int::class.java.classLoader) as? Int,  // roundsRemaining
        parcel.readByte() != 0.toByte(),  // stackable
        parcel.readDouble(),  // multiplier
        parcel.readHashMap(ClassLoader.getSystemClassLoader()) as? Map<String, Any>  // metadata
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(pointsDeducted)
        parcel.writeString(penaltyType.name)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(duration)
        parcel.writeLong(startTime)
        parcel.writeValue(roundsRemaining)
        parcel.writeByte(if (stackable) 1 else 0)
        parcel.writeDouble(multiplier)
        parcel.writeMap(metadata)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Penalty> {
        override fun createFromParcel(parcel: Parcel): Penalty = Penalty(parcel)
        override fun newArray(size: Int): Array<Penalty?> = arrayOfNulls(size)
    }
}
