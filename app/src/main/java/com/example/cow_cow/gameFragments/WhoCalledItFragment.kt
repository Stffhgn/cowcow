package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class WhoCalledItFragment : Fragment() {

    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private var playerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve playerId as Int
        playerId = arguments?.getInt("playerId") ?: 0

        // Check if playerId is valid
        if (playerId == 0) {
            showError("Invalid Player ID")
            return
        }

        // Initialize PlayerViewModel
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Observe player data
        playerViewModel.getPlayerById(playerId)?.observe(viewLifecycleOwner) { player ->
            player?.let {
                bindPlayerData(it)
            } ?: showError("Player not found")
        }
    }

    private fun bindPlayerData(player: Player) {
        binding.apply {
            playerName.text = player.name
            // Bind other player data as needed
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

