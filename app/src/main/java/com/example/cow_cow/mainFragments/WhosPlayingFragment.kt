package com.example.cow_cow.mainFragments

import android.os.Bundle
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
import com.example.cow_cow.viewModels.PlayerViewModel

class WhosPlayingFragment : Fragment() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var adapter: PlayerAdapter
    private var _binding: FragmentWhosPlayingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Set up RecyclerView and Adapter
        adapter = PlayerAdapter { player -> onPlayerClick(player) } // Pass the onPlayerClick lambda
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe changes to the player list
        viewModel.players.observe(viewLifecycleOwner) { players ->
            adapter.submitList(players.toList()) // Update the adapter with the player list
        }

        // Add Player Button Click Listener (Optional)
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                viewModel.addPlayer(Player(0, playerName)) // Add a new player with ID 0 for now
                binding.playerNameInput.text.clear()
            }
        }
    }

    // Handle player click to navigate to PlayerStatsFragment
    private fun onPlayerClick(player: Player) {
        // Use Safe Args to navigate and pass the player's ID and name to PlayerStatsFragment
        val action = WhosPlayingFragmentDirections
            .actionWhosPlayingFragmentToPlayerStatsFragment(player.id, player.name)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
