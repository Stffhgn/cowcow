package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
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
        return currentTime >= startTime + duration
    }

    // Function to apply the power-up effect to a player
    fun applyPowerUp(player: Player) {
        if (!isActive) return

        when (type) {
            PowerUpType.DOUBLE_POINTS -> player.basePoints *= 2
            PowerUpType.SCORE_MULTIPLIER -> player.basePoints *= 5
            PowerUpType.BONUS_POINTS -> player.addBonusPoints(effectValue)
            PowerUpType.EXTRA_TIME -> player.timePlayed += effectValue.toLong()
            PowerUpType.IMMUNITY -> player.isSilenced = false // Removes silencing penalties
            PowerUpType.SPEED_BOOST -> player.speed += effectValue
            PowerUpType.EXTRA_LIFE -> player.lives += 1
            PowerUpType.INVISIBILITY -> player.isInvisible = true
            PowerUpType.SCORE_MULTIPLIER -> player.basePoints *= effectValue
            PowerUpType.FREEZE_ENEMIES -> player.freezeEnemies(duration)
            PowerUpType.HEALTH_REGEN -> player.restoreHealth(effectValue)
            // Add more cases as needed based on the PowerUpType enum
            else -> {
                // Handle custom or unimplemented power-ups
            }
        }
    }

    // Function to deactivate the power-up
    fun deactivatePowerUp(player: Player) {
        isActive = false
        when (type) {
            PowerUpType.INVISIBILITY -> player.isInvisible = false
            PowerUpType.SPEED_BOOST -> player.speed -= effectValue
            // Handle deactivation for other power-ups as necessary
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
