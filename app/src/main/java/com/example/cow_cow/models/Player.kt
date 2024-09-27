package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable

data class Player(
    val id: Int,
    var name: String,
    var cowCount: Int = 0,
    var churchCount: Int = 0,
    var waterTowerCount: Int = 0,
    var isOnTeam: Boolean = false,
    var isPowerUpActive: Boolean = false,
    var basePoints: Int = 0,
    var penaltyPoints: Int = 0,
    var penalties: MutableList<Penalty> = mutableListOf(),
    var isSilenced: Boolean = false,
    var achievements: MutableList<Achievement> = mutableListOf(),
    var customRules: MutableList<CustomRule> = mutableListOf(),
    var timePlayed: Long = 0L,
    var distanceTraveled: Float = 0f,
    var teamId: Int? = null,
    val activePowerUps: MutableList<PowerUp> = mutableListOf()
) : Parcelable {

    // Function to add points
    fun addPoints(points: Int) {
        basePoints += points
    }

    // Function to add base points without modifiers
    fun addBasePoints(points: Int) {
        basePoints += points
    }

    // Function to apply a penalty
    fun applyPenalty(penalty: Penalty) {
        penalties.add(penalty)
        penaltyPoints += penalty.pointsDeducted
    }

    // Function to calculate total points including modifiers
    fun calculateTotalPoints(): Int {
        var totalPoints = basePoints

        // Apply penalty points
        totalPoints -= penaltyPoints

        // Apply power-up effects
        activePowerUps.filter { it.isActive }.forEach { powerUp ->
            when (powerUp.type) {
                PowerUpType.DOUBLE_POINTS -> totalPoints *= 2
                PowerUpType.FREEZE_PLAYER -> {
                    // No effect on total points, but freeze the player for a duration (handle elsewhere)
                }
                PowerUpType.EXTRA_POINTS -> totalPoints += powerUp.effectValue  // Add extra points from the power-up
                PowerUpType.EXTRA_TIME -> {
                    // No effect on total points, but grant extra time (handle elsewhere)
                }
                PowerUpType.BONUS_POINTS -> totalPoints += powerUp.effectValue  // Add bonus points from the power-up
            }
        }

        // Apply team bonus if the player is on a team
        if (isOnTeam) {
            totalPoints += 5
        }

        return totalPoints
    }

    // Function to check if the player has active penalties
    fun isPenalized(): Boolean {
        return penalties.any { it.isActive }
    }

    // Function to remove expired penalties
    fun removeExpiredPenalties(): Boolean {
        return penalties.removeAll { !it.isActive }
    }

    // Function to add an achievement
    fun addAchievement(achievement: Achievement) {
        achievements.add(achievement)
    }

    // Function to add custom rules
    fun applyCustomRule(rule: CustomRule) {
        customRules.add(rule)
    }

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString() ?: "",
        cowCount = parcel.readInt(),
        churchCount = parcel.readInt(),
        waterTowerCount = parcel.readInt(),
        isOnTeam = parcel.readByte() != 0.toByte(),
        isPowerUpActive = parcel.readByte() != 0.toByte(),
        basePoints = parcel.readInt(),
        penaltyPoints = parcel.readInt(),
        penalties = mutableListOf<Penalty>().apply {
            parcel.readList(this, Penalty::class.java.classLoader)
        },
        isSilenced = parcel.readByte() != 0.toByte(),
        achievements = mutableListOf<Achievement>().apply {
            parcel.readList(this, Achievement::class.java.classLoader)
        },
        customRules = mutableListOf<CustomRule>().apply {
            parcel.readList(this, CustomRule::class.java.classLoader)
        },
        timePlayed = parcel.readLong(),
        distanceTraveled = parcel.readFloat(),
        teamId = parcel.readValue(Int::class.java.classLoader) as? Int,
        activePowerUps = mutableListOf<PowerUp>().apply {
            parcel.readList(this, PowerUp::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(cowCount)
        parcel.writeInt(churchCount)
        parcel.writeInt(waterTowerCount)
        parcel.writeByte(if (isOnTeam) 1 else 0)
        parcel.writeByte(if (isPowerUpActive) 1 else 0)
        parcel.writeInt(basePoints)
        parcel.writeInt(penaltyPoints)
        parcel.writeList(penalties)
        parcel.writeByte(if (isSilenced) 1 else 0)
        parcel.writeList(achievements)
        parcel.writeList(customRules)
        parcel.writeLong(timePlayed)
        parcel.writeFloat(distanceTraveled)
        parcel.writeValue(teamId)
        parcel.writeList(activePowerUps)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}
