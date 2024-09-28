package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentPlayerListBinding
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

        // Set up RecyclerView
        val playerAdapter = PlayerAdapter()
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
        }

        // Optional: Handle sorting/filtering players (e.g., by score or name)
        binding.sortButton.setOnClickListener {
            viewModel.sortPlayersByScore()  // Example: Sort players by score
        }
    }
}
