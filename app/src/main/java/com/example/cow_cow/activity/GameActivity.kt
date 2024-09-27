package com.example.cow_cow.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerDrawerAdapter
import com.example.cow_cow.databinding.ActivityGameBinding
import com.example.cow_cow.models.DifficultyLevel
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.viewmodel.GameViewModel
import com.example.cow_cow.viewmodel.GameViewModelFactory

const val EDIT_PLAYER_REQUEST_CODE = 1

class GameActivity : AppCompatActivity() {

    // View binding for UI elements
    private lateinit var binding: ActivityGameBinding

    // ViewModel instance
    private lateinit var gameViewModel: GameViewModel

    // Adapter for the player drawer
    private lateinit var drawerAdapter: PlayerDrawerAdapter

    // Media player for sounds
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize GameRepository and GameViewModelFactory
        val repository = GameRepository()  // Initialize repository
        val factory = GameViewModelFactory(application, repository)  // Initialize factory with application and repository

        // Initialize ViewModel
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Observe players LiveData to update UI
        gameViewModel.players.observe(this) { players ->
            val teamPlayers = players.filter { it.isOnTeam }
            val individualPlayers = players.filter { !it.isOnTeam }

            // Update the drawer adapter with new data
            drawerAdapter.updateData(teamPlayers, individualPlayers)
        }

        // Setup the navigation drawer
        setupDrawer()

        // Setup buttons and their click listeners
        setupButtons()

