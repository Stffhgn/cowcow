package com.example.cow_cow.mainFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.controllers.PowerUpController
import com.example.cow_cow.databinding.FragmentStartBinding
import com.example.cow_cow.managers.*
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.TriviaRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel

    // Managers initialization
    private lateinit var playerManager: PlayerManager
    private lateinit var teamManager: TeamManager
    private lateinit var penaltyManager: PenaltyManager
    private lateinit var scoreManager: ScoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the repositories and managers relevant to the fragment
        val playerRepository = PlayerRepository(requireContext())
        playerManager = PlayerManager(playerRepository)
        scoreManager = ScoreManager(playerManager)
        teamManager = TeamManager(playerRepository, scoreManager)
        penaltyManager = PenaltyManager(playerManager)

        // Create ViewModel using the custom factory
        val factory = PlayerViewModelFactory(requireActivity().application, playerRepository, playerManager, penaltyManager)
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Observe the list of players and set up buttons
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            binding.playerButton.text = if (players.isNullOrEmpty()) {
                "Add Player"
            } else {
                "Who is Playing"
            }
        }
        setupButtons()
    }


    // Set up button click listeners
    private fun setupButtons() {
        binding.apply {
            startGameButton.setOnClickListener {
                navigateToGameOrAddPlayer()
            }
            playerButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Who's Playing/Add Player fragment")
                findNavController().navigate(R.id.action_startFragment_to_whosPlayingFragment)
            }
            howToPlayButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to How To Play fragment")
                findNavController().navigate(R.id.action_startFragment_to_howToPlayFragment)
            }
            settingsButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Settings fragment")
                findNavController().navigate(R.id.action_startFragment_to_appSettingsFragment)
            }
            storeButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Store fragment")
                findNavController().navigate(R.id.action_startFragment_to_storeFragment)
            }
            resetButton.setOnClickListener {
                resetAllData()
            }
        }
    }

    private fun navigateToGameOrAddPlayer() {
        playerViewModel.players.value?.let { players ->
            if (players.isNullOrEmpty()) {
                Log.d("StartFragment", "No players found, navigating to Add Player screen")
                findNavController().navigate(R.id.action_startFragment_to_whosPlayingFragment)
            } else {
                Log.d("StartFragment", "Players found, starting GameActivity")
                val intent = Intent(requireContext(), GameActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun resetAllData() {
        Log.d("StartFragment", "Resetting all game data")
        playerManager.getAllPlayers().forEach { player ->
            penaltyManager.clearAllPenalties(player)
            PowerUpController.clearAllActivePowerUpsForPlayer(player)
            playerManager.savePlayer(player)
        }
        playerManager.resetAllPlayerStats()
        teamManager.resetTeam()
        Log.d("StartFragment", "All game data reset completed.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
