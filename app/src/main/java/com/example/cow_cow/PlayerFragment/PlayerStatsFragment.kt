package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.AchievementsAdapter
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.viewModels.PlayerViewModel

class PlayerStatsFragment : Fragment() {

    private lateinit var binding: FragmentPlayerStatsBinding
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var achievementsAdapter: AchievementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Initialize the RecyclerView for achievements
        achievementsAdapter = AchievementsAdapter(mutableListOf())
        binding.achievementsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = achievementsAdapter
        }

        // Observe player data
        playerViewModel.selectedPlayer.observe(viewLifecycleOwner, Observer { player ->
            player?.let {
                // Update UI with player data
                binding.playerNameTextView.text = it.name
                binding.totalScoreTextView.text = "Total Score: ${it.calculateTotalPoints()}"

                // Update achievements
                achievementsAdapter.updateAchievements(it.achievements)
            }
        })
    }
}
