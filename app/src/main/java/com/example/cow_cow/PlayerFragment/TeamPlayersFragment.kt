package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.TeamAdapter
import com.example.cow_cow.databinding.FragmentTeamPlayersBinding
import com.example.cow_cow.viewModels.TeamViewModel
import com.example.cow_cow.models.Player

class TeamPlayersFragment : Fragment() {

    private lateinit var binding: FragmentTeamPlayersBinding
    private lateinit var teamViewModel: TeamViewModel
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeamPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        teamViewModel = ViewModelProvider(requireActivity()).get(TeamViewModel::class.java)

        // Setup RecyclerView and Adapter
        setupRecyclerView()

        // Observe team players from ViewModel and update UI
        teamViewModel.team.observe(viewLifecycleOwner, Observer { team ->
            team?.let {
                teamAdapter.updateData(it.members.toMutableList())
            }
        })

        // Add Player Button logic
        binding.addPlayerButton.setOnClickListener {
            // Show dialog or navigate to player selection fragment
            showAddPlayerDialog()
        }
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(mutableListOf(), onPlayerClick = { player ->
            // Handle player click (e.g., show stats or options)
            showPlayerOptionsDialog(player)
        })

        binding.teamPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }
    }

    private fun showAddPlayerDialog() {
        // Logic to show a dialog or fragment for adding a player
        // This can be a PlayerSelectionDialog to pick from available players
    }

    private fun showPlayerOptionsDialog(player: Player) {
        // Logic to show player options (e.g., remove from team, view stats)
        // You could implement a dialog here for player actions
    }
}
