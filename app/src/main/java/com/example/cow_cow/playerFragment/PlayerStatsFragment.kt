package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerStatsViewModel
import com.example.cow_cow.viewModels.PlayerStatsViewModelFactory
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class PlayerStatsFragment : Fragment(R.layout.fragment_player_stats) {

    private var _binding: FragmentPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerStatsViewModel: PlayerStatsViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerId: String
    private lateinit var scoreManager: ScoreManager
    private lateinit var penaltyManager: PenaltyManager

    private val args: PlayerStatsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPlayerStatsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the playerId from the arguments
        playerId = args.playerID
        Log.d("PlayerStatsFragment", "Received playerId: $playerId")

        // Initialize the PlayerRepository
        val application = requireActivity().application
        val playerRepository = PlayerRepository(application)
        val playerManager = PlayerManager(playerRepository)

        // Set up ViewModel with PlayerStatsViewModelFactory
        val statsFactory = PlayerStatsViewModelFactory(application, playerRepository, playerId)
        playerStatsViewModel = ViewModelProvider(this, statsFactory).get(PlayerStatsViewModel::class.java)

        // Create the PlayerViewModelFactory
        val playerFactory = PlayerViewModelFactory(application, playerRepository, playerManager, penaltyManager)
        playerViewModel = ViewModelProvider(this, playerFactory).get(PlayerViewModel::class.java)

        // Observe the players LiveData
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            Log.d("PlayerStatsFragment", "players LiveData updated. Number of players: ${players.size}")
            val player = players.find { it.id == playerId }
            if (player != null) {
                bindPlayerData(player)
            } else {
                displayError("Player not found")
            }
        }

        // Observe player data
        playerStatsViewModel.player.observe(viewLifecycleOwner) { player ->
            bindPlayerData(player)
        }
    }

    private fun bindPlayerData(player: Player) {
        // Update the UI elements with player data using the binding
        binding.apply {
            playerNameTextView.text = getString(R.string.player_name, player.name)
            totalScoreTextView.text = getString(
                R.string.total_score,
                scoreManager.calculatePlayerScore(player)
            )
            cowStatTextView.text = getString(R.string.cows_spotted, player.cowCount)
            churchStatTextView.text = getString(R.string.churches_spotted, player.churchCount)
            waterTowerStatTextView.text = getString(R.string.water_towers_spotted, player.waterTowerCount)
            bonusPointsTextView.text = getString(R.string.bonus_points, player.bonusPoints)
            activePowerUpsTextView.text = getString(R.string.active_power_ups, player.activePowerUps.size)
            gamesPlayedTextView.text = getString(R.string.games_played, player.gamesPlayed)
            rankTextView.text = getString(R.string.rank, player.rank.name)
        }
    }

    private fun displayError(message: String) {
        Log.d("PlayerStatsFragment", "Displaying error message: $message")
        // Hide progress bar and show error text
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PlayerStatsFragment", "Navigating back to WhosPlayingDialogFragment")
    }
}
