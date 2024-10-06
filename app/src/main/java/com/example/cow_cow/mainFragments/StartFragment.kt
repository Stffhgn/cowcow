package com.example.cow_cow.mainFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.databinding.FragmentStartBinding
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.PlayerViewModelFactory

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the repository with context for SharedPreferences
        val repository = PlayerRepository(requireContext())

        // Create ViewModel using the custom factory
        val factory = PlayerViewModelFactory(requireActivity().application, repository)
        playerViewModel = ViewModelProvider(this, factory).get(PlayerViewModel::class.java)

        // Observe the list of players
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            if (players.isNullOrEmpty()) {
                // No players, show "Add Player" button
                binding.playerButton.text = "Add Player"
            } else {
                // Players exist, show "Who is Playing"
                binding.playerButton.text = "Who is Playing"
            }
        }

        // Set up button listeners
        setupButtons()
    }

    // Set up button click listeners
    private fun setupButtons() {
        binding.apply {

            // Start Game Button
            startGameButton.setOnClickListener {
                navigateToGameOrAddPlayer()
            }

            // Add Player or Who is Playing Button
            playerButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Who's Playing/Add Player fragment")
                findNavController().navigate(R.id.action_startFragment_to_whosPlayingFragment)
            }

            // How To Play Button
            howToPlayButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to How To Play fragment")
                findNavController().navigate(R.id.action_startFragment_to_howToPlayFragment)
            }

            // Settings Button
            settingsButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Settings fragment")
                findNavController().navigate(R.id.action_startFragment_to_appSettingsFragment)
            }

            // Store Button
            storeButton.setOnClickListener {
                Log.d("StartFragment", "Navigating to Store fragment")
                findNavController().navigate(R.id.action_startFragment_to_storeFragment)
            }
        }
    }

    private fun navigateToGameOrAddPlayer() {
        playerViewModel.players.value?.let { players ->
            if (players.isNullOrEmpty()) {
                Log.d("StartFragment", "No players found, navigating to Add Player screen")
                findNavController().navigate(R.id.action_startFragment_to_whosPlayingFragment)
            } else {
                Log.d("StartFragment", "Players found, starting GameActivity")
                val intent = Intent(requireContext(), GameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
                requireActivity().finish()  // Ensure the current activity is finished
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
