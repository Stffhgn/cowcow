package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cow_cow.R
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.models.Player
import com.example.cow_cow.databinding.FragmentCowCowBinding
import com.example.cow_cow.controllers.SoundController
import com.example.cow_cow.utils.Animations

class CowCowFragment : Fragment() {

    // View binding for the fragment layout
    private var _binding: FragmentCowCowBinding? = null
    private val binding get() = _binding!!

    // Instance of CowCowController
    private val cowCowController = CowCowController()

    // List of players
    private val players = mutableListOf(
        Player(id = 1, name = "Player 1"),
        Player(id = 2, name = "Player 2"),
        Player(id = 3, name = "Player 3")  // You can add as many players as needed
    )

    // Sound controller for audio feedback
    private val soundController = SoundController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCowCowBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    // Setup the button click listeners to allow any player to call objects
    private fun setupButtons() {
        binding.apply {
            // When Cow button is clicked
            cowButton.setOnClickListener {
                handleObjectCall("Cow")
            }

            // When Church button is clicked
            churchButton.setOnClickListener {
                handleObjectCall("Church")
            }

            // When Water Tower button is clicked
            waterTowerButton.setOnClickListener {
                handleObjectCall("Water Tower")
            }

            // Reset Game Button
            resetButton.setOnClickListener {
                cowCowController.resetGame(players)
                updatePlayerScoreDisplay()
                Toast.makeText(requireContext(), "Game Reset!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the call for Cow, Church, or Water Tower
    private fun handleObjectCall(objectType: String) {
        // Check which player called first (Simulate multiple players)
        val caller = getFirstPlayerToCall(objectType)

        // Validate the call
        if (cowCowController.validateCall(caller, objectType)) {
            // Apply points for the correct call
            val pointsAwarded = cowCowController.applyPoints(caller, objectType)
            soundController.playSound(objectType, requireContext())  // Play sound feedback

            // Show feedback on UI
            showFeedback(caller.name, objectType, pointsAwarded)
            updatePlayerScoreDisplay()

        } else {
            // Show invalid call message with shake animation
            Animations.shakeView(binding.root)
            Toast.makeText(requireContext(), "Invalid call!", Toast.LENGTH_SHORT).show()
        }
    }

    // Simulate the first player to call out the object
    private fun getFirstPlayerToCall(objectType: String): Player {
        // In a real game, this would be determined dynamically by who physically calls first
        // For this example, we will randomly select a player (you can replace with actual logic)
        return players.random()  // Simulate who called out first (you can replace with actual input)
    }

    // Update the player score display
    private fun updatePlayerScoreDisplay() {
        // Show all players' scores on screen (Leaderboard style)
        binding.playerScoreTextView.text = players.joinToString(separator = "\n") {
            "${it.name}: ${it.basePoints} points"
        }
    }

    // Show feedback when a player correctly calls an object
    private fun showFeedback(playerName: String, objectType: String, pointsAwarded: Int) {
        binding.feedbackTextView.text = "$playerName called $objectType! +$pointsAwarded points!"
        Animations.bounceView(binding.feedbackTextView)  // Apply bounce animation for feedback
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
