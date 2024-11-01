package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.TeamAdapter
import com.example.cow_cow.databinding.FragmentTeamPlayersBinding
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.TeamViewModel
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.repositories.PlayerRepository

class TeamPlayersFragment : Fragment() {

    private var _binding: FragmentTeamPlayersBinding? = null
    private val binding get() = _binding!!
    private lateinit var teamViewModel: TeamViewModel
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        teamViewModel = ViewModelProvider(requireActivity()).get(TeamViewModel::class.java)

        // Setup RecyclerView and Adapter with a score manager
        setupRecyclerView()

        // Observe players and filter for those on the team
        teamViewModel.players.observe(viewLifecycleOwner) { players ->
            val teamMembers = players.filter { it.isOnTeam }
            teamAdapter.updateData(teamMembers.toMutableList())
        }

        // Add Player Button logic
        binding.addPlayerButton.setOnClickListener {
            // Show dialog or navigate to player selection fragment
            showAddPlayerDialog()
        }
    }

    private fun setupRecyclerView() {
        val playerRepository = PlayerRepository(requireContext())
        val playerManager = PlayerManager(playerRepository)
        val scoreManager = ScoreManager(playerManager) // Replace with actual instance if needed
        teamAdapter = TeamAdapter(
            mutableListOf(),
            onPlayerClick = { player -> showPlayerOptionsDialog(player) },
            scoreManager = scoreManager // Pass the score manager as required
        )

        binding.teamPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }
    }

    private fun showAddPlayerDialog() {
        // Logic to show a dialog or fragment for adding a player
        // This can be a PlayerSelectionDialog to pick from available players
    }

    private fun showPlayerOptionsDialog(player: Player) {
        // Logic to show player options (e.g., remove from team, view stats)
        // You could implement a dialog here for player actions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
