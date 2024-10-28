package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.PowerUpRarity
import com.example.cow_cow.managers.PowerUpManager

// Data class representing a power-up's state
data class PowerUp(
    val type: PowerUpType,          // Type of power-up (based on the enum PowerUpType)
    var isActive: Boolean = false,  // Whether the power-up is currently active
    var duration: Long = 0L,        // Duration for which the power-up is active (in milliseconds)
    val effectValue: Int = 0,       // Effect value, if applicable (like points or speed boost)
    val startTime: Long = System.currentTimeMillis(),  // When the power-up was activated
    val level: Int = 1,             // Optional: level of power-up for scaling effects
    var isHeld: Boolean = false,    // Indicates if the power-up is held and not yet activated
    val rarity: PowerUpRarity = PowerUpRarity.COMMON  // Rarity of the power-up (e.g., COMMON, RARE, LEGENDARY)
) : Parcelable {

    // Function to check if the power-up has expired (based on the duration)
    fun hasExpired(currentTime: Long): Boolean {
        Log.d("PowerUp", "Checking if power-up $type has expired.")
        return currentTime >= startTime + duration
    }

    // Function to activate the power-up using the PowerUpManager
    fun activate(player: Player) {
        if (isHeld) {
            isHeld = false
            isActive = true
            Log.d("PowerUp", "Power-up $type activated for player ${player.name}.")
            PowerUpManager.applyPowerUpEffect(player, this)
        } else {
            Log.d("PowerUp", "Power-up $type cannot be activated as it is not held.")
        }
    }

    // Function to deactivate the power-up using the PowerUpManager
    fun deactivate(player: Player) {
        isActive = false
        Log.d("PowerUp", "Deactivating power-up $type for player ${player.name}.")
        PowerUpManager.deactivatePowerUp(player, this)
    }

    // Parcelable implementation for passing PowerUp objects between activities/fragments
    constructor(parcel: Parcel) : this(
        type = PowerUpType.valueOf(parcel.readString() ?: PowerUpType.DOUBLE_POINTS.name),
        isActive = parcel.readByte() != 0.toByte(),
        duration = parcel.readLong(),
        effectValue = parcel.readInt(),
        startTime = parcel.readLong(),
        level = parcel.readInt(),
        isHeld = parcel.readByte() != 0.toByte(),
        rarity = PowerUpRarity.valueOf(parcel.readString() ?: PowerUpRarity.COMMON.name)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(duration)
        parcel.writeInt(effectValue)
        parcel.writeLong(startTime)
        parcel.writeInt(level)
        parcel.writeByte(if (isHeld) 1 else 0)
        parcel.writeString(rarity.name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PowerUp> {
        override fun createFromParcel(parcel: Parcel): PowerUp = PowerUp(parcel)
        override fun newArray(size: Int): Array<PowerUp?> = arrayOfNulls(size)
    }
}