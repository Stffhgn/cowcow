package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PlayerViewModel

class PlayerStatsFragment : Fragment(R.layout.fragment_player_stats) {

    private var _binding: FragmentPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private val args: PlayerStatsFragmentArgs by navArgs()

    private var playerId: Int = 0
    private lateinit var playerName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding
        _binding = FragmentPlayerStatsBinding.bind(view)

        // Retrieve arguments using Safe Args
        playerId = args.playerID
        playerName = args.playerName

        // Initialize PlayerViewModel
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)

        // Fetch the player data using playerId and observe changes
        playerViewModel.getPlayerById(playerId)?.observe(viewLifecycleOwner) { player ->
            player?.let {
                bindPlayerData(it)
            } ?: showError("Player not found")
        }
    }

    // Method to bind player data to the UI
    private fun bindPlayerData(player: Player) {
        binding.apply {
            playerNameTextView.text = player.name
            //playerTotalScoreView.text = getString(R.string.total_score, player.calculateTotalPoints())
            cowStatTextView.text = player.cowCount.toString()
            churchStatTextView.text = player.churchCount.toString()
            waterTowerStatTextView.text = player.waterTowerCount.toString()
        }
    }

    // Method to show error messages via Toast
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
