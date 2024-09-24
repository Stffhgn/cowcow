package com.example.cow_cow.activity

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cow_cow.databinding.ActivityEditPlayerBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory
import com.example.cow_cow.repositories.PlayerRepository
import androidx.lifecycle.ViewModelProvider

class EditPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel
    private var player: Player? = null  // Member variable to store player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityEditPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val repository = PlayerRepository()
        val viewModelFactory = PlayerViewModelFactory(repository, this)  // Pass context here
        playerViewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)

        // Get player ID from Intent
        val playerId = intent.getIntExtra("PLAYER_ID", -1)

        // Fetch player by ID
        player = playerViewModel.getPlayerById(playerId)

        player?.let { player ->
            // Update UI with player details
            binding.playerNameEditText.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    // Hide the keyboard when the user finishes editing
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)

                    // Make it non-editable again
                    binding.playerNameEditText.apply {
                        isFocusable = false
                        isClickable = true  // Enable clicking again
                    }
                }
            }


            binding.playerScoreTextView.text = "Total Score: ${player.totalScore}"
            binding.cowStatTextView.text = player.cowCount.toString()
            binding.churchStatTextView.text = player.churchCount.toString()
            binding.waterTowerStatTextView.text = player.waterTowerCount.toString()

            // Ensure the team is properly loaded before checking
            playerViewModel.loadTeam(this)

            // Add logging to debug the team check
            val isInTeam = playerViewModel.isPlayerInTeam(player)
            if (isInTeam) {
                binding.teamStatusTextView.text = "Team Status: On Team"
            } else {
                binding.teamStatusTextView.text = "Team Status: Not on Team"
            }

            // Debug logging to see if the player is recognized in the team
            Log.d("EditPlayerActivity", "Is player in team? $isInTeam")
        }

        // Save player changes on button click
        binding.savePlayerButton.setOnClickListener {
            val updatedName = binding.playerNameEditText.text.toString()
            if (updatedName.isNotBlank()) {
                player?.let {
                    it.name = updatedName
                    playerViewModel.updatePlayer(it, this)  // Pass context if required
                }
            }
            finish()
        }

        // Delete player with confirmation dialog
        binding.deletePlayerButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Back button functionality
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // Show confirmation dialog for deleting the player
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete this player?")
            .setPositiveButton("Yes") { _, _ ->
                player?.let {
                    playerViewModel.removePlayer(it, this) // Pass context to remove the player
                }
                finish() // Close the activity and return to "Who's Playing"
            }
            .setNegativeButton("No", null)
            .show()
    }
}
