package com.example.cow_cow.viewModels

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.models.DifficultyLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel to manage settings for the game.
 */
class SettingsViewModel(
    application: Application,
    private val sharedPreferences: SharedPreferences  // Using SharedPreferences for persisting settings
) : AndroidViewModel(application) {

    // --- Game Settings ---

    // Sound settings (muted or not)
    private val _isSoundMuted = MutableLiveData<Boolean>()
    val isSoundMuted: MutableLiveData<Boolean> get() = _isSoundMuted

    // Difficulty level settings (for various game modes like scavenger hunt, trivia, etc.)
    private val _difficultyLevel = MutableLiveData<DifficultyLevel>()
    val difficultyLevel: MutableLiveData<DifficultyLevel> get() = _difficultyLevel

    // Timer settings (for scavenger hunts, game duration, etc.)
    private val _timerDuration = MutableLiveData<Long>()  // Duration in milliseconds
    val timerDuration: MutableLiveData<Long> get() = _timerDuration

    private val _volumeLevel = MutableLiveData<Float>()
    val volumeLevel: MutableLiveData<Float> get() = _volumeLevel

    // --- UI Settings ---

    // Dark mode toggle
    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: MutableLiveData<Boolean> get() = _isDarkModeEnabled

    // Notification settings
    private val _isNotificationsEnabled = MutableLiveData<Boolean>()
    val isNotificationsEnabled: MutableLiveData<Boolean> get() = _isNotificationsEnabled

    // --- Initialization ---
    init {
        // Load initial settings from SharedPreferences
        loadSettings()
    }

    /**
     * Load all settings from SharedPreferences (or use default values).
     */
    private fun loadSettings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSoundMuted.postValue(sharedPreferences.getBoolean("sound_muted", false))
                _difficultyLevel.postValue(DifficultyLevel.valueOf(sharedPreferences.getString("difficulty_level", DifficultyLevel.MEDIUM.name)!!))
                _timerDuration.postValue(sharedPreferences.getLong("timer_duration", 300000L)) // Default to 5 minutes
                _isDarkModeEnabled.postValue(sharedPreferences.getBoolean("dark_mode", false))
                _isNotificationsEnabled.postValue(sharedPreferences.getBoolean("notifications_enabled", true))
            }
        }
    }

    /**
     * Save sound settings.
     */
    fun setSoundMuted(isMuted: Boolean) {
        _isSoundMuted.value = isMuted
        saveSetting("sound_muted", isMuted)
    }

    /**
     * Save difficulty level settings.
     */
    fun setDifficultyLevel(level: DifficultyLevel) {
        _difficultyLevel.value = level
        saveSetting("difficulty_level", level.name)
    }

    /**
     * Save timer duration settings.
     */
    fun setTimerDuration(duration: Long) {
        _timerDuration.value = duration
        saveSetting("timer_duration", duration)
    }

    /**
     * Toggle dark mode and save settings.
     */
    fun setDarkModeEnabled(isEnabled: Boolean) {
        _isDarkModeEnabled.value = isEnabled
        saveSetting("dark_mode", isEnabled)
    }

    /**
     * Toggle notification settings.
     */
    fun setNotificationsEnabled(isEnabled: Boolean) {
        _isNotificationsEnabled.value = isEnabled
        saveSetting("notifications_enabled", isEnabled)
    }

    // Function to set volume
    fun setVolumeLevel(volume: Float) {
        _volumeLevel.value = volume
        saveSetting("volume_level", volume)
    }

    // Add saveSetting for float values
    private fun saveSetting(key: String, value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().putFloat(key, value).apply()
        }

    // --- Save Settings Helper Functions ---

    /**
     * Generic save setting function for boolean values.
     */
    private fun saveSetting(key: String, value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }
    }

    /**
     * Generic save setting function for long values.
     */
    private fun saveSetting(key: String, value: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().putLong(key, value).apply()
        }
    }

    /**
     * Generic save setting function for string values.
     */
    private fun saveSetting(key: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().putString(key, value).apply()
        }
    }

    // --- Reset Settings ---

    /**
     * Reset all settings to default values.
     */
    fun resetSettingsToDefault() {
        setSoundMuted(false)
        setDifficultyLevel(DifficultyLevel.MEDIUM)
        setTimerDuration(300000L) // 5 minutes default
        setDarkModeEnabled(false)
        setNotificationsEnabled(true)
    }
}
