package com.example.cow_cow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory

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

        // Get the player ID from arguments
        arguments?.let {
            playerId = it.getInt("PLAYER_ID", 0)
        }

        // Initialize PlayerRepository and PlayerViewModelFactory
        val playerRepository = PlayerRepository()
        val factory = PlayerViewModelFactory(requireActivity().application, playerRepository) // Use requireActivity().application here

        // Initialize PlayerViewModel using ViewModelProvider and factory
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Fetch the player data using playerId
        val player = playerViewModel.getPlayerById(playerId)

        // Bind player data to the UI
        player?.let {
            binding.playerName?.text = it.name  // Use correct ID
            binding.playerTotalScore?.text = "Total Score: ${it.calculateTotalPoints()}" // Use correct ID
            binding.cowStat?.text = it.cowCount.toString() // Use correct ID
            binding.churchStat?.text = it.churchCount.toString() // Use correct ID
            binding.waterTowerStat?.text = it.waterTowerCount.toString() // Use correct ID
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
