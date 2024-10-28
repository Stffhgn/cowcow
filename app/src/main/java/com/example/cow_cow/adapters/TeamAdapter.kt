package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemPlayerBinding
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player

class TeamAdapter(
    private var teamPlayers: MutableList<Player>,
    private val scoreManager: ScoreManager, // Injected ScoreManager for score calculations
    private val onPlayerClick: ((Player) -> Unit)? = null // Optional lambda for player click actions
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(private val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: Player) {
            binding.playerNameTextView.text = player.name

            // Calculate total points using the ScoreManager
            val totalPoints = scoreManager.calculatePlayerScore(player)
            binding.playerScoreTextView.text = "Score: $totalPoints"

            // Handle player clicks if the lambda is provided
            binding.root.setOnClickListener {
                onPlayerClick?.invoke(player)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerBinding.inflate(inflater, parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teamPlayers[position])
    }

    override fun getItemCount(): Int = teamPlayers.size

    // Method to update the team player data
    fun updateData(newTeamPlayers: List<Player>) {
        teamPlayers.clear()
        teamPlayers.addAll(newTeamPlayers)
        notifyDataSetChanged()
    }

    // Optional method to remove a player from the team (if needed)
    fun removePlayer(player: Player) {
        teamPlayers.remove(player)
        notifyDataSetChanged()
    }

    // Optional method to add a player to the team (if needed)
    fun addPlayer(player: Player) {
        teamPlayers.add(player)
        notifyItemInserted(teamPlayers.size - 1)
    }
}
