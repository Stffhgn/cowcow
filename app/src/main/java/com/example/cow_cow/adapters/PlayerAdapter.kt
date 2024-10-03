package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

// PlayerAdapter now extends ListAdapter with DiffUtil for better performance
class PlayerAdapter(
    private val onPlayerClick: (Player) -> Unit // Lambda to handle player click events
) : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback()) {

    private var players: List<Player> = listOf()

    // ViewHolder to bind player data to the UI
    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"

            // Handle item clicks
            binding.root.setOnClickListener {
                onPlayerClick(player)
            }
        }
    }

    // Inflate the item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerBinding.inflate(inflater, parent, false)
        return PlayerViewHolder(binding)
    }

    // Bind player data to the ViewHolder
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = players.size

    // Optional: A method to update data using submitList with DiffUtil
    //fun updatePlayers(newPlayers: List<Player>) {
    //    submitList(newPlayers)
    //}
}

// DiffUtil Callback to handle efficient updates
class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Assuming player ID is unique
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Compare all fields to check if content is the same
        return oldItem == newItem
    }
}
