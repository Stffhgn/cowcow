package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentProfileBinding
import com.example.cow_cow.viewModels.PlayerViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Observe player data and update UI
        playerViewModel.selectedPlayer.observe(viewLifecycleOwner, Observer { player ->
            player?.let {
                binding.playerNameTextView.text = it.name
                binding.gamesPlayedTextView.text = "Games Played: ${it.gamesPlayed}"
                binding.playerRankTextView.text = "Player Rank: ${it.rank}"
                // Load profile image (placeholder logic)
                binding.playerProfileImageView.setImageResource(R.drawable.ic_launcher_round)
            }
        })

        // Edit profile button logic
        binding.editProfileButton.setOnClickListener {
            // Navigate to profile edit screen or show a dialog
            showEditProfileDialog()
        }
    }

    private fun showEditProfileDialog() {
        // Logic for showing a dialog or fragment for editing the profile
        // You can pass the player data to the dialog to edit the name or avatar
    }
}
