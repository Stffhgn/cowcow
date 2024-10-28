package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentGameStatsBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerStatsViewModel
import com.example.cow_cow.viewModels.PlayerStatsViewModelFactory

class GameStatsFragment : Fragment() {

    private var _binding: FragmentGameStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerStatsViewModel: PlayerStatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameStatsFragment", "onCreateView called, inflating layout")
        _binding = FragmentGameStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GameStatsFragment", "onViewCreated called")

        // Initialize PlayerRepository and PlayerStatsViewModelFactory
        val playerRepository = PlayerRepository(requireContext())
        val factory = PlayerStatsViewModelFactory(
            requireActivity().application,
            playerRepository,
            playerId = "some_player_id" // Replace with actual player ID logic
        )

        // Initialize PlayerStatsViewModel using ViewModelProvider and factory
        playerStatsViewModel = ViewModelProvider(this, factory).get(PlayerStatsViewModel::class.java)
        Log.d("GameStatsFragment", "PlayerStatsViewModel initialized")

        // Show loading indicator while data is being fetched
        binding.progressBar.visibility = View.VISIBLE
        Log.d("GameStatsFragment", "Loading indicator shown")

        // Observe LiveData to update UI with player data
        playerStatsViewModel.player.observe(viewLifecycleOwner) { player ->
            binding.progressBar.visibility = View.GONE // Hide loading indicator
            if (player != null) {
                bindPlayerData(player)
            } else {
                displayError("Player data not found")
            }
        }

        /* Handle errors from ViewModel
        playerStatsViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("GameStatsFragment", "Error: $it")
            }
        }*/
    }

    private fun bindPlayerData(player: Player) {
        Log.d("GameStatsFragment", "Binding player data to UI")

        /* Reset error view
        binding.errorTextView.visibility = View.GONE

        // Update the UI with player data
        binding.playerNameTextView.text = player.name
        binding.totalScoreTextView.text = "Total Score: ${player.basePoints}"
        binding.cowStatTextView.text = player.cowCount.toString()
        binding.churchStatTextView.text = player.churchCount.toString()
        binding.waterTowerStatTextView.text = player.waterTowerCount.toString()
        binding.winStreakTextView.text = "Win Streak: ${player.winStreak}"
        binding.penaltiesTextView.text = "Penalties: ${player.penaltyPoints}"
        binding.bonusPointsTextView.text = "Bonus Points: ${player.bonusPoints}"
        binding.rankTextView.text = "Rank: ${player.rank}"
 Add more fields as needed based on the player's attributes

         */
    }

    private fun displayError(message: String) {
        Log.d("GameStatsFragment", "Displaying error message: $message")
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("GameStatsFragment", "onDestroyView called, cleaning up binding")
        _binding = null
    }
}
