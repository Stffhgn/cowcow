package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

enum class PenaltyType {
    SILENCED,  // Player is silenced for a certain duration
    POINT_DEDUCTION,  // Deduct points as a penalty
    TEMPORARY_BAN,  // Temporarily restricts player from making calls
    FALSE_CALL,  // Penalty for making a false call
    OTHER  // Any other custom penalty
}

data class Penalty(
    val id: Int,
    val name: String,
    val pointsDeducted: Int,
    val penaltyType: PenaltyType,  // The type of penalty
    var isActive: Boolean = true,
    val duration: Long = 0L,  // Optional: Duration of the penalty (for timed penalties)
    val startTime: Long = System.currentTimeMillis()  // When the penalty was applied
) : Parcelable {
    // Function to check if the player has active penalties

    constructor(parcel: Parcel) : this(
        parcel.readInt(),  // id
        parcel.readString() ?: "",  // name
        parcel.readInt(),  // pointsDeducted
        PenaltyType.valueOf(parcel.readString() ?: PenaltyType.OTHER.name),  // penaltyType
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),  // duration
        parcel.readLong()  // startTime
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(pointsDeducted)
        parcel.writeString(penaltyType.name)
        parcel.writeByte(if (isActive) 1 else 0)  // Save boolean as byte
        parcel.writeLong(duration)
        parcel.writeLong(startTime)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Penalty> {
        override fun createFromParcel(parcel: Parcel): Penalty {
            return Penalty(parcel)
        }

        override fun newArray(size: Int): Array<Penalty?> {
            return arrayOfNulls(size)
        }
    }
}