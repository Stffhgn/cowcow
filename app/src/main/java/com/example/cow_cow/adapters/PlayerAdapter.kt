package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.models.Player

/**
 * Adapter class for displaying a list of players in a RecyclerView.
 *
 * @param players List of Player objects to display.
 * @param onPlayerClick A lambda function that handles player item click events.
 */
class PlayerAdapter(
    private var players: MutableList<Player>,
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type.
     * Inflates the layout for each player item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        // Inflate the ItemPlayerBinding layout for each player item.
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the content of the ViewHolder with player details.
     */
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])  // Bind the player object at the current position.
    }

    /**
     * Returns the total number of players in the list.
     */
    override fun getItemCount(): Int = players.size

    /**
     * ViewHolder class that holds the view for each player item in the list.
     *
     * @param binding The view binding object for the player item layout.
     */
    inner class PlayerViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the player's data to the UI elements.
         *
         * @param player The Player object containing player details.
         */
        fun bind(player: Player) {
            // Set the player's name and total score in the corresponding TextViews.
            binding.playerNameTextView.text = player.name
            binding.playerScoreTextView.text = "Score: ${player.calculateTotalPoints()}"

            // Set an onClickListener to trigger the passed lambda function when the player is clicked.
            itemView.setOnClickListener { onPlayerClick(player) }
        }
    }

    /**
     * Updates the player list with new data and refreshes the RecyclerView.
     *
     * @param newPlayers The updated list of players.
     */
    fun updatePlayers(newPlayers: List<Player>) {
        players.clear()  // Clear the existing player list.
        players.addAll(newPlayers)  // Add the new player data.
        notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView UI.
    }
}