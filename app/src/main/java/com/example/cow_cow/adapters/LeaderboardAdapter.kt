package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemLeaderboardBinding
import com.example.cow_cow.models.Player

class LeaderboardAdapter(
    private var players: List<Player>
) : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player, rank: Int) {
            binding.playerRankTextView.text = "#$rank"
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLeaderboardBinding.inflate(inflater, parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(players[position], position + 1)
    }

    override fun getItemCount(): Int = players.size

    fun updateData(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }
}
