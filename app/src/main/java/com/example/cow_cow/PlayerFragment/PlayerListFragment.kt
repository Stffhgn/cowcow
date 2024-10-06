package com.example.cow_cow.PlayerFragment

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
import com.example.cow_cow.viewModels.PlayerViewModel

class PlayerListFragment : Fragment() {

    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing players data
    private lateinit var playerViewModel: PlayerViewModel
    // Adapter for handling player list
    private lateinit var playerAdapter: PlayerAdapter

    // Log tag for debugging
    private val TAG = "PlayerListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentPlayerListBinding.inflate(inflater, container, false)

        // Log fragment creation
        Log.d(TAG, "onCreateView: PlayerListFragment view is being created.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController for navigation
        val navController = findNavController()

        // Initialize ViewModel using ViewModelProvider
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java) // Ensuring the ViewModel is scoped to the activity

        // Log for ViewModel initialization
        Log.d(TAG, "onViewCreated: PlayerViewModel initialized.")

        // Initialize PlayerAdapter with a click listener
        playerAdapter = PlayerAdapter { player ->
            // Log player click event
            Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")

            // Handle player click - navigate to PlayerStatsFragment with SafeArgs
            val action = PlayerListFragmentDirections
                .actionPlayerListFragmentToPlayerStatsFragment(player.id, player.name)

            // Log the navigation action
            Log.d(TAG, "Navigating to PlayerStatsFragment with player: ${player.name}, ID: ${player.id}")

            // Navigate to the PlayerStatsFragment
            navController.navigate(action)
        }

        // Set up RecyclerView for the player list
        binding.playerRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Log RecyclerView setup
        Log.d(TAG, "RecyclerView set up with PlayerAdapter.")

        // Observe LiveData from the ViewModel for the list of players
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            // Update the adapter with the new player list
            playerAdapter.submitList(players)

            // Log the player list update
            Log.d(TAG, "Player list updated. Number of players: ${players.size}")
        }

        // Log ViewModel observer setup
        Log.d(TAG, "ViewModel observer set up for players LiveData.")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clean up ViewBinding when the view is destroyed
        _binding = null

        // Log fragment destruction
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}
