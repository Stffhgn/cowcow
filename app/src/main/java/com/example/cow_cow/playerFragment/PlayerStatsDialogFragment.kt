package com.example.cow_cow.playerFragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.databinding.DialogPlayerStatsBinding
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
            Log.d(TAG, "Deleting player with ID: $playerId")
            playerViewModel.removePlayerById(playerId)
            dismiss() // Close the dialog after deleting the player
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
        // Update the UI with player data
        binding.playerNameTextView.text = "Name: ${player.name}"
        binding.totalScoreTextView.text = "Total Score: ${player.calculateTotalPoints()}"
        binding.cowStatTextView.text = "Cows Spotted: ${player.cowCount}"
        binding.churchStatTextView.text = "Churches Spotted: ${player.churchCount}"
        binding.waterTowerStatTextView.text = "Water Towers Spotted: ${player.waterTowerCount}"
        binding.isOnTeamTextView.text = "Is On Team: ${player.isOnTeam}"
        binding.isCurrentPlayerTextView.text = "Is Current Player: ${player.isCurrentPlayer}"
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