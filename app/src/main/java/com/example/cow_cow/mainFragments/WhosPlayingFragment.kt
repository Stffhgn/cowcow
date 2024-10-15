package com.example.cow_cow.mainFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhosPlayingBinding
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
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

    // Interface to communicate with GameActivity
    private var listener: OnPlayerSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlayerSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlayerSelectedListener")
        }
    }

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
                binding.playerRecyclerView.isVisible = true // Show RecyclerView
                binding.playerListTitle.isVisible = true // Show title

                adapter.submitList(players.toList()) // Update the adapter with the new player list
                binding.startGameButton.isEnabled = true // Enable Start Game button
                Log.d(TAG, "Player list updated. Number of players: ${players.size}")
            } else {
                Log.d(TAG, "No players found to display.")
                binding.playerListTitle.text = "No Players Available"  // Update the title when empty
                binding.playerRecyclerView.isVisible = false // Hide RecyclerView
                binding.startGameButton.isEnabled = false // Disable Start Game button
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

        // Start Game Button Click Listener
        binding.startGameButton.setOnClickListener {
            Log.d(TAG, "Start Game button clicked.")
            navigateToGameOrAddPlayer() // Ensure players are present before starting the game
        }

        // Back Button Click Listener
        binding.backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked.")
            findNavController().navigateUp() // Navigate back in the navigation stack
        }

        // Log ViewModel observer setup
        Log.d(TAG, "ViewModel observer set up for players LiveData.")
    }

    private fun onPlayerClick(player: Player) {
        Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")

        // Use the listener to communicate with GameActivity without requiring an objectType
        listener?.onPlayerSelected(player.id)

        Log.d(TAG, "Navigating to PlayerStatsFragment with player: ${player.name}, ID: ${player.id}")
    }

    // Function to handle starting the game if players are present
    private fun navigateToGameOrAddPlayer() {
        viewModel.players.value?.let { players ->
            if (players.isNotEmpty()) {
                Log.d(TAG, "Players found, navigating to GameFragment.")
                val action = WhosPlayingFragmentDirections.actionWhosPlayingFragmentToCowCowFragment()
                findNavController().navigate(action)
            } else {
                Log.d(TAG, "No players found, unable to start the game.")
                // Optional: Show a Toast message or alert dialog indicating no players are present
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Reloading players to update scores.")
        viewModel.loadPlayers() // Reload player data to ensure the latest score updates
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}
