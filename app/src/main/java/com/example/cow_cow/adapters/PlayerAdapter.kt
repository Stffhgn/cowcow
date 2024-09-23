package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

class PlayerAdapter(
    private var players: MutableList<Player>, // Make this mutable for dynamic updates
    private val onPlayerClick: (Player) -> Unit // Pass player object on click
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player)
    }

    override fun getItemCount(): Int = players.size

    // Method to update the players list and notify the adapter of data change
    fun updatePlayers(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)
        notifyDataSetChanged() // Refresh the adapter to reflect the new data
    }

    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.totalScore}"

            // Set click listener for each player item
            binding.root.setOnClickListener {
                onPlayerClick(player) // Trigger the callback with the selected player
            }
        }
    }
}