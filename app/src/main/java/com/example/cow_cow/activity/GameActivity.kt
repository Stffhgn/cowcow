package com.example.cow_cow.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.adapters.PlayerDrawerAdapter
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewmodel.GameViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameViewModel: GameViewModel
    private lateinit var drawerAdapter: PlayerDrawerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the ViewModel
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Load players
        gameViewModel.loadPlayers(this)

        // Load team
        gameViewModel.loadTeam(this)

        // Initialize the Drawer Adapter
        setupDrawer()

        // Set up the "Who's Playing" button to open the drawer
        binding.playerButton?.setOnClickListener {
            binding.drawerLayout?.openDrawer(GravityCompat.END)
        }

        // Set up buttons
        binding.cowButton.setOnClickListener {
            showPlayerSelectionDialog("Cow")
        }
        binding.churchButton.setOnClickListener {
            showPlayerSelectionDialog("Church")
        }
        binding.waterTowerButton.setOnClickListener {
            showPlayerSelectionDialog("Water Tower")
        }
        binding.whiteFenceButton.setOnClickListener {
            showPlayerSelectionDialog("White Fence")
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        // Update the initial team score
        updateTeamScore()
    }

    private fun setupDrawer() {
        val drawerRecyclerView = binding.navigationView.findViewById<RecyclerView>(R.id.drawerRecyclerView)
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerAdapter = PlayerDrawerAdapter(gameViewModel.team, gameViewModel.getIndividualPlayers())
        drawerRecyclerView.adapter = drawerAdapter
    }

    private fun updateDrawerList() {
        drawerAdapter.updateData(gameViewModel.team, gameViewModel.getIndividualPlayers())
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun showPlayerSelectionDialog(action: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_player_list, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = if (action == "White Fence") "Select Players for the Team" else "Who Called It?"

        val playerRecyclerView = dialogView.findViewById<RecyclerView>(R.id.playerRecyclerView)
        playerRecyclerView.layoutManager = LinearLayoutManager(this)

        val playerAdapter = PlayerAdapter(gameViewModel.players) { player ->
            handlePlayerSelection(action, player)
            dialog.dismiss()
        }
        playerRecyclerView.adapter = playerAdapter

        dialog.show()
    }

    // Modify handlePlayerSelection
    private fun handlePlayerSelection(action: String, player: Player) {
        if (action == "White Fence") {
            gameViewModel.togglePlayerInTeam(player, this) // Pass context
        } else {
            gameViewModel.incrementPlayerCount(player, action)
        }

        // Save players
        gameViewModel.savePlayers(this)

        updateTeamScore()
        updateDrawerList()
    }

    private fun updateTeamScore() {
        val combinedPoints = gameViewModel.calculateTeamScore()
        binding.teamPointsTextView.text = "Team Points: $combinedPoints"
    }

    // Save team data when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        gameViewModel.saveTeam(this)
    }
}
