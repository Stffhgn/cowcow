package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import com.example.cow_cow.controllers.GpsLog
import com.example.cow_cow.controllers.ItemCallDetail
import com.example.cow_cow.enums.RankType

data class Player(
    val id: String,
    var name: String,
    var cowCount: Int = 0,
    var churchCount: Int = 0,
    var waterTowerCount: Int = 0,
    var isOnTeam: Boolean = false,
    var isCurrentPlayer: Boolean = false,
    var basePoints: Int = 0,
    var penaltyPoints: Int = 0,
    var bonusPoints: Int = 0,
    var timePlayed: Long = 0L,
    var isSilenced: Boolean = false,
    var customRule: CustomRule? = null,
    var distanceTraveled: Float = 0f,
    var teamId: String? = null,
    var timeSpent: Long = 0L,
    var winStreak: Int = 0,
    var objectivesCompleted: Int = 0,
    var notificationsEnabled: Boolean = true,
    var gamesPlayed: Int = 0,
    var rank: RankType = RankType.BEGINNER,
    var objectsCalled: MutableList<String> = mutableListOf(),
    var purchasedItems: MutableList<ScavengerHuntItem> = mutableListOf(),
    var premiumAccess: Boolean = false,
    var achievements: MutableList<Achievement> = mutableListOf(),
    var penalties: MutableList<Penalty> = mutableListOf(),
    var calledItemsDetails: MutableList<ItemCallDetail> = mutableListOf(),
    var heldPowerUps: MutableList<PowerUp> = mutableListOf(),
    var activePowerUps: MutableList<PowerUp> = mutableListOf(), // Added activePowerUps
    var hasActiveShield: Boolean = false,
    var gpsLogs: MutableList<GpsLog> = mutableListOf(),
    var customRules: MutableList<CustomRule> = mutableListOf(), // Add this property to store custom rules

) : Parcelable {

    // --- Parcelable Constructor ---
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
        timePlayed = parcel.readLong(),
        isSilenced = parcel.readByte() != 0.toByte(),
        customRule = parcel.readParcelable(CustomRule::class.java.classLoader),
        distanceTraveled = parcel.readFloat(),
        teamId = parcel.readString(),
        timeSpent = parcel.readLong(),
        winStreak = parcel.readInt(),
        objectivesCompleted = parcel.readInt(),
        notificationsEnabled = parcel.readByte() != 0.toByte(),
        gamesPlayed = parcel.readInt(),
        rank = RankType.valueOf(parcel.readString() ?: "BEGINNER"),
        objectsCalled = parcel.createStringArrayList() ?: mutableListOf(),
        purchasedItems = mutableListOf<ScavengerHuntItem>().apply {
            parcel.readList(this, ScavengerHuntItem::class.java.classLoader)
        },
        premiumAccess = parcel.readByte() != 0.toByte(),
        achievements = mutableListOf<Achievement>().apply {
            parcel.readList(this, Achievement::class.java.classLoader)
        },
        penalties = mutableListOf<Penalty>().apply {
            parcel.readList(this, Penalty::class.java.classLoader)
        },
        calledItemsDetails = mutableListOf<ItemCallDetail>().apply {
            parcel.readList(this, ItemCallDetail::class.java.classLoader)
        },
        heldPowerUps = mutableListOf<PowerUp>().apply {
            parcel.readList(this, PowerUp::class.java.classLoader)
        },
        activePowerUps = mutableListOf<PowerUp>().apply {
            parcel.readList(this, PowerUp::class.java.classLoader)
        },

        hasActiveShield = parcel.readByte() != 0.toByte(),

        gpsLogs = mutableListOf<GpsLog>().apply {
            parcel.readList(this, GpsLog::class.java.classLoader)
        },
        customRules = mutableListOf<CustomRule>().apply {
            parcel.readList(this, CustomRule::class.java.classLoader)
        }
    )

    // --- Write to Parcel ---
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
        parcel.writeLong(timePlayed)
        parcel.writeByte(if (isSilenced) 1 else 0)
        parcel.writeParcelable(customRule, flags)
        parcel.writeFloat(distanceTraveled)
        parcel.writeString(teamId)
        parcel.writeLong(timeSpent)
        parcel.writeInt(winStreak)
        parcel.writeInt(objectivesCompleted)
        parcel.writeByte(if (notificationsEnabled) 1 else 0)
        parcel.writeInt(gamesPlayed)
        parcel.writeString(rank.name)
        parcel.writeStringList(objectsCalled)
        parcel.writeList(purchasedItems)
        parcel.writeByte(if (premiumAccess) 1 else 0)
        parcel.writeList(achievements)
        parcel.writeList(penalties)
        parcel.writeList(calledItemsDetails)
        parcel.writeList(heldPowerUps)
        parcel.writeList(activePowerUps) // Added activePowerUps to parcel
        parcel.writeByte(if (hasActiveShield) 1 else 0)
        parcel.writeList(gpsLogs)
        parcel.writeList(customRules)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player = Player(parcel)
        override fun newArray(size: Int): Array<Player?> = arrayOfNulls(size)
    }
}
