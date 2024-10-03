package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.ScavengerHuntAdapter
import com.example.cow_cow.databinding.FragmentScavengerHuntBinding
import com.example.cow_cow.enums.AgeGroup
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.LocationType
import com.example.cow_cow.enums.Season
import com.example.cow_cow.enums.TimeOfDay
import com.example.cow_cow.enums.WeatherCondition
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.viewModels.GameViewModel
import com.example.cow_cow.viewModels.ScavengerHuntViewModel

class ScavengerHuntFragment : Fragment() {

    private var _binding: FragmentScavengerHuntBinding? = null
    private val binding get() = _binding!!

    private lateinit var scavengerHuntViewModel: ScavengerHuntViewModel
    private lateinit var gameViewModel: GameViewModel
    private lateinit var scavengerHuntAdapter: ScavengerHuntAdapter
    // Add this line to instantiate the repository
    private lateinit var scavengerHuntRepository: ScavengerHuntRepository
    // Logging tag for debugging
    private val TAG = "ScavengerHuntFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScavengerHuntBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "Fragment created and view initialized.")

        // Initialize ViewModels
        scavengerHuntViewModel = ViewModelProvider(this).get(ScavengerHuntViewModel::class.java)
        gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        // Observe players LiveData from the GameViewModel
        gameViewModel.players.observe(viewLifecycleOwner) { playerList ->
            if (playerList.isNotEmpty()) {
                Log.d(TAG, "Players loaded: ${playerList.size}")

                // Find the current player
                val player = playerList.find { it.isCurrentPlayer }

                if (player != null) {
                    // Initialize scavenger hunt with the player and the scavenger hunt repository
                    gameViewModel.initializeScavengerHunt(player, scavengerHuntRepository, requireContext(), "Easy")
                    Log.d(TAG, "Initialized scavenger hunt for player: ${player.name}")
                } else {
                    Log.e(TAG, "Current player not found.")
                }

            } else {
                Log.e(TAG, "No players found.")
            }
        }

        // Initialize the adapter with an empty list and the click handler
        scavengerHuntAdapter = ScavengerHuntAdapter(emptyList()) { item ->
            Log.d(TAG, "Scavenger hunt item clicked: ${item.name}")
            val currentPlayer = getCurrentPlayer()
            gameViewModel.markScavengerItemAsFound(item.name, currentPlayer)
            Toast.makeText(requireContext(), "${item.name} found!", Toast.LENGTH_SHORT).show()

            // Check if the scavenger hunt is complete
            if (gameViewModel.isScavengerHuntComplete()) {
                Log.d(TAG, "Scavenger hunt completed.")
                Toast.makeText(requireContext(), "Scavenger Hunt Complete!", Toast.LENGTH_LONG).show()
            }
        }

        // Set up RecyclerView with the scavenger hunt adapter
        binding.scavengerHuntRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scavengerHuntAdapter
        }

        // Observe the scavenger hunt items and update the RecyclerView when they change
        scavengerHuntViewModel.scavengerHuntItems.observe(viewLifecycleOwner) { items ->
            Log.d(TAG, "Scavenger hunt items updated: ${items.size} items loaded.")
            scavengerHuntAdapter.updateData(items)
        }

        // Show status messages from the ViewModel
        scavengerHuntViewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Log.d(TAG, "Status message: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Load scavenger hunt items when the fragment is created
        Log.d(TAG, "Loading scavenger hunt items.")
        scavengerHuntViewModel.loadScavengerHuntItems()

        // Add a scavenger hunt item when the button is clicked
        binding.addScavengerHuntButton.setOnClickListener {
            val newItem = ScavengerHuntItem(
                name = "Find a blue car",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.TEENS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.FALL
            )
            Log.d(TAG, "Adding new scavenger hunt item: ${newItem.name}")
            scavengerHuntViewModel.addScavengerHuntItem(newItem, requireContext())
            Toast.makeText(requireContext(), "Scavenger hunt item added!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This method returns the current player. Replace this logic with the actual way
     * you retrieve the active player in your app.
     */
    private fun getCurrentPlayer(): Player {
        Log.d(TAG, "Retrieving current player.")
        return Player(1, "Player 1") // Replace this with the real player retrieval logic
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "Fragment view destroyed.")
        _binding = null
    }
}
