package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    private val leaderboardViewModel: LeaderboardViewModel by viewModels()
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var playerController: PlayerController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        leaderboardRecyclerView = view.findViewById(R.id.leaderboardRecyclerView)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize PlayerController with the list of players from the ViewModel
        val players = leaderboardViewModel.leaderboard.value ?: emptyList()
        playerController = PlayerController(players.toMutableList())

        // Initialize RecyclerView adapter
        leaderboardAdapter = LeaderboardAdapter(players)
        leaderboardRecyclerView.adapter = leaderboardAdapter

        // Observe leaderboard data from ViewModel
        leaderboardViewModel.leaderboard.observe(viewLifecycleOwner, Observer { leaderboard ->
            leaderboardAdapter.updateLeaderboard(leaderboard)
        })

        // Fetch the latest leaderboard data
        leaderboardViewModel.loadLeaderboard()

        return view
    }

    /**
     * Adapter for displaying the leaderboard items in the RecyclerView.
     */
    inner class LeaderboardAdapter(private var players: List<Player>) :
        RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

        inner class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // Define UI elements for each leaderboard item
            val playerNameTextView: TextView = view.findViewById(R.id.playerNameTextView)
            val playerScoreTextView: TextView = view.findViewById(R.id.playerScoreTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
            return LeaderboardViewHolder(view)
        }

        override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
            val player = players[position]
            val totalPoints = playerController.calculateTotalPoints(player)
            holder.playerNameTextView.text = player.name
            holder.playerScoreTextView.text = totalPoints.toString()
        }

        override fun getItemCount(): Int = players.size

        /**
         * Update the leaderboard when new data is available.
         */
        fun updateLeaderboard(newPlayers: List<Player>) {
            players = newPlayers
            playerController = PlayerController(players.toMutableList()) // Update the controller with the new player list
            notifyDataSetChanged()
        }
    }
}
