package com.example.cow_cow.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils

class WhoCalledItFragment : Fragment() {

    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private var action: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = arguments?.getString("ACTION")
        Log.d("WhoCalledItFragment", "Action received: $action")

        // Set up the repository and factory
        val repository = PlayerRepository()
        val viewModelFactory = PlayerViewModelFactory(repository, requireContext())
        playerViewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)

        binding.whoCalledItRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val action = "Cow" // For example, this value will come from the user's selection

        // Initialize the adapter with context from the fragment
        playerAdapter = PlayerAdapter(mutableListOf()) { player ->
            handlePlayerSelection(player, action)
        }

        // Set the adapter to the RecyclerView
        binding.whoCalledItRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // Use requireContext() here
        binding.whoCalledItRecyclerView.adapter = playerAdapter

        // Observe the players and update the adapter
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            if (players.isEmpty()) {
                Log.d("WhoCalledItFragment", "No players loaded.")
            } else {
                Log.d("WhoCalledItFragment", "Players loaded: ${players.size}")
            }
            playerAdapter.updatePlayers(players.toMutableList()) // Convert to MutableList<Player>
        }
    }

    private fun handlePlayerSelection(player: Player, action: String) {
        // Update the player's score based on the action
        when (action) {
            "Cow" -> player.cowCount += 1  // Add 1 point for Cow
            "Church" -> player.churchCount += 1 // Assuming 1 point per church
            "Water Tower" -> player.waterTowerCount += 1 // Assuming 1 point per water tower
        }

        // Update the player in the ViewModel with the required Context
        playerViewModel.updatePlayer(player, requireContext())

        // Notify the adapter about the updated player data
        playerAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
