package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.DialogGameSettingsBinding
import com.example.cow_cow.viewModels.SettingsViewModel
import com.example.cow_cow.viewModels.SettingsViewModelFactory

class GameSettingsFragmentDialog : DialogFragment() {

    private var _binding: DialogGameSettingsBinding? = null
    private val binding get() = _binding!!

    // ViewModel for game settings
    private lateinit var settingsViewModel: SettingsViewModel

    companion object {
        const val TAG = "GameSettingsFragmentDialog"

        fun newInstance(): GameSettingsFragmentDialog {
            return GameSettingsFragmentDialog()
        }
    }

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
        Log.d(TAG, "onCreateView called - Inflating DialogGameSettingsBinding")
        _binding = DialogGameSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called - Setting up UI and Observers")

        // Setup the initial UI state from ViewModel
        setupUI()

        // Observe changes from ViewModel and update the UI accordingly
        observeViewModel()

        // Okay button to dismiss the dialog
        binding.okayButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupUI() {
        Log.d(TAG, "Setting up UI components with current ViewModel values")

        // Setup the sound toggle
        binding.soundSwitch.isChecked = settingsViewModel.isSoundMuted.value == false
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setSoundMuted(!isChecked)  // True if sound is muted
        }

        // Setup other UI components as needed
    }

    private fun observeViewModel() {
        settingsViewModel.isSoundMuted.observe(viewLifecycleOwner) { isMuted ->
            binding.soundSwitch.isChecked = !isMuted
        }
        // Add other observers as needed
    }

    override fun onStart() {
        super.onStart()
        // Adjust the size of the dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}