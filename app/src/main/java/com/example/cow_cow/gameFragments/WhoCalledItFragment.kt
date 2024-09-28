package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class WhoCalledItFragment : Fragment() {

    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private var playerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the player ID from arguments (with a default fallback of 0)
        arguments?.let {
            playerId = it.getInt("PLAYER_ID", 0)
        }

        // Ensure we have a valid player ID
        if (playerId == 0) {
            showError("Invalid Player ID")
            return
        }

        // Initialize PlayerRepository and PlayerViewModelFactory
        val playerRepository = PlayerRepository()
        val factory = PlayerViewModelFactory(requireActivity().application, playerRepository)

        // Initialize PlayerViewModel using ViewModelProvider and factory
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Fetch the player data using playerId and observe changes
        playerViewModel.getPlayerById(playerId)?.observe(viewLifecycleOwner) { player ->
            player?.let {
                bindPlayerData(it)
            } ?: showError("Player not found")
        }
    }

    // Method to bind player data to the UI
    private fun bindPlayerData(player: Player) {
        binding.apply {
            playerName.text = player.name
            playerTotalScore.text = getString(R.string.total_score, player.calculateTotalPoints())
            cowStat.text = player.cowCount.toString()
            churchStat.text = player.churchCount.toString()
            waterTowerStat.text = player.waterTowerCount.toString()
        }
    }

    // Method to show error messages via Toast
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
