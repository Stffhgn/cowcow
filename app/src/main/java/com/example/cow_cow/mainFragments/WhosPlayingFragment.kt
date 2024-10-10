package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhosPlayingBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory

class WhosPlayingFragment : Fragment() {

    private lateinit var viewModel: PlayerListViewModel
    private lateinit var adapter: PlayerAdapter
    private var _binding: FragmentWhosPlayingBinding? = null
    private val binding get() = _binding!!

    // Logging tag for debugging
    private val TAG = "WhosPlayingFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: WhosPlayingFragment view is being created.")
        _binding = FragmentWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the repository
        val repository = PlayerRepository(requireContext())

        // Initialize ViewModel with PlayerListViewModelFactory
        val factory = PlayerListViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(PlayerListViewModel::class.java)

        // Log for ViewModel initialization
        Log.d(TAG, "onViewCreated: PlayerListViewModel initialized.")

        // Set up RecyclerView and Adapter
        adapter = PlayerAdapter { player -> onPlayerClick(player) } // Pass the onPlayerClick lambda
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Log RecyclerView setup
        Log.d(TAG, "RecyclerView set up with PlayerAdapter.")

        // Observe changes to the player list
        viewModel.players.observe(viewLifecycleOwner) { players ->
            if (players.isNotEmpty()) {
                binding.playerListTitle.text = "Players"  // Set the title back to "Players"
                binding.playerRecyclerView.visibility = View.VISIBLE // Show RecyclerView
                binding.playerListTitle.visibility = View.VISIBLE // Show title

                adapter.submitList(players.toList()) // Update the adapter with the new player list
                Log.d(TAG, "Player list updated. Number of players: ${players.size}")
            } else {
                Log.d(TAG, "No players found to display.")
                binding.playerListTitle.text = "No Players Available"  // Update the title when empty
                binding.playerRecyclerView.visibility = View.GONE // Hide RecyclerView
            }
        }

        // Add Player Button Click Listener
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                Log.d(TAG, "Adding a new player with name: $playerName")
                val playerId = PlayerIDGenerator.generatePlayerID()
                val newPlayer = Player(id = playerId, name = playerName)

                // Add the player using the ViewModel
                viewModel.addPlayer(newPlayer)

                // Clear the input field
                binding.playerNameInput.text.clear()

                // Reload players to reflect changes
                viewModel.loadPlayers()
            } else {
                Log.d(TAG, "Player name input is empty, not adding player.")
            }
        }

        // Log ViewModel observer setup
        Log.d(TAG, "ViewModel observer set up for players LiveData.")
    }

    // Handle player click to navigate to PlayerStatsFragment
    private fun onPlayerClick(player: Player) {
        // Use Safe Args to navigate and pass the player's ID to PlayerStatsFragment
        val action = WhosPlayingFragmentDirections
            .actionWhosPlayingFragmentToPlayerStatsFragment(player.id)
        findNavController().navigate(action)
        Log.d(TAG, "Navigating to PlayerStatsFragment with player: ${player.name}, ID: ${player.id}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}
