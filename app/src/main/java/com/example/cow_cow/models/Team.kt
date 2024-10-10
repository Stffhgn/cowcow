package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class Team(
    var id: String,                                 // Unique identifier for the team
    var name: String,                            // Name of the team
    var members: MutableList<Player> = mutableListOf(),  // List of team members (players)
    var teamScore: Int = 0,                      // Total team score
    var teamBonuses: MutableList<TeamBonus> = mutableListOf(),  // List of active team bonuses
    var achievements: MutableList<Achievement> = mutableListOf(), // List of team achievements
    var teamWins: Int = 0,                       // Count of team victories
    var teamLosses: Int = 0,                     // Count of team losses
    var teamPenalties: MutableList<Penalty> = mutableListOf(),   // List of penalties applied to the team
    var teamRank: Int = 0                        // Current ranking of the team (if applicable)
) : Parcelable {

    // Function to add a player to the team
    fun addMember(player: Player) {
        if (!members.contains(player)) {
            members.add(player)
            Log.d("Team", "Player ${player.name} added to team $name.")
        } else {
            Log.d("Team", "Player ${player.name} is already a member of team $name.")
        }
    }

    // Function to remove a player from the team
    fun removeMember(player: Player) {
        if (members.remove(player)) {
            Log.d("Team", "Player ${player.name} removed from team $name.")
        } else {
            Log.d("Team", "Player ${player.name} was not found in team $name.")
        }
    }

    // Function to calculate total team score, including bonuses
    fun calculateTotalTeamScore(): Int {
        var totalScore = teamScore
        teamBonuses.filter { it.isActive }.forEach { bonus ->
            totalScore += bonus.effectValue
        }
        Log.d("Team", "Total score for team $name calculated: $totalScore")
        return totalScore
    }

    // Function to apply a team bonus
    fun applyTeamBonus(bonus: TeamBonus) {
        teamBonuses.add(bonus)
        Log.d("Team", "Bonus ${bonus.type} applied to team $name. Effect value: ${bonus.effectValue}")
    }

    // Function to add an achievement to the team
    fun addAchievement(achievement: Achievement) {
        achievements.add(achievement)
        Log.d("Team", "Achievement ${achievement.name} added to team $name.")
    }

    // Function to apply a penalty to the team
    fun applyTeamPenalty(penalty: Penalty) {
        teamPenalties.add(penalty)
        teamScore -= penalty.pointsDeducted
        Log.d("Team", "Penalty ${penalty.name} applied to team $name. Deducted ${penalty.pointsDeducted} points.")
    }

    // Function to update team ranking (if using a ranking system)
    fun updateTeamRank(rank: Int) {
        teamRank = rank
        Log.d("Team", "Team $name rank updated to $teamRank.")
    }

    // Parcelable implementation to allow Team objects to be passed between Activities/Fragments
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        members = parcel.createTypedArrayList(Player.CREATOR) ?: mutableListOf(),
        teamScore = parcel.readInt(),
        teamBonuses = parcel.createTypedArrayList(TeamBonus.CREATOR) ?: mutableListOf(),
        achievements = parcel.createTypedArrayList(Achievement.CREATOR) ?: mutableListOf(),
        teamWins = parcel.readInt(),
        teamLosses = parcel.readInt(),
        teamPenalties = parcel.createTypedArrayList(Penalty.CREATOR) ?: mutableListOf(),
        teamRank = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeTypedList(members)
        parcel.writeInt(teamScore)
        parcel.writeTypedList(teamBonuses)
        parcel.writeTypedList(achievements)
        parcel.writeInt(teamWins)
        parcel.writeInt(teamLosses)
        parcel.writeTypedList(teamPenalties)
        parcel.writeInt(teamRank)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Team> {
        override fun createFromParcel(parcel: Parcel): Team = Team(parcel)
        override fun newArray(size: Int): Array<Team?> = arrayOfNulls(size)
    }
}