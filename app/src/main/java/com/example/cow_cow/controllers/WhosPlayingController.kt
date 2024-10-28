package com.example.cow_cow.controllers

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.DialogWhosPlayingBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.playerFragment.PlayerStatsDialogFragment
import com.example.cow_cow.playerFragment.PlayerStatsFragment
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory

class WhosPlayingController(
    private val context: Context,
    private val binding: DialogWhosPlayingBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val playerController: PlayerController // Inject PlayerController to handle player actions

) {

    private lateinit var viewModel: PlayerListViewModel
    private lateinit var adapter: PlayerAdapter
    private val TAG = "WhosPlayingController"

    fun initializeController() {
        // Initialize the repository
        val repository = PlayerRepository(context)

        // Initialize ViewModel with PlayerListViewModelFactory
        val factory = PlayerListViewModelFactory((context as androidx.fragment.app.FragmentActivity).application, repository)
        viewModel = ViewModelProvider(context, factory).get(PlayerListViewModel::class.java)

        // Log for ViewModel initialization
        Log.d(TAG, "PlayerListViewModel initialized.")

        // Set up RecyclerView and Adapter
        setupRecyclerView()

        // Observe changes to the player list
        observePlayers()
    }

    private fun setupRecyclerView() {
        // Create an instance of ScoreManager (adjust based on how it's implemented in your project)
        val scoreManager = com.example.cow_cow.managers.ScoreManager // If ScoreManager is an object, no need to invoke it.

        // Initialize the PlayerAdapter with the required parameters
        adapter = PlayerAdapter(
            isWhoCalledItContext = true, // Set this based on your context (e.g., if this is being used in a WhoCalledItFragment)
            onPlayerClick = { player -> onPlayerClick(player) }, // Pass the click handling lambda
            scoreManager = scoreManager // Inject the ScoreManager instance
        )

        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(context)

        // Log RecyclerView setup
        Log.d(TAG, "RecyclerView set up with PlayerAdapter using ScoreManager.")
    }


    private fun onPlayerClick(player: Player) {
        // Log the player click action
        Log.d(TAG, "Player ${player.name} clicked. Showing PlayerStatsDialogFragment.")

        // Create an instance of PlayerStatsDialogFragment using the player ID
        val dialogFragment = PlayerStatsDialogFragment.newInstance(player.id)

        // Show the PlayerStatsDialogFragment
        dialogFragment.show((context as FragmentActivity).supportFragmentManager, PlayerStatsDialogFragment.TAG)
    }



    private fun observePlayers() {
        viewModel.players.observe(lifecycleOwner, Observer { players ->
            updateUI(players)
        })
    }

    private fun updateUI(players: List<Player>) {
        if (players.isNotEmpty()) {
            binding.playerListTitle.text = "Who Is Playing"
            binding.playerRecyclerView.isVisible = true
            binding.playerListTitle.isVisible = true

            adapter.submitList(players.toList()) {
                adapter.notifyDataSetChanged()
            }

            Log.d(TAG, "Player list updated. Number of players: ${players.size}")
        } else {
            Log.d(TAG, "No players found to display.")
            binding.playerListTitle.text = "No Players Available"
            binding.playerRecyclerView.isVisible = false
        }
    }

    fun addPlayer(playerName: String) {
        if (playerName.isNotEmpty()) {
            Log.d(TAG, "Attempting to add a new player with name: $playerName")

            // Generate a new Player object
            val newPlayer = Player(id = PlayerIDGenerator.generatePlayerID(), name = playerName)

            // Use PlayerController to add the player
            val isAdded = playerController.addPlayer(newPlayer)

            if (isAdded) {
                // Notify the ViewModel to update its LiveData and UI
                viewModel.addPlayer(newPlayer)

                // Clear the input field in the binding
                binding.playerNameInput.text.clear()

                Log.d(TAG, "Player added successfully: ${newPlayer.name}")
            } else {
                Log.d(TAG, "Player with ID ${newPlayer.id} already exists. Not adding again.")
            }
        } else {
            Log.d(TAG, "Player name input is empty, not adding player.")
        }
    }


    fun refreshPlayers() {
        viewModel.loadPlayers()
    }
}
