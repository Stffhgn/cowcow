package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import com.example.cow_cow.enums.TeamBonusType

data class TeamBonus(
    val type: TeamBonusType,      // Type of bonus (e.g., extra points, speed boost)
    val effectValue: Int,         // The value of the bonus (e.g., +50 points)
    var isActive: Boolean = true, // Whether the bonus is currently active
    val duration: Long = 0L       // Duration of the bonus (optional)
) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        type = TeamBonusType.valueOf(parcel.readString() ?: TeamBonusType.EXTRA_POINTS.name),
        effectValue = parcel.readInt(),
        isActive = parcel.readByte() != 0.toByte(),
        duration = parcel.readLong()
    )

    // Writing object to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeInt(effectValue)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TeamBonus> {
        override fun createFromParcel(parcel: Parcel): TeamBonus {
            return TeamBonus(parcel)
        }

        override fun newArray(size: Int): Array<TeamBonus?> {
            return arrayOfNulls(size)
        }
    }
}
