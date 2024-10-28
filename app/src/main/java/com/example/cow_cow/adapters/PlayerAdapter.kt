package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.PlayerDiffCallback
import com.example.cow_cow.managers.ScoreManager

// PlayerAdapter extends ListAdapter with DiffUtil for better performance
class PlayerAdapter(
    private var isWhoCalledItContext: Boolean = false, // true if being used in WhoCalledItFragment
    private val onPlayerClick: (Player) -> Unit, // Lambda to handle player click events
    private val scoreManager: ScoreManager // Injected ScoreManager for score calculations
) : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback()) {

    // ViewHolder to display player data
    inner class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = scoreManager.calculatePlayerScore(player).toString()

            // Update team status and background color based on team membership
            binding.teamStatusTextView.text = if (player.isOnTeam) {
                "In Team"
            } else {
                "Not in Team"
            }

            val backgroundColor = if (player.isOnTeam) {
                R.color.colorOnTeam
            } else {
                R.color.colorNotOnTeam
            }
            binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, backgroundColor))

            // Handle player item click
            binding.root.setOnClickListener {
                onPlayerClick(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position)
        holder.bind(player)
    }
}
