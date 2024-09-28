package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

class PlayerDrawerAdapter(
    private var teamPlayers: List<Player>,
    private var individualPlayers: List<Player>,
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerDrawerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"

            // Handle click events for each player
            binding.root.setOnClickListener {
                onPlayerClick(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerBinding.inflate(inflater, parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = if (position < teamPlayers.size) {
            teamPlayers[position]
        } else {
            individualPlayers[position - teamPlayers.size]
        }
        holder.bind(player)
    }

    override fun getItemCount(): Int = teamPlayers.size + individualPlayers.size

    fun updateData(newTeamPlayers: List<Player>, newIndividualPlayers: List<Player>) {
        teamPlayers = newTeamPlayers
        individualPlayers = newIndividualPlayers
        notifyDataSetChanged()
    }
}
