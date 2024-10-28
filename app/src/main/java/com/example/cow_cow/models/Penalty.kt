package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PenaltyType

// Global function to calculate points to deduct based on penalty type
fun calculatePenaltyPoints(penaltyType: PenaltyType): Int {
    return when (penaltyType) {
        PenaltyType.FALSE_CALL -> 10
        PenaltyType.SILENCED -> 5
        PenaltyType.POINT_DEDUCTION -> 15
        PenaltyType.TIME_PENALTY -> 20
        else -> {
            Log.w("Penalty", "Unknown penalty type: ${penaltyType.name}. No points deducted.")
            0
        }
    }
}

data class Penalty(
    val id: String,
    val name: String,
    val pointsDeducted: Int = 0,
    val penaltyType: PenaltyType,
    var isActive: Boolean = true,
    val duration: Long = 0L,
    val startTime: Long = System.currentTimeMillis(),
    var roundsRemaining: Int? = null,
    val stackable: Boolean = true,
    val multiplier: Double = 1.0,
    val metadata: Map<String, Any>? = null
) : Parcelable {

    /**
     * Checks if a penalty has expired based on duration or remaining rounds.
     * @param currentTime The current time to check against.
     * @param currentRound The current round, if applicable.
     * @return Boolean indicating whether the penalty has expired.
     */
    fun hasExpired(currentTime: Long, currentRound: Int?): Boolean {
        return if (duration > 0) {
            currentTime >= startTime + duration
        } else if (roundsRemaining != null && currentRound != null) {
            roundsRemaining!! <= 0
        } else {
            false
        }
    }

    /**
     * Reduces the number of rounds remaining for round-based penalties.
     */
    fun reduceRounds() {
        roundsRemaining?.let {
            if (it > 0) roundsRemaining = it - 1
            Log.d("Penalty", "Reduced rounds remaining for penalty $name: $roundsRemaining")
        }
    }

    // Parcelable implementation for passing Penalty between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        PenaltyType.valueOf(parcel.readString() ?: PenaltyType.OTHER.name),
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte(),
        parcel.readDouble(),
        parcel.readHashMap(ClassLoader.getSystemClassLoader()) as? Map<String, Any>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
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

        fun generatePenaltyId(playerId: String, penaltyType: PenaltyType): Int {
            return (playerId.hashCode() * 100) + penaltyType.ordinal
        }
    }
}
