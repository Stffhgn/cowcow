package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.databinding.ItemTeamBinding
import com.example.cow_cow.models.Player

class PlayerDrawerAdapter(
    private var team: List<Player>,
    private var individualPlayers: List<Player>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_TEAM = 0
    private val ITEM_TYPE_PLAYER = 1

    override fun getItemViewType(position: Int): Int {
        return if (team.isNotEmpty() && position == 0) ITEM_TYPE_TEAM else ITEM_TYPE_PLAYER
    }

    override fun getItemCount(): Int {
        return if (team.isNotEmpty()) individualPlayers.size + 1 else individualPlayers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_TYPE_TEAM) {
            val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TeamViewHolder(binding)
        } else {
            val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PlayerViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TeamViewHolder) {
            holder.bind(team)
        } else if (holder is PlayerViewHolder) {
            val index = if (team.isNotEmpty()) position - 1 else position
            holder.bind(individualPlayers[index], team)
        }
    }

    inner class TeamViewHolder(private val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: List<Player>) {
            binding.teamNameTextView.text = "Team"
            val combinedScore = team.sumBy { it.cowCount + it.churchCount * 2 + it.waterTowerCount * 3 }
            binding.teamScoreTextView.text = "Score: $combinedScore"
            val teamMemberNames = team.joinToString(", ") { it.name }
            binding.teamMembersTextView.text = "Members: $teamMemberNames"
        }
    }

    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player, team: List<Player>) {
            binding.playerNameTextView.text = player.name
            val totalScore = player.totalScore
            binding.playerScoreTextView.text = "Score: $totalScore"
            binding.playerStatusTextView.text = if (team.contains(player)) "On Team" else "Individual"
        }
    }

    fun updateData(newTeam: List<Player>, newIndividualPlayers: List<Player>) {
        team = newTeam
        individualPlayers = newIndividualPlayers
        notifyDataSetChanged()
    }
}
