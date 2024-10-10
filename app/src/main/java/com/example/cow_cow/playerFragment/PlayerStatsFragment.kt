package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerStatsViewModel
import com.example.cow_cow.viewModels.PlayerStatsViewModelFactory

class PlayerStatsFragment : Fragment() {

    private var _binding: FragmentPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerStatsViewModel: PlayerStatsViewModel

    private val args: PlayerStatsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewModel with PlayerStatsViewModelFactory
        val playerRepository = PlayerRepository(requireActivity().applicationContext)
        val factory = PlayerStatsViewModelFactory(requireActivity().application, playerRepository, args.playerID)
        playerStatsViewModel = ViewModelProvider(this, factory).get(PlayerStatsViewModel::class.java)

        // Observe player data
        playerStatsViewModel.player.observe(viewLifecycleOwner) { player ->
            binding.playerNameTextView.text = player.name
            binding.totalScoreTextView.text = "Total Score: ${player.calculateTotalPoints()}"
            binding.cowStatTextView.text = player.cowCount.toString()
            binding.churchStatTextView.text = player.churchCount.toString()
            binding.waterTowerStatTextView.text = player.waterTowerCount.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
