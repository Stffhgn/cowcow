package com.example.cow_cow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind player data to the view and handle click events
        fun bind(player: Player) {
            // Log data binding action
            Log.d(TAG, "Binding player: ${player.name}, ID: ${player.id}")

            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"

            // Set checkbox state
            binding.isPlayingCheckbox.isChecked = player.isCurrentPlayer

            // Control visibility of additional elements based on context
            if (isWhoCalledItContext) {
                // Show only the elements relevant to WhoCalledItFragment
                binding.avatarImageView.visibility = View.GONE
                binding.playerNameEditText.visibility = View.GONE
                binding.playerNameTextView.visibility = View.VISIBLE
                binding.playerScoreTextView.visibility = View.VISIBLE
                binding.isPlayingCheckbox.visibility = View.VISIBLE
            } else {
                // Default visibility for other contexts
                binding.avatarImageView.visibility = View.VISIBLE
                binding.playerNameEditText.visibility = View.VISIBLE
            }

            // Handle item clicks and log when a player is clicked
            binding.root.setOnClickListener {
                Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")
                onPlayerClick(player)
            }

            // (Optional) Set checkbox click listener if you need it
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
        val player = getItem(position)
        holder.bind(player)
    }
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
