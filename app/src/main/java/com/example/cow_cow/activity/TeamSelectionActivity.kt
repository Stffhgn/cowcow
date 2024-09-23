package com.example.cow_cow.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.ActivityTeamSelectionBinding
import com.example.cow_cow.models.Player

class TeamSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamSelectionBinding
    private lateinit var playerAdapter: PlayerAdapter
    private var teamPlayers: MutableList<Player> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityTeamSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the team list from the intent
        val teamList = intent.getParcelableArrayListExtra<Player>("TEAM_LIST")
        if (teamList != null) {
            teamPlayers = teamList.toMutableList()
        }

        // Set up the RecyclerView and Adapter
        binding.teamRecyclerView.layoutManager = LinearLayoutManager(this)
        playerAdapter = PlayerAdapter(teamPlayers) { player ->
            // Optional: Implement logic to remove player from team
            // removePlayerFromTeam(player)
        }
        binding.teamRecyclerView.adapter = playerAdapter

        // Handle back button click
        binding.backToGameButton.setOnClickListener {
            finish() // Return to the previous activity
        }
    }

    // Optional: Function to remove a player from the team
    private fun removePlayerFromTeam(player: Player) {
        teamPlayers.remove(player)
        playerAdapter.updatePlayers(teamPlayers)
        // You might want to update the team in GameActivity as well
    }
}