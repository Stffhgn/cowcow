package com.example.cow_cow.models

import com.example.cow_cow.enums.TeamBonusType

data class Team(
    var id: Int,                                 // Unique identifier for the team
    var name: String,                            // Name of the team
    var members: MutableList<Player>,            // List of team members (players)
    var teamScore: Int = 0,                      // Total team score
    var teamBonuses: MutableList<TeamBonus> = mutableListOf(),  // List of active team bonuses
    var achievements: MutableList<Achievement> = mutableListOf(), // List of team achievements
    var teamWins: Int = 0,                       // Count of team victories
    var teamLosses: Int = 0,                     // Count of team losses
    var teamPenalties: MutableList<Penalty> = mutableListOf(),   // List of penalties applied to the team
    var teamRank: Int = 0                        // Current ranking of the team (if applicable)
) {

    // Function to add a player to the team
    fun addMember(player: Player) {
        if (!members.contains(player)) {
            members.add(player)
        }
    }

    // Function to remove a player from the team
    fun removeMember(player: Player) {
        members.remove(player)
    }

    // Function to calculate total team score, including bonuses
    fun calculateTotalTeamScore(): Int {
        var totalScore = teamScore
        teamBonuses.filter { it.isActive }.forEach { bonus ->
            totalScore += bonus.effectValue
        }
        return totalScore
    }

    // Function to apply a team bonus
    fun applyTeamBonus(bonus: TeamBonus) {
        teamBonuses.add(bonus)
    }

    // Function to add an achievement to the team
    fun addAchievement(achievement: Achievement) {
        achievements.add(achievement)
    }

    // Function to apply a penalty to the team
    fun applyTeamPenalty(penalty: Penalty) {
        teamPenalties.add(penalty)
        teamScore -= penalty.pointsDeducted
    }

    // Function to update team ranking (if using a ranking system)
    fun updateTeamRank(rank: Int) {
        teamRank = rank
    }
}
