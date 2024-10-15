package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.DialogTeamManagementBinding
import com.example.cow_cow.models.Player


class TeamManagementFragmentDialog : DialogFragment() {

    private var _binding: DialogTeamManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTeamAdapter: PlayerAdapter
    private lateinit var notOnTeamAdapter: PlayerAdapter
    private var players: List<Player> = emptyList()  // Use default empty list to avoid crash

    companion object {
        const val TAG_DIALOG = "TeamManagementFragmentDialog"

        fun newInstance(players: List<Player>): TeamManagementFragmentDialog {
            val fragment = TeamManagementFragmentDialog()
            val args = Bundle().apply {
                putParcelableArrayList("players", ArrayList(players))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTeamManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the players list from arguments and handle the null case
        val players: List<Player> = arguments?.getParcelableArrayList("players") ?: emptyList()

        // Set up the RecyclerViews and pass the player list
        setupRecyclerViews()
        refreshPlayerList(players) // Pass the players list to this function
    }

    // Function to initialize RecyclerViews for both lists
    private fun setupRecyclerViews() {
        // Adapter for players on the team
        onTeamAdapter = PlayerAdapter { player ->
            togglePlayerTeamStatus(player)  // Call this function when a player is clicked
        }

        // Adapter for players not on the team
        notOnTeamAdapter = PlayerAdapter { player ->
            togglePlayerTeamStatus(player)  // Call this function when a player is clicked
        }

        // Setup RecyclerView for players on the team
        binding.onTeamRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = onTeamAdapter
        }

        // Setup RecyclerView for players not on the team
        binding.notOnTeamRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notOnTeamAdapter
        }
    }

    private fun togglePlayerTeamStatus(player: Player) {
        (activity as? GameActivity)?.togglePlayerTeamStatus(player)
            ?: Log.e("TeamManagementFragmentDialog", "GameActivity not found!")

        // Refresh the UI after toggling the player status
        refreshPlayerList(players)
        // Dismiss the dialog after the player status is updated
        dismiss()
    }

    // Refresh the lists (on team and not on team)
    private fun refreshPlayerList(players: List<Player>) {
        val playersOnTeam = players.filter { it.isOnTeam }
        val playersNotOnTeam = players.filter { !it.isOnTeam }

        // Update the adapters with the new lists
        onTeamAdapter.submitList(playersOnTeam)
        notOnTeamAdapter.submitList(playersNotOnTeam)

        // Handle UI changes for empty states
        handleEmptyState(playersOnTeam, playersNotOnTeam)
    }

    // Handle empty state for players on team and not on team
    private fun handleEmptyState(playersOnTeam: List<Player>, playersNotOnTeam: List<Player>) {
        binding.onTeamRecyclerView.visibility = if (playersOnTeam.isEmpty()) View.GONE else View.VISIBLE
        binding.notOnTeamRecyclerView.visibility = if (playersNotOnTeam.isEmpty()) View.GONE else View.VISIBLE
        binding.availablePlayersEmptyState.visibility =
            if (playersOnTeam.isEmpty() && playersNotOnTeam.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
