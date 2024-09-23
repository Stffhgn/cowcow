package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

class TeamAdapter(
    private var players: List<Player>,
    private val onPlayerTeamToggle: (Player) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player, onPlayerTeamToggle)
    }

    override fun getItemCount(): Int = players.size

    fun updatePlayers(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    class TeamViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player, onPlayerTeamToggle: (Player) -> Unit) {
            binding.playerNameTextView.text = player.name
            binding.root.setOnClickListener {
                onPlayerTeamToggle(player)
            }
        }
    }
}