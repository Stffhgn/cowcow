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
    var basePoints: Int = 0,
    var penaltyPoints: Int = 0,
    var bonusPoints: Int = 0, // Track bonus points separately for flexibility
    var penalties: MutableList<Penalty> = mutableListOf(),
    var achievements: MutableList<Achievement> = mutableListOf(),
    var customRules: MutableList<CustomRule> = mutableListOf(),
    var activePowerUps: MutableList<PowerUp> = mutableListOf(),
    var timePlayed: Long = 0L,
    var distanceTraveled: Float = 0f,
    var teamId: Int? = null,

    // Game states (flags)
    var isSilenced: Boolean = false,
    var isPowerUpActive: Boolean = false
) : Parcelable {

    // --- Point Management ---

    /**
     * Adds base points to the player.
     * Use this method when awarding standard points, not affected by any modifiers or rules.
     */
    fun addBasePoints(points: Int) {
        basePoints += points
    }

    /**
     * Adds bonus points to the player.
     * Use this for extra points from achievements, power-ups, etc.
     */
    fun addBonusPoints(points: Int) {
        bonusPoints += points
    }

    /**
     * Applies penalties to the player.
     * Use this for subtracting penalty points based on rules or actions.
     */
    fun applyPenalty(penalty: Penalty) {
        penalties.add(penalty)
        penaltyPoints += penalty.pointsDeducted
    }

    /**
     * Calculates the total points for the player, including base points, bonuses, penalties,
     * and any active power-ups.
     */
    fun calculateTotalPoints(): Int {
        var totalPoints = basePoints + bonusPoints

        // Apply penalty points
        totalPoints -= penaltyPoints

        // Apply power-up effects
        activePowerUps.filter { it.isActive }.forEach { powerUp ->
            when (powerUp.type) {
                PowerUpType.DOUBLE_POINTS -> totalPoints *= 2
                PowerUpType.EXTRA_POINTS -> totalPoints += powerUp.effectValue
                PowerUpType.BONUS_POINTS -> totalPoints += powerUp.effectValue
                PowerUpType.EXTRA_TIME -> {
                    // No direct impact on points, handled elsewhere (like in GameViewModel)
                }
                PowerUpType.FREEZE_PLAYER -> {
                    // No effect on points, but could apply a silencing effect elsewhere
                }
            }
        }

        // Apply team bonus if applicable
        if (isOnTeam) {
            totalPoints += 5
        }

        return totalPoints
    }

    // --- State Management ---

    /**
     * Check if the player is currently penalized.
     */
    fun isPenalized(): Boolean {
        return penalties.any { it.isActive }
    }

    /**
     * Remove expired penalties from the player's list.
     */
    fun removeExpiredPenalties(): Boolean {
        return penalties.removeAll { !it.isActive }
    }

    /**
     * Add an achievement to the player's list.
     */
    fun addAchievement(achievement: Achievement) {
        achievements.add(achievement)
    }

    /**
     * Apply a custom rule to the player.
     * This could affect gameplay or scoring based on the rule type.
     */
    fun applyCustomRule(rule: CustomRule) {
        customRules.add(rule)
    }

    // --- Parcelable Implementation ---

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString() ?: "",
        cowCount = parcel.readInt(),
        churchCount = parcel.readInt(),
        waterTowerCount = parcel.readInt(),
        isOnTeam = parcel.readByte() != 0.toByte(),
        basePoints = parcel.readInt(),
        penaltyPoints = parcel.readInt(),
        bonusPoints = parcel.readInt(), // Added for future-proofing
        penalties = mutableListOf<Penalty>().apply {
            parcel.readList(this, Penalty::class.java.classLoader)
        },
        achievements = mutableListOf<Achievement>().apply {
            parcel.readList(this, Achievement::class.java.classLoader)
        },
        customRules = mutableListOf<CustomRule>().apply {
            parcel.readList(this, CustomRule::class.java.classLoader)
        },
        activePowerUps = mutableListOf<PowerUp>().apply {
            parcel.readList(this, PowerUp::class.java.classLoader)
        },
        timePlayed = parcel.readLong(),
        distanceTraveled = parcel.readFloat(),
        teamId = parcel.readValue(Int::class.java.classLoader) as? Int,
        isSilenced = parcel.readByte() != 0.toByte(),
        isPowerUpActive = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(cowCount)
        parcel.writeInt(churchCount)
        parcel.writeInt(waterTowerCount)
        parcel.writeByte(if (isOnTeam) 1 else 0)
        parcel.writeInt(basePoints)
        parcel.writeInt(penaltyPoints)
        parcel.writeInt(bonusPoints) // Added for future-proofing
        parcel.writeList(penalties)
        parcel.writeList(achievements)
        parcel.writeList(customRules)
        parcel.writeList(activePowerUps)
        parcel.writeLong(timePlayed)
        parcel.writeFloat(distanceTraveled)
        parcel.writeValue(teamId)
        parcel.writeByte(if (isSilenced) 1 else 0)
        parcel.writeByte(if (isPowerUpActive) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}
