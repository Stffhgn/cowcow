// C:\Users\steff\AndroidStudioProjects\Cow_Cow\app\src\main\java\com\example\cow_cow\PlayerFragment\PlayerListFragment.kt

package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentPlayerListBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PlayerListViewModel

class PlayerListFragment : Fragment() {

    private lateinit var binding: FragmentPlayerListBinding
    private val viewModel: PlayerListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with PlayerAdapter and handle player clicks
        val playerAdapter = PlayerAdapter { player -> onPlayerClick(player) }
        binding.recyclerViewPlayers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playerAdapter
        }

        // Observe the player list LiveData and update the UI when it changes
        viewModel.players.observe(viewLifecycleOwner) { players ->
            playerAdapter.submitList(players)
        }

        // Handle add new player button
        binding.addPlayerButton.setOnClickListener {
            // Logic to add new player (navigate to add player screen or show a dialog)
            // Example: Show a dialog to add a new player
            // AddPlayerDialog().show(parentFragmentManager, "AddPlayerDialog")
        }

        // Optional: Handle sorting/filtering players (e.g., by score or name)
        binding.sortButton.setOnClickListener {
            viewModel.sortPlayersByScore()  // Example: Sort players by score
        }
    }

    // Function to handle player clicks
    private fun onPlayerClick(player: Player) {
        val action = PlayerListFragmentDirections
            .actionPlayerListFragmentToPlayerStatsFragment(playerID = player.id, playerName = player.name)
        findNavController().navigate(action)
    }
}
