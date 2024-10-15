package com.example.cow_cow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

// PlayerAdapter now extends ListAdapter with DiffUtil for better performance
class PlayerAdapter(
    private var isWhoCalledItContext: Boolean = false, // true if being used in WhoCalledItFragment
    private val onPlayerClick: (Player) -> Unit // Lambda to handle player click events
) : ListAdapter<Player, PlayerAdapter.PlayerViewHolder>(PlayerDiffCallback()) {

    // Log tag for debugging
    private val TAG = "PlayerAdapter"

    // Make this method public to allow access from outside of PlayerAdapter
    fun setWhoCalledItContext(isContext: Boolean) {
        isWhoCalledItContext = isContext
        notifyDataSetChanged() // Notify that data set has changed to refresh the UI
    }

    // ViewHolder to bind player data to the UI
    inner class PlayerViewHolder(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind player data to the view and handle click events
        fun bind(player: Player) {
            // Log data binding action
            Log.d(TAG, "Binding player: ${player.name}, ID: ${player.id}")

            // Set player details
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"

            // Set checkbox state to indicate if the player is the current one
            binding.isPlayingCheckbox.isChecked = player.isCurrentPlayer

            // Control visibility of elements based on the context
            binding.avatarImageView.visibility = if (isWhoCalledItContext) View.GONE else View.VISIBLE
            binding.isPlayingCheckbox.visibility = if (isWhoCalledItContext) View.VISIBLE else View.GONE

            // Handle item clicks and log when a player is clicked
            binding.root.setOnClickListener {
                Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")
                onPlayerClick(player)
            }

            // Checkbox listener for updating player status
            binding.isPlayingCheckbox.setOnCheckedChangeListener { _, isChecked ->
                player.isCurrentPlayer = isChecked
                Log.d(TAG, "Player ${player.name} checkbox changed: $isChecked")
                // Update ViewModel or perform any actions as needed
            }
        }
    }

    // Inflate the item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerBinding.inflate(inflater, parent, false)
        Log.d(TAG, "Creating new ViewHolder")
        return PlayerViewHolder(binding)
    }

    // Bind player data to the ViewHolder
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = getItem(position) // Assuming you're using a DiffUtil-based adapter (ListAdapter), use getItem() to retrieve the player
        Log.d("PlayerAdapter", "Binding player: ${player.name}, Is on team: ${player.isOnTeam}")
        holder.bind(player)

        // Set background color based on whether the player is on a team
        if (player.isOnTeam) {
            holder.binding.teamStatusTextView.text = "In Team"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorOnTeam))
        } else {
            holder.binding.teamStatusTextView.text = "Not in Team"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorNotOnTeam))
        }

        // Handle player item click
        holder.itemView.setOnClickListener {
            Log.d("PlayerAdapter", "Player clicked: ${player.name}, Current team status: ${player.isOnTeam}")
            onPlayerClick(player)
        }
    }
}

// DiffUtil Callback to handle efficient updates
class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Player ID should be unique
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Compare all fields to check if content is the same
        return oldItem == newItem
    }
}
