package com.example.cow_cow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory

class PlayerStatsFragment : Fragment() {

    private var _binding: FragmentPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private var playerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentPlayerStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the player ID from the arguments (if passed)
        arguments?.let {
            playerId = it.getInt("PLAYER_ID", 0)
        }

        // Initialize PlayerRepository and PlayerViewModelFactory
        val playerRepository = PlayerRepository()
        val factory = PlayerViewModelFactory(playerRepository, requireContext()) // Pass context here

        // Initialize PlayerViewModel using ViewModelProvider and factory
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Fetch the player data using the playerId
        val player = playerViewModel.getPlayerById(playerId)

        // Bind player data to the UI
        player?.let {
            binding.playerNameTextView.text = it.name
            binding.playerTotalScoreTextView.text = "Total Score: ${it.totalScore}"
            binding.cowStatTextView.text = it.cowCount.toString()
            binding.churchStatTextView.text = it.churchCount.toString()
            binding.waterTowerStatTextView.text = it.waterTowerCount.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
