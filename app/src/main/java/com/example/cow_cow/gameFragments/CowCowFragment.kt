package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.controllers.MainGameController
import com.example.cow_cow.managers.GameNewsManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.databinding.FragmentCowCowBinding
import com.example.cow_cow.controllers.SoundController
import com.example.cow_cow.utils.Animations
import com.example.cow_cow.viewModels.GameViewModel

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
        Player(id = 3, name = "Player 3")
    )

    // Sound controller for audio feedback
    private lateinit var soundController: SoundController

    // ViewModel for game logic
    private lateinit var gameViewModel: GameViewModel

    // GameNewsManager for updating game news
    private val gameNewsManager = GameNewsManager()

    // Logging tag
    private val TAG = "CowCowFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Fragment view is being created")
        _binding = FragmentCowCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        Log.d(TAG, "onViewCreated: ViewModel initialized")

        // Initialize SoundController with context
        soundController = SoundController(requireContext())
        Log.d(TAG, "onViewCreated: SoundController initialized")

        // Setup button click listeners
        setupButtons()
    }

    // Setup the button click listeners
    private fun setupButtons() {
        binding.apply {
            cowButton.setOnClickListener {
                Log.d(TAG, "Cow button clicked")
                handleObjectCall("Cow")
            }
            churchButton.setOnClickListener {
                Log.d(TAG, "Church button clicked")
                handleObjectCall("Church")
            }
            waterTowerButton.setOnClickListener {
                Log.d(TAG, "Water Tower button clicked")
                handleObjectCall("Water Tower")
            }
            resetButton.setOnClickListener {
                Log.d(TAG, "Reset button clicked")
                cowCowController.resetGame(players)
                updatePlayerScoreDisplay()
                Toast.makeText(requireContext(), "Game Reset!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle the call for Cow, Church, or Water Tower
    private fun handleObjectCall(objectType: String) {
        Log.d(TAG, "handleObjectCall: $objectType called")
        val caller = getFirstPlayerToCall(objectType)
        Log.d(TAG, "handleObjectCall: Player ${caller.name} called $objectType")

        if (cowCowController.validateCall(caller, objectType)) {
            val pointsAwarded = cowCowController.applyPoints(caller, objectType)
            Log.d(TAG, "handleObjectCall: Valid call. $pointsAwarded points awarded to ${caller.name}")

            // Play the sound depending on the object type
            val soundResId = when (objectType) {
                "Cow" -> R.raw.cow_sound
                "Church" -> R.raw.church_sound
                "Water Tower" -> R.raw.water_tower_sound
                else -> R.raw.cow_sound
            }
            soundController.playSound(soundResId)
            Log.d(TAG, "handleObjectCall: Sound played for $objectType")

            // Update game news using GameNewsManager
            val newsMessage = "${caller.name} called $objectType and earned $pointsAwarded points!"
            gameNewsManager.addNewsMessage(newsMessage)
            Log.d(TAG, "handleObjectCall: News updated with message - $newsMessage")

            // Rotate the news to show the latest update
            gameViewModel.rotateGameNews()
            Log.d(TAG, "handleObjectCall: Game news rotated")

            // Update the player score display
            updatePlayerScoreDisplay()
        } else {
            // Handle invalid call with an animation or message
            Log.d(TAG, "handleObjectCall: Invalid call by ${caller.name}")
            Animations.shakeView(binding.root)
        }
    }

    // Simulate which player calls first
    private fun getFirstPlayerToCall(objectType: String): Player {
        val player = players.random()
        Log.d(TAG, "getFirstPlayerToCall: Randomly selected player is ${player.name}")
        return player
    }

    // Update player score display
    private fun updatePlayerScoreDisplay() {
        val scoreDisplay = players.joinToString("\n") {
            "${it.name}: ${it.basePoints} points"
        }
        Log.d(TAG, "updatePlayerScoreDisplay: $scoreDisplay")
        binding.playerScoreTextView.text = scoreDisplay
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: Fragment view is being destroyed")
        _binding = null
        soundController.cleanup()  // Clean up sound resources when fragment is destroyed
        Log.d(TAG, "onDestroyView: SoundController resources cleaned up")
    }
}
