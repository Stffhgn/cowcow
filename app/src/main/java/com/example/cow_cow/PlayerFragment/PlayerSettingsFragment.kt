package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentPlayerSettingsBinding
import com.example.cow_cow.viewModels.PlayerViewModel

class PlayerSettingsFragment : Fragment() {

    private var _binding: FragmentPlayerSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerSettingsBinding.inflate(inflater, container, false)
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load player settings from ViewModel
        playerViewModel.loadPlayers()

        // Example setting listener for changing player name
        binding.saveButton.setOnClickListener {
            val newPlayerName = binding.playerNameEditText.text.toString()
            if (newPlayerName.isNotBlank()) {
                playerViewModel.updatePlayerName(newPlayerName)
                Toast.makeText(requireContext(), "Player name updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Switch for enabling or disabling sound
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            playerViewModel.updateSoundSettings(isChecked)
            val message = if (isChecked) "Sound enabled" else "Sound disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Switch for enabling or disabling notifications
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            playerViewModel.updateNotificationsSettings(isChecked)
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
