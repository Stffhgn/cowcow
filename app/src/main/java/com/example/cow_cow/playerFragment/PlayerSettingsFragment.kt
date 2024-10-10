package com.example.cow_cow.playerFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentPlayerSettingsBinding
import com.example.cow_cow.viewModels.PlayerViewModel
import com.example.cow_cow.viewModels.SettingsViewModel
import com.example.cow_cow.viewModels.SettingsViewModelFactory

class PlayerSettingsFragment : Fragment() {

    private var _binding: FragmentPlayerSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val factory = SettingsViewModelFactory(requireActivity().application, sharedPreferences)
        settingsViewModel = ViewModelProvider(requireActivity(), factory).get(SettingsViewModel::class.java)
    }

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

        // Load player data from ViewModel
        playerViewModel.loadPlayers()

        // Observe selected player
        playerViewModel.selectedPlayer.observe(viewLifecycleOwner) { player ->
            player?.let {
                binding.playerNameEditText.setText(it.name)
                // Synchronize notifications setting
                settingsViewModel.setNotificationsEnabled(it.notificationsEnabled)
            }
        }

        // Observe sound settings
        settingsViewModel.isSoundMuted.observe(viewLifecycleOwner) { isMuted ->
            binding.soundSwitch.isChecked = !isMuted
        }

        // Observe notifications settings
        settingsViewModel.isNotificationsEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.notificationsSwitch.isChecked = isEnabled
        }

        // Listener for saving player name
        binding.saveButton.setOnClickListener {
            val newPlayerName = binding.playerNameEditText.text.toString()
            val selectedPlayer = playerViewModel.selectedPlayer.value

            if (newPlayerName.isNotBlank() && selectedPlayer != null) {
                playerViewModel.updatePlayerName(selectedPlayer.id, newPlayerName, requireContext())
                Toast.makeText(requireContext(), "Player name updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener for sound switch
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setSoundMuted(!isChecked)
            val message = if (isChecked) "Sound enabled" else "Sound muted"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Listener for notifications switch
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setNotificationsEnabled(isChecked)
            val message = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            // Update Player data
            val selectedPlayer = playerViewModel.selectedPlayer.value
            if (selectedPlayer != null) {
                val updatedPlayer = selectedPlayer.copy(notificationsEnabled = isChecked)
                playerViewModel.updatePlayer(updatedPlayer)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
