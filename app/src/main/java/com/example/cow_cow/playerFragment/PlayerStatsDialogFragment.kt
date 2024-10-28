package com.example.cow_cow.playerFragment

import android.content.DialogInterface
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

        // Retrieve the playerId from the arguments
        playerId = arguments?.getString("playerID") ?: throw IllegalArgumentException("Player ID required")
        Log.d(TAG, "Received playerId: $playerId")

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
            Log.d(TAG, "players LiveData updated. Number of players: ${players.size}")
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

        // Set up Delete button
        binding.deletePlayerButton.setOnClickListener {
            val playerName = binding.playerNameTextView.text.toString()
            Log.d(TAG, "Deleting player with name: $playerName")

            if (playerName.isNotBlank()) {
                playerViewModel.removePlayerByName(playerName)
                Toast.makeText(requireContext(), "Player $playerName deleted", Toast.LENGTH_SHORT).show()
                dismiss() // Close the dialog after deleting the player
            } else {
                Toast.makeText(requireContext(), "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


        // Set up Okay button
        binding.okayButton.setOnClickListener {
            dismiss() // Simply close the dialog
        }
    }

    override fun onStart() {
        super.onStart()
        // Set the dialog to take up the majority of the screen space
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


    private fun bindPlayerData(player: Player) {
        // Update the UI elements with player data using the binding
        binding.apply {
            playerNameTextView.text = player.name // Display the player's name
            totalScoreTextView.text = getString(
                R.string.total_score,
                ScoreManager.calculatePlayerScore(player) // Pass the player object to calculate the score
            )
            cowStatTextView.text = getString(R.string.cows_spotted, player.cowCount)
            churchStatTextView.text = getString(R.string.churches_spotted, player.churchCount)
            waterTowerStatTextView.text = getString(R.string.water_towers_spotted, player.waterTowerCount)
            isOnTeamTextView.text = getString(R.string.is_on_team, player.isOnTeam.toString())
            isCurrentPlayerTextView.text = getString(R.string.is_current_player, player.isCurrentPlayer.toString())
        }
    }


    private fun displayError(message: String) {
        Log.d(TAG, "Displaying error message: $message")
        // Hide progress bar and show error text
        /*binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "PlayerStatsDialogFragment view destroyed")
    }
}