        // Update team score display
        updateTeamScore()
    }

    // Launch EditPlayerActivity to edit player data
    private fun editPlayer(player: Player) {
        val intent = Intent(this, EditPlayerActivity::class.java)
        intent.putExtra("PLAYER_DATA", player)
        startActivityForResult(intent, EDIT_PLAYER_REQUEST_CODE)
    }

    // Handle results from activities (e.g., EditPlayerActivity)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PLAYER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.let {
                val updatedPlayer = it.getParcelableExtra<Player>("UPDATED_PLAYER")
                val deletedPlayerId = it.getIntExtra("DELETED_PLAYER_ID", -1)

                updatedPlayer?.let { player ->
                    gameViewModel.updatePlayer(player)
                }

                if (deletedPlayerId != -1) {
                    gameViewModel.removePlayerById(deletedPlayerId)
                    Toast.makeText(this, "Player deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Update the player's score display
    private fun updatePlayerScoreDisplay(player: Player) {
        // Assuming there's a TextView to display the player's score
        binding.playerScoreTextView?.text = "Total Score: ${player.calculateTotalPoints()}"
    }

    // Setup buttons and their actions
    private fun setupButtons() {
        binding.apply {
            // Cow button
            cowButton.setOnClickListener {
                playSound(R.raw.cow_sound)
                showPlayerSelectionDialog("Cow") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Cow")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            // Church button
            churchButton.setOnClickListener {
                playSound(R.raw.church_sound)
                showPlayerSelectionDialog("Church") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Church")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            // Water Tower button
            waterTowerButton.setOnClickListener {
                playSound(R.raw.water_tower_sound)
                showPlayerSelectionDialog("Water Tower") { selectedPlayer ->
                    gameViewModel.applyPointsWithRules(selectedPlayer, "Water Tower")
                    updatePlayerScoreDisplay(selectedPlayer)
                }
            }

            // Team toggle button
            whiteFenceButton.setOnClickListener {
                showPlayerTeamToggleDialog()
            }

            // Back button
            backButton.setOnClickListener { finish() }

            // Reset button
            resetButton?.setOnClickListener {
                gameViewModel.resetCalledObjects()
                Toast.makeText(this@GameActivity, "Objects have been reset!", Toast.LENGTH_SHORT).show()
            }

            // Start scavenger hunt button
            startScavengerHuntButton?.setOnClickListener {
                gameViewModel.initializeScavengerHuntItems()
                gameViewModel.startScavengerHuntTimer(300000L) // 5 minutes
                updateScavengerHuntItemDisplay()
            }

            // More options button
            moreOptionsButton?.setOnClickListener {
                showScavengerHuntEditDialog()
            }

            // Player button (opens drawer)
            playerButton.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }

            // Scavenger hunt item click
            scavengerHuntItemTextView?.setOnClickListener {
                val scavengerHuntItem = gameViewModel.scavengerHuntList.firstOrNull()
                scavengerHuntItem?.let { item ->
                    val itemName = item.name
                    showPlayerSelectionDialog("Scavenger Hunt Item") { selectedPlayer ->
                        onScavengerItemFound(itemName, selectedPlayer)
                    }
                }
            }
        }
    }

    // Update scavenger hunt item display
    private fun updateScavengerHuntItemDisplay() {
        val scavengerHuntItem = gameViewModel.scavengerHuntList.firstOrNull()
        if (scavengerHuntItem != null) {
            val itemName = scavengerHuntItem.name
            val itemDifficulty = scavengerHuntItem.difficultyLevel
            binding.scavengerHuntItemTextView?.text = "Find this item: $itemName (Difficulty: $itemDifficulty)"
            binding.scavengerHuntItemTextView?.isClickable = true
        } else {
            binding.scavengerHuntItemTextView?.text = "No scavenger hunt item available."
        }
    }

    // Handle when a scavenger hunt item is found
    private fun onScavengerItemFound(itemName: String, selectedPlayer: Player) {
        if (gameViewModel.markScavengerItemAsFound(itemName, selectedPlayer)) {
            updatePlayerScoreDisplay(selectedPlayer)
            Toast.makeText(this, "${selectedPlayer.name} found the $itemName!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Item not found or already found!", Toast.LENGTH_SHORT).show()
        }
    }

    // Play sound effects
    private fun playSound(soundResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundResId).apply {
            start()
            setOnCompletionListener { release() }
        }
    }

    // Setup the navigation drawer
    private fun setupDrawer() {
        binding.navigationView.findViewById<RecyclerView>(R.id.drawerRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@GameActivity)
            drawerAdapter = PlayerDrawerAdapter(gameViewModel.team, gameViewModel.getIndividualPlayers())
            adapter = drawerAdapter
        }
    }

    // Show player selection dialog
    private fun showPlayerSelectionDialog(objectType: String, onPlayerSelected: (Player) -> Unit) {
        val playerList = gameViewModel.players.value ?: emptyList()
        val playerNames = playerList.map { it.name }.toTypedArray()

        if (playerNames.isEmpty()) {
            Toast.makeText(this, "No players available!", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Who found the $objectType?")
            .setItems(playerNames) { _, which ->
                val selectedPlayer = playerList[which]
                onPlayerSelected(selectedPlayer)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    // Show team toggle dialog
    private fun showPlayerTeamToggleDialog() {
        val playerList = gameViewModel.players.value ?: emptyList()

        if (playerList.isEmpty()) {
            Toast.makeText(this, "No players available!", Toast.LENGTH_SHORT).show()
            return
        }

        val playerNames = playerList.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(playerList.size) { i -> gameViewModel.team.contains(playerList[i]) }

        AlertDialog.Builder(this)
            .setTitle("Select Players to Join/Leave the Team")
            .setMultiChoiceItems(playerNames, checkedItems) { _, which, isChecked ->
                val player = playerList[which]
                if (isChecked) {
                    gameViewModel.addToTeam(player)
                } else {
                    gameViewModel.removeFromTeam(player)
                }
            }
            .setPositiveButton("Done") { dialog, _ ->
                updateTeamScore()
                dialog.dismiss()
            }
            .show()
    }

    // Update team score display
    private fun updateTeamScore() {
        val totalTeamScore = gameViewModel.calculateTeamScore()
        binding.teamScoreTextView?.text = "Team Score: $totalTeamScore"
    }

    // Show dialog to edit scavenger hunt items
    private fun showScavengerHuntEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_scavenger_hunt_item, null)
        val editItemName = dialogView.findViewById<EditText>(R.id.editItemName)
        val difficultySpinner = dialogView.findViewById<Spinner>(R.id.difficultySpinner)

        AlertDialog.Builder(this)
            .setTitle("Edit Scavenger Hunt Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val itemName = editItemName.text.toString()
                val selectedDifficulty = when (difficultySpinner.selectedItemPosition) {
                    0 -> DifficultyLevel.EASY
                    1 -> DifficultyLevel.MEDIUM
                    2 -> DifficultyLevel.HARD
                    else -> DifficultyLevel.EASY
                }

                if (itemName.isNotBlank()) {
                    val newItem = ScavengerHuntItem(name = itemName, difficultyLevel = selectedDifficulty)
                    gameViewModel.addOrEditScavengerHuntItem(newItem)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Load players and team when activity resumes
        gameViewModel.loadPlayers()
        gameViewModel.loadTeam()
    }

    override fun onPause() {
        super.onPause()
        // Save players and team when activity pauses
        gameViewModel.savePlayers()
        gameViewModel.saveTeam()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        gameViewModel.scavengerHuntTimer?.cancel()
        super.onDestroy()
    }
}
