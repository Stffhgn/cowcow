package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentPlayerListBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory

class PlayerListFragment : Fragment() {

    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing players data
    private lateinit var playerListViewModel: PlayerListViewModel
    // Adapter for handling player list
    private lateinit var playerAdapter: PlayerAdapter

    // Log tag for debugging
    private val TAG = "PlayerListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: PlayerListFragment view is being created.")
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using PlayerListViewModelFactory
        val playerRepository = PlayerRepository(requireActivity().applicationContext)
        val factory = PlayerListViewModelFactory(requireActivity().application, playerRepository)
        playerListViewModel = ViewModelProvider(requireActivity(), factory).get(PlayerListViewModel::class.java)

        // Log for ViewModel initialization
        Log.d(TAG, "onViewCreated: PlayerListViewModel initialized.")

        // Initialize PlayerAdapter with a click listener
        playerAdapter = PlayerAdapter { player ->
            // Log player click event
            Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")

            // Handle player click - navigate to PlayerStatsFragment with SafeArgs
            val action = PlayerListFragmentDirections
                .actionPlayerListFragmentToPlayerStatsFragment(player.id)

            // Log the navigation action
            Log.d(TAG, "Navigating to PlayerStatsFragment with player: ${player.name}, ID: ${player.id}")

            // Navigate to the PlayerStatsFragment
            findNavController().navigate(action)
        }

        // Set up RecyclerView for the player list
        binding.playerRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Log RecyclerView setup
        Log.d(TAG, "RecyclerView set up with PlayerAdapter.")

        // Observe LiveData from the ViewModel for the list of players
        playerListViewModel.players.observe(viewLifecycleOwner) { players ->
            // Update the adapter with the new player list
            playerAdapter.submitList(players)

            // Log the player list update
            Log.d(TAG, "Player list updated. Number of players: ${players.size}")
        }

        // Log ViewModel observer setup
        Log.d(TAG, "ViewModel observer set up for players LiveData.")

        // Set up button listeners
        setupButtons()
    }

    // Set up button click listeners
    private fun setupButtons() {
        binding.apply {
            // Add Player Button
            addPlayerButton.setOnClickListener {
                Log.d(TAG, "Add Player button clicked.")
                addPlayer()
            }

            // Sort Players by Score Button
            sortButton.setOnClickListener {
                Log.d(TAG, "Sort Players by Score button clicked.")
                playerListViewModel.sortPlayersByScore()
            }
        }
    }

    // Function to add a new player
    private fun addPlayer() {
        Log.d(TAG, "Adding a new player.")
        val newPlayerId = PlayerIDGenerator.generatePlayerID()
        val newPlayer = Player(id = newPlayerId, name = "New Player")
        playerListViewModel.addPlayer(newPlayer)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clean up ViewBinding when the view is destroyed
        _binding = null

        // Log fragment destruction
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}