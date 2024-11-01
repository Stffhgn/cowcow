package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.databinding.DialogPlayerStatsBinding
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerStatsViewModel
import com.example.cow_cow.viewModels.PlayerStatsViewModelFactory
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class PlayerStatsDialogFragment : DialogFragment() {

    private var _binding: DialogPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerStatsViewModel: PlayerStatsViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerId: String
    private lateinit var scoreManager: ScoreManager
    private lateinit var penaltyManager: PenaltyManager

    companion object {
        const val TAG = "PlayerStatsDialogFragment"

        fun newInstance(playerId: String): PlayerStatsDialogFragment {
            val args = Bundle()
            args.putString("playerID", playerId)
            val fragment = PlayerStatsDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPlayerStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerId = arguments?.getString("playerID") ?: throw IllegalArgumentException("Player ID required")
        Log.d(TAG, "Received playerId: $playerId")

        val application = requireActivity().application
        val playerRepository = PlayerRepository(application)
        val playerManager = PlayerManager(playerRepository)
        scoreManager = ScoreManager(playerManager)

        // Initialize ViewModel with PlayerStatsViewModelFactory
        val statsFactory = PlayerStatsViewModelFactory(application, playerRepository, playerId)
        playerStatsViewModel = ViewModelProvider(this, statsFactory).get(PlayerStatsViewModel::class.java)

        // Create PlayerViewModel with the factory
        val playerFactory = PlayerViewModelFactory(application, playerRepository, playerManager, penaltyManager)
        playerViewModel = ViewModelProvider(this, playerFactory).get(PlayerViewModel::class.java)

        // Observe player data updates
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            val player = players.find { it.id == playerId }
            if (player != null) {
                bindPlayerData(player)
            } else {
                displayError("Player not found")
            }
        }

        playerStatsViewModel.player.observe(viewLifecycleOwner) { player ->
            bindPlayerData(player)
        }

        // Set up button actions
        binding.deletePlayerButton.setOnClickListener {
            val playerName = binding.playerNameTextView.text.toString()
            if (playerName.isNotBlank()) {
                playerViewModel.removePlayerByName(playerName)
                Toast.makeText(requireContext(), "Player $playerName deleted", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.okayButton.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun bindPlayerData(player: Player) {
        binding.apply {
            playerNameTextView.text = player.name
            totalScoreTextView.text = getString(
                R.string.total_score,
                scoreManager.calculatePlayerScore(player)
            )
            cowStatTextView.text = getString(R.string.cows_spotted, player.cowCount)
            churchStatTextView.text = getString(R.string.churches_spotted, player.churchCount)
            waterTowerStatTextView.text = getString(R.string.water_towers_spotted, player.waterTowerCount)
            isOnTeamTextView.text = getString(R.string.is_on_team, player.isOnTeam.toString())
            isCurrentPlayerTextView.text = getString(R.string.is_current_player, player.isCurrentPlayer.toString())
        }
    }

    private fun displayError(message: String) {
        Log.e(TAG, "Error: $message")
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
