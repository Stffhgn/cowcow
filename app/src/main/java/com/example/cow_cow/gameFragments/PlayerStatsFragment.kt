package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentPlayerStatsBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class PlayerStatsFragment : Fragment() {

    private var _binding: FragmentPlayerStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private var playerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentPlayerStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the player ID from the arguments (if passed)
        arguments?.let {
            playerId = it.getInt("playerId", 0)
        }

        // Show an error if the playerId is invalid
        if (playerId == 0) {
            displayError("Invalid Player ID")
            return
        }

        // Initialize PlayerRepository and PlayerViewModelFactory
        val playerRepository = PlayerRepository()
        val factory = PlayerViewModelFactory(requireActivity().application, playerRepository)

        // Initialize PlayerViewModel using ViewModelProvider and factory
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Show loading indicator while data is being fetched
        binding.progressBar.visibility = View.VISIBLE

        // Observe LiveData to reactively update UI
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            // Hide loading indicator once data is loaded
            binding.progressBar.visibility = View.GONE

            val player = players.find { it.id == playerId }
            if (player != null) {
                // Bind player data to the UI
                bindPlayerData(player)
            } else {
                displayError("Player not found")
            }
        }

        // Handle errors from ViewModel (e.g., if loading fails)
        playerViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("PlayerStatsFragment", "Error: $it")  // Log for debugging
            }
        }
    }

    private fun bindPlayerData(player: Player) {
        // Reset error view
        binding.errorTextView.visibility = View.GONE

        // Update the UI with player data
        binding.playerNameTextView.text = player.name
        binding.totalScoreTextView.text = "Total Score: ${player.calculateTotalPoints()}"
        binding.cowStatTextView.text = player.cowCount.toString()
        binding.churchStatTextView.text = player.churchCount.toString()
        binding.waterTowerStatTextView.text = player.waterTowerCount.toString()
    }

    private fun displayError(message: String) {
        // Hide progress bar and show error text
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
