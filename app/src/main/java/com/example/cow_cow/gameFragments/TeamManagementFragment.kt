package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentTeamManagementBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class TeamManagementFragment : Fragment() {

    private var _binding: FragmentTeamManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var availablePlayerAdapter: PlayerAdapter
    private lateinit var teamPlayerAdapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        val playerRepository = PlayerRepository()
        val factory = PlayerViewModelFactory(requireActivity().application, playerRepository)
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Set up RecyclerViews
        setupRecyclerViews()

        // Observe LiveData to display available and team players
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            val availablePlayers = players.filter { !it.isOnTeam }
            availablePlayerAdapter.submitList(availablePlayers)
            handleEmptyState(availablePlayers, true) // Check for empty state for available players
        }

        playerViewModel.teamPlayers.observe(viewLifecycleOwner) { teamPlayers ->
            teamPlayerAdapter.submitList(teamPlayers)
            handleEmptyState(teamPlayers, false) // Check for empty state for team players
        }

        // Observe loading state and toggle progress bar visibility
        playerViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages and display them
        playerViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                playerViewModel.clearError() // Clear the error after displaying
            }
        }

        // Save team button functionality
        binding.saveTeamButton.setOnClickListener {
            playerViewModel.saveTeam()
            Toast.makeText(requireContext(), "Team saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to initialize RecyclerViews
    private fun setupRecyclerViews() {
        availablePlayerAdapter = PlayerAdapter { player ->
            playerViewModel.addPlayerToTeam(player)
        }

        teamPlayerAdapter = PlayerAdapter { player ->
            playerViewModel.removePlayerFromTeam(player)
        }

        binding.availablePlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = availablePlayerAdapter
        }

        binding.teamPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = teamPlayerAdapter
        }
    }

    // Handle empty state for both available players and team players
    private fun handleEmptyState(players: List<Player>, isAvailableList: Boolean) {
        if (isAvailableList) {
            if (players.isEmpty()) {
                binding.availablePlayersEmptyState.visibility = View.VISIBLE
                binding.availablePlayersRecyclerView.visibility = View.GONE
            } else {
                binding.availablePlayersEmptyState.visibility = View.GONE
                binding.availablePlayersRecyclerView.visibility = View.VISIBLE
            }
        } else {
            if (players.isEmpty()) {
                binding.teamPlayersEmptyState.visibility = View.VISIBLE
                binding.teamPlayersRecyclerView.visibility = View.GONE
            } else {
                binding.teamPlayersEmptyState.visibility = View.GONE
                binding.teamPlayersRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
