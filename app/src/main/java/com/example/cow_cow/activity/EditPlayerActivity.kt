package com.example.cow_cow.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.ActivityEditPlayerBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory

class EditPlayerActivity : AppCompatActivity() {

    // View Binding for accessing UI elements
    private lateinit var binding: ActivityEditPlayerBinding

    // ViewModel for managing player data
    private lateinit var playerViewModel: PlayerViewModel

    // Player object representing the current player being edited
    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityEditPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel with repository and factory
        val repository = PlayerRepository()
        val viewModelFactory = PlayerViewModelFactory(application, repository)  // Fix this line
        playerViewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)

        // Get player data from Intent
        player = intent.getParcelableExtra<Player>("PLAYER_DATA")

        player?.let {
            // Update UI with player details
            updatePlayerUI(it)
            // Set up button listeners
            setupButtonListeners()
        } ?: run {
            // If player data is not available, show an error and finish the activity
            Toast.makeText(this, "Player data not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Updates the UI elements with the player's current data
     */
    private fun updatePlayerUI(player: Player) {
        // Set player name
        binding.playerNameEditText.setText(player.name)

        // Set total score
        val totalPoints = player.calculateTotalPoints()
        binding.playerScoreTextView.text = "Total Score: $totalPoints"

        // Set individual stats
        binding.cowStatTextView.text = player.cowCount.toString()
        binding.churchStatTextView.text = player.churchCount.toString()
        binding.waterTowerStatTextView.text = player.waterTowerCount.toString()

        // Update team status (simplified)
        binding.teamStatusTextView.text = if (player.isOnTeam) {
            "Team Status: On Team"
        } else {
            "Team Status: Not on Team"
        }
    }

    /**
     * Sets up click listeners for the buttons
     */
    private fun setupButtonListeners() {
        // Save player changes
        binding.savePlayerButton.setOnClickListener {
            val updatedName = binding.playerNameEditText.text.toString()
            if (updatedName.isNotBlank()) {
                player?.let {
                    it.name = updatedName
                    playerViewModel.updatePlayer(it)  // Update player in ViewModel
                    Toast.makeText(this, "Player updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete player with confirmation
        binding.deletePlayerButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Back button functionality
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Shows a confirmation dialog before deleting the player
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete this player?")
            .setPositiveButton("Yes") { _, _ ->
                player?.let {
                    playerViewModel.removePlayerById(it.id)  // Delete player via ViewModel
                    Toast.makeText(this, "Player deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
