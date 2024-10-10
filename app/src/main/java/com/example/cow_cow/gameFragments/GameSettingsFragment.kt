package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentSettingsBinding
import com.example.cow_cow.viewModels.SettingsViewModel
import com.example.cow_cow.viewModels.SettingsViewModelFactory

class GameSettingsFragment : Fragment() {

    // Binding object instance with access to the views in fragment_settings.xml
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Obtain shared preferences from the activity
        val sharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

        // Instantiate SettingsViewModel with the custom factory
        val factory = SettingsViewModelFactory(requireActivity().application, sharedPreferences)
        settingsViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameSettingsFragment", "onCreateView called - Inflating FragmentSettingsBinding")
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GameSettingsFragment", "onViewCreated called - Setting up UI and Observers")

        // Setup the initial UI state from ViewModel
        setupUI()

        // Observe changes from ViewModel and update the UI accordingly
        observeViewModel()
    }

    private fun setupUI() {
        Log.d("GameSettingsFragment", "Setting up UI components with current ViewModel values")

        // Setup the sound toggle
        binding.soundSwitch.isChecked = settingsViewModel.isSoundMuted.value == false
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setSoundMuted(!isChecked)  // True if sound is muted
        }

        // Setup the difficulty spinner
        // Add other UI setup as needed, for example:
        // binding.volumeSlider.value = settingsViewModel.volumeLevel.value ?: 1.0f
    }

    private fun observeViewModel() {
        settingsViewModel.isSoundMuted.observe(viewLifecycleOwner) { isMuted ->
            binding.soundSwitch.isChecked = !isMuted
        }
        // Add other observers as needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
