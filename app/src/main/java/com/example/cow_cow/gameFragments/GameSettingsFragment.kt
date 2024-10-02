package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cow_cow.databinding.FragmentSettingsBinding
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.viewModels.SettingsViewModel

class GameSettingsFragment : Fragment() {

    // Binding object instance with access to the views in fragment_settings.xml
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the initial UI state from ViewModel
        setupUI()

        // Observe changes from ViewModel and update the UI accordingly
        observeViewModel()
    }

    private fun setupUI() {
        // Setup the sound toggle
        binding.soundSwitch.isChecked = settingsViewModel.isSoundMuted.value == false
        binding.soundSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            settingsViewModel.setSoundMuted(!isChecked)  // True if sound is muted
        }

        // Setup the difficulty spinner
        binding.difficultySpinner.setSelection(getDifficultyPosition(settingsViewModel.difficultyLevel.value))
        binding.difficultySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val difficulty = getDifficultyLevelFromPosition(position)
                settingsViewModel.setDifficultyLevel(difficulty)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        // Setup the dark mode switch
        binding.darkModeSwitch.isChecked = settingsViewModel.isDarkModeEnabled.value == true
        binding.darkModeSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            settingsViewModel.setDarkModeEnabled(isChecked)
        }

        // Setup the notifications switch
        binding.notificationsSwitch.isChecked = settingsViewModel.isNotificationsEnabled.value == true
        binding.notificationsSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            settingsViewModel.setNotificationsEnabled(isChecked)
        }

        // Setup the timer duration slider
        binding.timerSeekBar.progress = (settingsViewModel.timerDuration.value ?: 300000L).toInt() / 1000
        binding.timerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                settingsViewModel.setTimerDuration(progress * 1000L)  // Save in milliseconds
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun observeViewModel() {
        // Observe settings data from ViewModel and update UI accordingly

        settingsViewModel.isSoundMuted.observe(viewLifecycleOwner) { isMuted ->
            binding.soundSwitch.isChecked = !isMuted
        }

        settingsViewModel.difficultyLevel.observe(viewLifecycleOwner) { level: DifficultyLevel ->
            binding.difficultySpinner.setSelection(getDifficultyPosition(level))
        }

        settingsViewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.darkModeSwitch.isChecked = isEnabled
        }

        settingsViewModel.isNotificationsEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.notificationsSwitch.isChecked = isEnabled
        }

        settingsViewModel.timerDuration.observe(viewLifecycleOwner) { duration ->
            binding.timerSeekBar.progress = (duration / 1000).toInt()
        }
    }

    // Helper method to get the selected difficulty position
    private fun getDifficultyPosition(level: DifficultyLevel?): Int {
        return when (level) {
            DifficultyLevel.EASY -> 0
            DifficultyLevel.MEDIUM -> 1
            DifficultyLevel.HARD -> 2
            else -> 1 // Default to medium
        }
    }

    // Helper method to get the selected difficulty level from spinner position
    private fun getDifficultyLevelFromPosition(position: Int): DifficultyLevel {
        return when (position) {
            0 -> DifficultyLevel.EASY
            1 -> DifficultyLevel.MEDIUM
            2 -> DifficultyLevel.HARD
            else -> DifficultyLevel.MEDIUM
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
