package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.RankType
import com.example.cow_cow.managers.GameManager.applyTimePenaltyToGame

data class Player(
    val id: String,
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
    var teamId: String? = null,          // Team ID (if applicable)
    var customRule: CustomRule? = null,  // Custom rule affecting the player
    var isSilenced: Boolean = false,     // Is the player silenced by a penalty?
    var isPowerUpActive: Boolean = false,// Is any power-up active?
    var timeSpent: Long = 0L,            // Total time spent in the game
    var winStreak: Int = 0,              // Number of consecutive wins
    var objectivesCompleted: Int = 0,    // Number of objectives completed
    var notificationsEnabled: Boolean = true,  // Are notifications enabled for the player?
    var gamesPlayed: Int = 0,            // Number of games the player has played
    var rank: RankType = RankType.BEGINNER, // Player's rank (default is "Beginner")
    var objectsCalled: MutableList<String> = mutableListOf()  // List to track called objects

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

        when (penalty.penaltyType) {
            PenaltyType.POINT_DEDUCTION -> {
                // Apply point deduction
                val effectiveDeduction = (penalty.pointsDeducted * penalty.multiplier).toInt()
                basePoints -= effectiveDeduction
                penaltyPoints += effectiveDeduction
                Log.d("Penalty", "Applied penalty '\${penalty.name}' to player $name. Deducted $effectiveDeduction points.")
            }
            PenaltyType.SILENCED -> {
                // Silence player for a specific duration
                isSilenced = true
                Log.d("Penalty", "Player $name has been silenced due to penalty: \${penalty.name}.")
            }
            PenaltyType.TEMPORARY_BAN -> {
                // Temporarily ban the player from making calls
                isSilenced = true
                Log.d("Penalty", "Player $name has been temporarily banned due to penalty: \${penalty.name}.")
            }
            PenaltyType.FALSE_CALL -> {
                // Apply deduction for false call
                val effectiveDeduction = (penalty.pointsDeducted * penalty.multiplier).toInt()
                basePoints -= effectiveDeduction
                penaltyPoints += effectiveDeduction
                Log.d("Penalty", "Applied false call penalty '\${penalty.name}' to player $name. Deducted $effectiveDeduction points.")
            }
            PenaltyType.TIME_PENALTY -> {
                applyTimePenaltyToGame(penalty.duration)
                Log.d("Penalty", "Applied time penalty '\${penalty.name}' to player $name. Reduced the game timer by \${penalty.duration} milliseconds.")
            }
            PenaltyType.OTHER -> {
                // Apply any other type of penalty
                Log.d("Penalty", "Applied custom penalty '\${penalty.name}' to player $name.")
            }
        }
    }

    /**
     * Adds an object (e.g., Cow, Church, Water Tower) to the player's list of called objects.
     * @param objectType The object that the player has called.
     */
    fun addObjectCalled(objectType: String) {
        objectsCalled.add(objectType)
        // Optionally, you can update player points or other logic here
        when (objectType) {
            "Cow" -> basePoints += 1
            "Church" -> basePoints += 2
            "Water Tower" -> basePoints += 3
            // Handle other object types if necessary
        }
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
                    Log.d("PowerUpManager", "Score Multiplier applied: \${powerUp.effectValue} points added for player $name.")
                }
                PowerUpType.BONUS_POINTS -> {
                    totalPoints += powerUp.effectValue
                    Log.d("PowerUpManager", "Bonus Points applied: \${powerUp.effectValue} points added for player $name.")
                }
                PowerUpType.EXTRA_TIME -> {
                    Log.d("PowerUpManager", "Extra Time applied for player $name: No direct impact on points.")
                }
                else -> {
                    Log.w("PowerUpManager", "Unknown PowerUpType: \${powerUp.type} applied to player $name. No effect on points.")
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
            RankType.valueOf(newRank.uppercase())
        } catch (e: IllegalArgumentException) {
            Log.e("Player", "Invalid rank: $newRank. Keeping current rank.")
            return
        }

        // Update the player's rank.
        this.rank = rankType
        Log.d("Player", "Rank updated successfully for player $name to ${this.rank}")
    }

    // --- Parcelable Implementation ---

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
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
        teamId = parcel.readString(),
        isSilenced = parcel.readByte() != 0.toByte(),
        isPowerUpActive = parcel.readByte() != 0.toByte(),
        gamesPlayed = parcel.readInt(),
        objectsCalled = parcel.createStringArrayList() ?: mutableListOf()  // Reading the list of objects called
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
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
        parcel.writeString(teamId)
        parcel.writeByte(if (isSilenced) 1 else 0)
        parcel.writeByte(if (isPowerUpActive) 1 else 0)
        parcel.writeInt(gamesPlayed)
        parcel.writeStringList(objectsCalled)  // Writing the list of objects called
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}