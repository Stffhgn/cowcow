package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.enums.SoundType
import com.example.cow_cow.repositories.SoundRepository

class SoundViewModel(application: Application, private val soundRepository: SoundRepository) : AndroidViewModel(application) {

    // LiveData to track if a sound is currently playing
    private val _isSoundPlaying = MutableLiveData<Boolean>()
    val isSoundPlaying: LiveData<Boolean> get() = _isSoundPlaying

    // LiveData to track the current sound settings (volume, etc.)
    private val _soundSettings = MutableLiveData<SoundSettings>()
    val soundSettings: LiveData<SoundSettings> get() = _soundSettings

    // LiveData for the current sound type being played
    private val _currentSoundType = MutableLiveData<SoundType?>()
    val currentSoundType: LiveData<SoundType?> get() = _currentSoundType

    // Initialize with default settings (can be adjusted later)
    init {
        _isSoundPlaying.value = false
        _soundSettings.value = SoundSettings(volume = 1.0f) // Default volume at 100%
    }

    /**
     * Play a sound based on the provided SoundType.
     */
    fun playSound(soundType: SoundType) {
        val mediaPlayer = soundRepository.playSoundFromRepository(soundType)
        if (mediaPlayer != null) {
            _isSoundPlaying.value = true
            _currentSoundType.value = soundType
        } else {
            _isSoundPlaying.value = false
        }
    }

    /**
     * Stop the currently playing sound.
     */
    fun stopSound() {
        soundRepository.stopCurrentSound()
        _isSoundPlaying.value = false
        _currentSoundType.value = null
    }

    /**
     * Adjust the volume of the sound system.
     */
    fun setVolume(volume: Float) {
        _soundSettings.value = _soundSettings.value?.copy(volume = volume)
        soundRepository.setVolume(volume)
    }

    /**
     * Mute or unmute the sound system.
     */
    fun toggleMute(isMuted: Boolean) {
        _soundSettings.value = _soundSettings.value?.copy(isMuted = isMuted)
        soundRepository.mute(isMuted)
    }

    /**
     * Check if a specific sound is currently playing.
     */
    fun isSpecificSoundPlaying(soundType: SoundType): Boolean {
        return _currentSoundType.value == soundType && _isSoundPlaying.value == true
    }

    /**
     * Update sound settings, such as volume or mute status.
     */
    fun updateSoundSettings(settings: SoundSettings) {
        _soundSettings.value = settings
        soundRepository.setVolume(settings.volume)
        soundRepository.mute(settings.isMuted)
    }

    /**
     * Reset the sound system to default settings.
     */
    fun resetSoundSettings() {
        _soundSettings.value = SoundSettings(volume = 1.0f)
        soundRepository.reset()
    }

    /**
     * Update sound files via OTA (Over-The-Air).
     */
    fun updateSoundsFromOTA(otaUrls: Map<SoundType, String>) {
        viewModelScope.launch {
            soundRepository.updateSoundsFromOTA(otaUrls)
        }
    }
}