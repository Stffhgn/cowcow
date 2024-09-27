package com.example.cow_cow.models // Adjust this to match your package structure

import android.os.Parcel
import android.os.Parcelable


// Enum representing the types of power-ups in the game
enum class PowerUpType {
    DOUBLE_POINTS,
    FREEZE_PLAYER,
    EXTRA_POINTS,   // Existing enum value
    EXTRA_TIME,     // New enum value
    BONUS_POINTS    // New enum value
}

// Data class representing a power-up's state
data class PowerUp(
    val type: PowerUpType,
    var isActive: Boolean,
    var duration: Long,
    val effectValue: Int = 0  // Default to 0 if no effect
) : Parcelable {
    constructor(parcel: Parcel) : this(
        type = PowerUpType.valueOf(parcel.readString() ?: PowerUpType.DOUBLE_POINTS.name),
        isActive = parcel.readByte() != 0.toByte(),
        duration = parcel.readLong(),
        effectValue = parcel.readInt()  // Read the effect value from the parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(duration)
        parcel.writeInt(effectValue)  // Write the effect value to the parcel
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PowerUp> {
        override fun createFromParcel(parcel: Parcel): PowerUp = PowerUp(parcel)
        override fun newArray(size: Int): Array<PowerUp?> = arrayOfNulls(size)
    }
}