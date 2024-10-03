package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.RankType

data class Player(
    val id: Int,
    var name: String,
    var cowCount: Int = 0,               // Number of cows spotted
    var churchCount: Int = 0,            // Number of churches spotted
    var waterTowerCount: Int = 0,        // Number of water towers spotted
    var isOnTeam: Boolean = false,       // Is the player part of a team?
    var isCurrentPlayer: Boolean = false,// Is this the current player in focus?
    var basePoints: Int = 0,             // Base points earned by the player
    var penaltyPoints: Int = 0,          // Points deducted due to penalties
    var bonusPoints: Int = 0,            // Bonus points earned
    var penalties: MutableList<Penalty> = mutableListOf(),   // List of active penalties
    var achievements: MutableList<Achievement> = mutableListOf(), // List of achievements earned
    var customRules: MutableList<CustomRule> = mutableListOf(),   // Custom rules applied to the player
    var activePowerUps: MutableList<PowerUp> = mutableListOf(),   // List of active power-ups
    var timePlayed: Long = 0L,           // Total time spent playing
    var distanceTraveled: Float = 0f,    // Total distance traveled
    var teamId: Int? = null,             // Team ID (if applicable)
    var customRule: CustomRule? = null,  // Custom rule affecting the player
    var isSilenced: Boolean = false,     // Is the player silenced by a penalty?
    var isPowerUpActive: Boolean = false,// Is any power-up active?
    var timeSpent: Long = 0L,            // Total time spent in the game
    var winStreak: Int = 0,              // Number of consecutive wins
    var objectivesCompleted: Int = 0,    // Number of objectives completed
    var notificationsEnabled: Boolean = true,  // Are notifications enabled for the player?
    var gamesPlayed: Int = 0,            // Number of games the player has played
    var rank: RankType = RankType.BEGINNER // Player's rank (default is "Beginner")
) : Parcelable {

    // --- Point Management ---

    /**
     * Adds base points to the player.
     * Use this method when awarding standard points, not affected by any modifiers or rules.
     */
    fun addBasePoints(points: Int) {
        basePoints += points
        Log.d("Player", "Added $points base points to player $name. Total base points: $basePoints.")
    }

    /**
     * Adds bonus points to the player.
     * Use this for extra points from achievements, power-ups, etc.
     */
    fun addBonusPoints(points: Int) {
        bonusPoints += points
        Log.d("Player", "Added $points bonus points to player $name. Total bonus points: $bonusPoints.")
    }

    /**
     * Applies penalties to the player.
     * Use this for subtracting penalty points based on rules or actions.
     */
    fun applyPenalty(penalty: Penalty) {
        penalties.add(penalty)
        penaltyPoints += penalty.pointsDeducted
        Log.d("Player", "Applied penalty '${penalty.name}' to player $name. Penalty points: $penaltyPoints.")
    }

    /**
     * Calculates the total points for the player, including base points, bonuses, penalties,
     * and any active power-ups.
     */
    fun calculateTotalPoints(): Int {
        var totalPoints = basePoints + bonusPoints

        // Apply penalty points
        totalPoints -= penaltyPoints
        Log.d("Player", "After applying penalty points: $totalPoints.")

        // Apply power-up effects
        activePowerUps.filter { it.isActive }.forEach { powerUp ->
            when (powerUp.type) {
                PowerUpType.DOUBLE_POINTS -> {
                    totalPoints *= 2
                    Log.d("PowerUpManager", "Double Points applied: Total points doubled for player $name.")
                }
                PowerUpType.SCORE_MULTIPLIER -> {
                    totalPoints += powerUp.effectValue
                    Log.d("PowerUpManager", "Score Multiplier applied: ${powerUp.effectValue} points added for player $name.")
                }
                PowerUpType.BONUS_POINTS -> {
                    totalPoints += powerUp.effectValue
                    Log.d("PowerUpManager", "Bonus Points applied: ${powerUp.effectValue} points added for player $name.")
                }
                PowerUpType.EXTRA_TIME -> {
                    Log.d("PowerUpManager", "Extra Time applied for player $name: No direct impact on points.")
                }
                else -> {
                    Log.w("PowerUpManager", "Unknown PowerUpType: ${powerUp.type} applied to player $name. No effect on points.")
                }
            }
        }

        // Apply team bonus if the player is part of a team
        if (isOnTeam) {
            totalPoints += 5
            Log.d("Player", "Team bonus applied: 5 points added for player $name. Total points: $totalPoints.")
        }

        return totalPoints
    }

    // --- Game State Management ---

    /**
     * Increments the number of games played by the player.
     */
    fun incrementGamesPlayed() {
        gamesPlayed++
        Log.d("Player", "Incremented games played for player $name. Games played: $gamesPlayed.")
    }

    /**
     * Check if the player is currently penalized.
     * A player is penalized if they have any active penalties.
     */
    fun isPenalized(): Boolean {
        val penalized = penalties.any { it.isActive }
        Log.d("Player", "Player $name is penalized: $penalized.")
        return penalized
    }

    /**
     * Removes expired penalties from the player's list.
     */
    fun removeExpiredPenalties(): Boolean {
        val removed = penalties.removeAll { !it.isActive }
        Log.d("Player", "Removed expired penalties for player $name. Any removed: $removed.")
        return removed
    }

    /**
     * Update the rank of the player based on the provided rank string.
     * This function attempts to convert the given string into a valid RankType.
     * If the provided rank is invalid, the current rank remains unchanged.
     *
     * @param newRank The new rank for the player, provided as a string (e.g., "BEGINNER", "EXPERT").
     */
    fun updateRank(newRank: String) {
        Log.d("Player", "Updating rank for player $name to $newRank")

        // Attempt to convert the newRank string to a valid RankType.
        val rankType = try {
            // Converts the provided string to uppercase to match the RankType enum values.
            RankType.valueOf(newRank.uppercase())
        } catch (e: IllegalArgumentException) {
            // Log an error if the provided rank is invalid and do not update the player's rank.
            Log.e("Player", "Invalid rank: $newRank. Keeping current rank.")
            return
        }

        // If the conversion is successful, update the player's rank.
        this.rank = rankType
        Log.d("Player", "Rank updated successfully for player $name to ${this.rank}")
    }


    /**
     * Add an achievement to the player's list.
     */
    fun addAchievement(achievement: Achievement) {
        achievements.add(achievement)
        Log.d("Player", "Added achievement '${achievement.name}' to player $name.")
    }

    /**
     * Apply a custom rule to the player.
     * This could affect gameplay or scoring based on the rule type.
     */
    fun applyCustomRule(rule: CustomRule) {
        customRules.add(rule)
        Log.d("Player", "Applied custom rule '${rule.ruleName}' to player $name.")
    }

    // --- Parcelable Implementation ---

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString() ?: "",
        cowCount = parcel.readInt(),
        churchCount = parcel.readInt(),
        waterTowerCount = parcel.readInt(),
        isOnTeam = parcel.readByte() != 0.toByte(),
        isCurrentPlayer = parcel.readByte() != 0.toByte(),
        basePoints = parcel.readInt(),
        penaltyPoints = parcel.readInt(),
        bonusPoints = parcel.readInt(),
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
        isPowerUpActive = parcel.readByte() != 0.toByte(),
        gamesPlayed = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(cowCount)
        parcel.writeInt(churchCount)
        parcel.writeInt(waterTowerCount)
        parcel.writeByte(if (isOnTeam) 1 else 0)
        parcel.writeByte(if (isCurrentPlayer) 1 else 0)
        parcel.writeInt(basePoints)
        parcel.writeInt(penaltyPoints)
        parcel.writeInt(bonusPoints)
        parcel.writeList(penalties)
        parcel.writeList(achievements)
        parcel.writeList(customRules)
        parcel.writeList(activePowerUps)
        parcel.writeLong(timePlayed)
        parcel.writeFloat(distanceTraveled)
        parcel.writeValue(teamId)
        parcel.writeByte(if (isSilenced) 1 else 0)
        parcel.writeByte(if (isPowerUpActive) 1 else 0)
        parcel.writeInt(gamesPlayed)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}
