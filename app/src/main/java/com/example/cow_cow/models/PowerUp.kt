package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PowerUpType

// Data class representing a power-up's state
data class PowerUp(
    val type: PowerUpType,          // Type of power-up (based on the enum PowerUpType)
    var isActive: Boolean,          // Whether the power-up is currently active
    var duration: Long = 0L,        // Duration for which the power-up is active (in milliseconds)
    val effectValue: Int = 0,       // Effect value, if applicable (like points or speed boost)
    val startTime: Long = System.currentTimeMillis(),  // When the power-up was activated
    val level: Int = 1              // Optional: level of power-up for scaling effects
) : Parcelable {

    // Function to check if the power-up has expired (based on the duration)
    fun hasExpired(currentTime: Long): Boolean {
        Log.d("PowerUp", "Checking if power-up $type has expired.")
        return currentTime >= startTime + duration
    }

    // Function to apply the power-up effect to a player
    fun applyPowerUp(player: Player) {
        if (!isActive) {
            Log.d("PowerUp", "Power-up $type is not active.")
            return
        }

        Log.d("PowerUp", "Applying power-up $type to player ${player.name}.")
        when (type) {
            PowerUpType.DOUBLE_POINTS -> {
                player.basePoints *= 2
                Log.d("PowerUp", "Double Points applied to ${player.name}.")
            }
            PowerUpType.SCORE_MULTIPLIER -> {
                player.basePoints *= 5
                Log.d("PowerUp", "Score Multiplier applied: 5x points for ${player.name}.")
            }
            PowerUpType.BONUS_POINTS -> {
                player.addBonusPoints(effectValue)
                Log.d("PowerUp", "Bonus Points applied: ${effectValue} bonus points added for ${player.name}.")
            }
            PowerUpType.EXTRA_TIME -> {
                player.timePlayed += effectValue.toLong()
                Log.d("PowerUp", "Extra Time applied: ${effectValue}ms added for ${player.name}.")
            }
            PowerUpType.IMMUNITY -> {
                player.isSilenced = false
                Log.d("PowerUp", "Immunity applied: ${player.name} is no longer silenced.")
            }
            else -> {
                Log.w("PowerUp", "Unknown or unimplemented power-up type: $type")
                // Handle custom or unimplemented power-ups
            }
        }
    }

    // Function to deactivate the power-up
    fun deactivatePowerUp(player: Player) {
        isActive = false
        Log.d("PowerUp", "Deactivating power-up $type for player ${player.name}.")
        when (type) {
            // Add cases if you need to handle specific deactivation logic for power-ups
            else -> {
                Log.d("PowerUp", "No deactivation logic needed for $type.")
            }
        }
    }

    // Parcelable implementation for passing PowerUp objects between activities/fragments
    constructor(parcel: Parcel) : this(
        type = PowerUpType.valueOf(parcel.readString() ?: PowerUpType.DOUBLE_POINTS.name),
        isActive = parcel.readByte() != 0.toByte(),
        duration = parcel.readLong(),
        effectValue = parcel.readInt(),
        startTime = parcel.readLong(),
        level = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeLong(duration)
        parcel.writeInt(effectValue)
        parcel.writeLong(startTime)
        parcel.writeInt(level)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PowerUp> {
        override fun createFromParcel(parcel: Parcel): PowerUp = PowerUp(parcel)
        override fun newArray(size: Int): Array<PowerUp?> = arrayOfNulls(size)
    }
}
