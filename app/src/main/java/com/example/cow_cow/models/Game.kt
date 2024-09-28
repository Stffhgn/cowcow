package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import com.example.cow_cow.enums.GameMode
import com.example.cow_cow.enums.GameStatus
import com.example.cow_cow.enums.RuleEffectType

data class Game(
    var players: MutableList<Player> = mutableListOf(),  // List of players participating in the game
    var isTeamMode: Boolean = false,                    // Whether the game is played in team mode
    var team: Team? = null,                             // If in team mode, the team object is active
    var gameMode: GameMode = GameMode.CLASSIC,          // Game mode (e.g., classic, scavenger hunt, etc.)
    var status: GameStatus = GameStatus.NOT_STARTED,    // Current status of the game
    var activePowerUps: MutableList<PowerUp> = mutableListOf(),  // Active power-ups in the game
    var customRules: MutableList<CustomRule> = mutableListOf(),  // List of active custom rules
    var round: Int = 1,                                 // Tracks the current round in the game
    var totalRounds: Int = 10,                          // Total rounds (if applicable, for multi-round games)
    var elapsedTime: Long = 0L,                         // Time elapsed in the current game (in milliseconds)
    var maxTimeLimit: Long = 600000L                    // Maximum time limit for the game (default 10 minutes)
) : Parcelable {

    // Function to add a player to the game
    fun addPlayer(player: Player) {
        if (!players.contains(player)) {
            players.add(player)
        }
    }

    // Function to remove a player from the game
    fun removePlayer(player: Player) {
        players.remove(player)
    }

    // Function to start the game
    fun startGame() {
        if (players.isNotEmpty() && status == GameStatus.NOT_STARTED) {
            status = GameStatus.IN_PROGRESS
        }
    }

    // Function to pause the game
    fun pauseGame() {
        if (status == GameStatus.IN_PROGRESS) {
            status = GameStatus.PAUSED
        }
    }

    // Function to end the game
    fun endGame() {
        status = GameStatus.COMPLETED
    }

    // Function to apply a power-up to a player
    fun applyPowerUp(player: Player, powerUp: PowerUp) {
        if (players.contains(player) && !activePowerUps.contains(powerUp)) {
            activePowerUps.add(powerUp)
            player.applyPowerUpEffect(powerUp)
        }
    }

    // Function to apply a custom rule (with conditions and effects)
    fun applyCustomRule(rule: CustomRule) {
        if (!customRules.contains(rule)) {
            customRules.add(rule)
        }
    }

    // Function to check game time and apply penalties or bonuses based on elapsed time
    fun checkTimeLimit() {
        if (elapsedTime >= maxTimeLimit) {
            status = GameStatus.COMPLETED
        }
    }

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Player.CREATOR) ?: mutableListOf(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Team::class.java.classLoader),
        GameMode.valueOf(parcel.readString() ?: GameMode.CLASSIC.name),
        GameStatus.valueOf(parcel.readString() ?: GameStatus.NOT_STARTED.name),
        parcel.createTypedArrayList(PowerUp.CREATOR) ?: mutableListOf(),
        parcel.createTypedArrayList(CustomRule.CREATOR) ?: mutableListOf(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(players)
        parcel.writeByte(if (isTeamMode) 1 else 0)
        parcel.writeParcelable(team, flags)
        parcel.writeString(gameMode.name)
        parcel.writeString(status.name)
        parcel.writeTypedList(activePowerUps)
        parcel.writeTypedList(customRules)
        parcel.writeInt(round)
        parcel.writeInt(totalRounds)
        parcel.writeLong(elapsedTime)
        parcel.writeLong(maxTimeLimit)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game = Game(parcel)
        override fun newArray(size: Int): Array<Game?> = arrayOfNulls(size)
    }
}
