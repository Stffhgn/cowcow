package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
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

    private val args: PlayerStatsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPlayerStatsBinding.bind(view)

        super.onViewCreated(view, savedInstanceState)


        // Retrieve the playerId from the arguments
        playerId = args.playerID
        Log.d("PlayerStatsFragment", "Received playerId: $playerId")

        // Initialize the PlayerRepository
        val application = requireActivity().application
        val repository = PlayerRepository(application)

        // Set up ViewModel with PlayerStatsViewModelFactory
        val statsFactory = PlayerStatsViewModelFactory(application, repository, playerId)
        playerStatsViewModel = ViewModelProvider(this, statsFactory).get(PlayerStatsViewModel::class.java)

        // Create the PlayerViewModelFactory
        val playerFactory = PlayerViewModelFactory(application, repository)
        // Obtain the PlayerViewModel using the factory
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
            binding.playerNameTextView.text = "Name: ${player.name}"
            binding.totalScoreTextView.text = "Total Score: ${player.calculateTotalPoints()}"
            binding.cowStatTextView.text = "Cows Spotted: ${player.cowCount}"
            binding.churchStatTextView.text = "Churches Spotted: ${player.churchCount}"
            binding.waterTowerStatTextView.text = "Water Towers Spotted: ${player.waterTowerCount}"
            binding.isOnTeamTextView.text = "Is On Team: ${player.isOnTeam}"
            binding.isCurrentPlayerTextView.text = "Is Current Player: ${player.isCurrentPlayer}"
            binding.basePointsTextView.text = "Base Points: ${player.basePoints}"
            binding.penaltyPointsTextView.text = "Penalty Points: ${player.penaltyPoints}"
            binding.bonusPointsTextView.text = "Bonus Points: ${player.bonusPoints}"
            binding.penaltiesTextView.text = "Penalties: ${player.penalties.size}"
            binding.achievementsTextView.text = "Achievements: ${player.achievements.size}"
            binding.customRulesTextView.text = "Custom Rules: ${player.customRules.size}"
            binding.activePowerUpsTextView.text = "Active Power-Ups: ${player.activePowerUps.size}"
            binding.timePlayedTextView.text = "Time Played: ${player.timePlayed} ms"
            binding.distanceTraveledTextView.text = "Distance Traveled: ${player.distanceTraveled} m"
            binding.teamIdTextView.text = "Team ID: ${player.teamId ?: "None"}"
            binding.isSilencedTextView.text = "Is Silenced: ${player.isSilenced}"
            binding.isPowerUpActiveTextView.text = "Is Power-Up Active: ${player.isPowerUpActive}"
            binding.timeSpentTextView.text = "Time Spent: ${player.timeSpent} ms"
            binding.winStreakTextView.text = "Win Streak: ${player.winStreak}"
            binding.objectivesCompletedTextView.text = "Objectives Completed: ${player.objectivesCompleted}"
            binding.notificationsEnabledTextView.text = "Notifications Enabled: ${player.notificationsEnabled}"
            binding.gamesPlayedTextView.text = "Games Played: ${player.gamesPlayed}"
            binding.rankTextView.text = "Rank: ${player.rank.name}"
            binding.objectsCalledTextView.text = "Objects Called: ${player.objectsCalled.size}"
        }
    }

    private fun bindPlayerData(player: Player) {
        // Update the UI with player data
        binding.playerNameTextView.text = player.name
        binding.totalScoreTextView.text = "Total Score: ${player.calculateTotalPoints()}"
        binding.cowStatTextView.text = player.cowCount.toString()
        binding.churchStatTextView.text = player.churchCount.toString()
        binding.waterTowerStatTextView.text = player.waterTowerCount.toString()
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
    }
}