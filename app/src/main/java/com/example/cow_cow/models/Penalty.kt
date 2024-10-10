package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PenaltyType

// Global function to calculate points to deduct based on penalty type
fun calculatePenaltyPoints(penaltyType: PenaltyType): Int {
    return when (penaltyType) {
        PenaltyType.FALSE_CALL -> 10    // Deduct 10 points for false call
        PenaltyType.SILENCED -> 5       // Deduct 5 points for being silenced
        PenaltyType.POINT_DEDUCTION -> 15 // General point deduction penalty
        PenaltyType.TIME_PENALTY -> 20  // Deduct 20 points for time penalties
        else -> {
            // Default case for any unexpected penalty types
            Log.w("Penalty", "Unknown penalty type: \${penaltyType.name}. No points deducted.")
            0  // No deduction for unhandled penalty types
        }
    }
}

data class Penalty(
    val id: String,                             // Unique ID for the penalty
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

    /**
     * Checks if a penalty has expired based on duration or remaining rounds.
     * @param currentTime The current time to check against.
     * @param currentRound The current round, if applicable.
     * @return Boolean indicating whether the penalty has expired.
     */
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

    /**
     * Reduces the number of rounds remaining for round-based penalties.
     */
    fun reduceRounds() {
        roundsRemaining?.let {
            if (it > 0) roundsRemaining = it - 1
            Log.d("Penalty", "Reduced rounds remaining for penalty $name: $roundsRemaining")
        }
    }

    /**
     * Applies the penalty to a player's points (with the multiplier if applicable).
     * @param player The player to whom the penalty is applied.
     */
    fun applyPenalty(player: Player) {
        when (penaltyType) {
            PenaltyType.POINT_DEDUCTION -> {
                val effectiveDeduction = (pointsDeducted * multiplier).toInt()
                player.basePoints -= effectiveDeduction
                Log.d("Penalty", "Applied penalty $name to player \${player.name}. Deducted \$effectiveDeduction points.")
            }
            PenaltyType.SILENCED -> {
                player.isSilenced = true
                Log.d("Penalty", "Player \${player.name} has been silenced due to penalty: $name.")
            }
            PenaltyType.TEMPORARY_BAN -> {
                player.isSilenced = true
                Log.d("Penalty", "Player \${player.name} has been temporarily banned due to penalty: $name.")
            }
            PenaltyType.FALSE_CALL -> {
                val effectiveDeduction = (pointsDeducted * multiplier).toInt()
                player.basePoints -= effectiveDeduction
                Log.d("Penalty", "Applied false call penalty $name to player \${player.name}. Deducted \$effectiveDeduction points.")
            }
            PenaltyType.TIME_PENALTY -> {
                player.timePlayed += duration
                Log.d("Penalty", "Applied time penalty $name to player \${player.name}. Added \$duration milliseconds to player's time penalty.")
            }
            PenaltyType.OTHER -> {
                Log.d("Penalty", "Applied custom penalty $name to player \${player.name}.")
            }
        }
    }

    // Parcelable implementation for passing Penalty between activities/fragments
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",  // id
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

        /**
         * Generates a unique penalty ID by combining playerId and penalty type ordinal.
         * @param playerId The ID of the player receiving the penalty.
         * @param penaltyType The type of penalty being applied.
         * @return A unique penalty ID.
         */
        fun generatePenaltyId(playerId: String, penaltyType: PenaltyType): Int {
            return (playerId.hashCode() * 100) + penaltyType.ordinal
        }
    }
}