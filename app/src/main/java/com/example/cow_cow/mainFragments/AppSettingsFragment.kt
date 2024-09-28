package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.cow_cow.R
import com.example.cow_cow.repositories.SettingsRepository
import com.google.android.material.button.MaterialButton

class AppSettingsFragment : Fragment() {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var soundSwitch: Switch
    private lateinit var notificationsSwitch: Switch
    private lateinit var saveButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsRepository = SettingsRepository(requireContext())

        soundSwitch = view.findViewById(R.id.soundSwitch)
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch)
        saveButton = view.findViewById(R.id.saveButton)

        // Load the existing settings
        loadSettings()

        // Set up save button click listener
        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    // Load settings from the repository and update the UI
    private fun loadSettings() {
        soundSwitch.isChecked = settingsRepository.getBoolean("SOUND_ENABLED", true)
        notificationsSwitch.isChecked = settingsRepository.getBoolean("NOTIFICATIONS_ENABLED", true)
    }

    // Save settings when the user clicks save
    private fun saveSettings() {
        settingsRepository.saveBoolean("SOUND_ENABLED", soundSwitch.isChecked)
        settingsRepository.saveBoolean("NOTIFICATIONS_ENABLED", notificationsSwitch.isChecked)
    }
}
