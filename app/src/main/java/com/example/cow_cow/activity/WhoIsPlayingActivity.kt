package com.example.cow_cow.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.ActivityWhoIsPlayingBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.utils.DataUtils
import com.example.cow_cow.viewmodel.PlayerViewModel
import com.example.cow_cow.viewmodel.PlayerViewModelFactory
import com.example.cow_cow.repositories.PlayerRepository

class WhoIsPlayingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhoIsPlayingBinding
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityWhoIsPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel with context
        val repository = PlayerRepository()
        val viewModelFactory = PlayerViewModelFactory(repository, this) // Pass context here
        playerViewModel = ViewModelProvider(this, viewModelFactory).get(PlayerViewModel::class.java)

        // Setup RecyclerView and Adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(this)
        playerAdapter = PlayerAdapter(mutableListOf()) { player ->
            // Handle player click (open EditPlayerActivity)
            val intent = Intent(this, EditPlayerActivity::class.java)
            intent.putExtra("PLAYER_ID", player.id)
            startActivity(intent)
        }
        binding.playerRecyclerView.adapter = playerAdapter

        // Observe players from the ViewModel and update the adapter
        playerViewModel.players.observe(this) { players ->
            playerAdapter.updatePlayers(players)
        }

        // Add a new player
        binding.addPlayerButton.setOnClickListener {
            showAddPlayerDialog()
        }

        // Set up back button listener
        binding.backButton.setOnClickListener {
            finish() // Goes back to the previous activity
        }
    }

    private fun showAddPlayerDialog() {
        // Create an AlertDialog to enter player name
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Player")

        // Set up an input field
        val input = EditText(this)
        input.hint = "Enter player name"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Add") { dialog, _ ->
            val playerName = input.text.toString()
            if (playerName.isNotBlank()) {
                // Create a new player and add to ViewModel
                val newPlayer = Player(id = playerViewModel.players.value?.size ?: 0, name = playerName)
                playerViewModel.addPlayer(newPlayer, this)  // Pass context when adding player
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onPause() {
        super.onPause()
        // Save the players to persistent storage
        playerViewModel.players.value?.let {
            DataUtils.savePlayers(this, it)
        }
    }

    override fun onResume() {
        super.onResume()
        // Load the players from persistent storage and update ViewModel
        val savedPlayers = DataUtils.loadPlayers(this).toMutableList()
        playerViewModel.setPlayers(savedPlayers)
    }
}
