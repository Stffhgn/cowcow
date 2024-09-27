package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cow_cow.R
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.models.Player
import com.example.cow_cow.databinding.FragmentCowCowBinding

class CowCowFragment : Fragment() {

    // View binding for the fragment layout
    private var _binding: FragmentCowCowBinding? = null
    private val binding get() = _binding!!

    // Instance of CowCowController
    private val cowCowController = CowCowController()

    // Example player (In a real case, this would come from the ViewModel or a shared source)
    private val player = Player(id = 1, name = "Player 1")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCowCowBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    // Setup the button click listeners to interact with the controller
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
                cowCowController.resetGame(listOf(player))  // You can reset all players here
                updatePlayerScoreDisplay()
            }
        }
    }

    // Handle the call for Cow, Church, Water Tower
    private fun handleObjectCall(objectType: String) {
        // Validate the call
        if (cowCowController.validateCall(player, objectType)) {
            // Apply the points
            val pointsAwarded = cowCowController.applyPoints(player, objectType)
            // Show feedback on the UI
            updatePlayerScoreDisplay()
        } else {
            // Show invalid call message (optional)
        }
    }

    // Update the player score in the UI
    private fun updatePlayerScoreDisplay() {
        binding.playerScoreTextView.text = "Total Score: ${player.basePoints}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
