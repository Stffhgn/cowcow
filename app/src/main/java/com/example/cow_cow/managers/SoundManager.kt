package com.example.cow_cow.managers

import android.content.Context
import android.media.MediaPlayer
import com.example.cow_cow.R
import com.example.cow_cow.enums.SoundType

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null
    private var isMuted: Boolean = false
    private var volumeLevel: Float = 1.0f  // Full volume by default

    /**
     * Play a sound effect based on the specified SoundType.
     *
     * @param context The context used to access resources.
     * @param soundType The type of sound to play (e.g., COW_SOUND, CHURCH_SOUND).
     */
    fun playSound(context: Context, soundType: SoundType) {
        if (isMuted) return  // If sound is muted, do not play anything

        stopCurrentSound()  // Stop any currently playing sound

        mediaPlayer = when (soundType) {
            SoundType.COW_SOUND -> MediaPlayer.create(context, R.raw.cow_sound)
            SoundType.CHURCH_SOUND -> MediaPlayer.create(context, R.raw.church_sound)
            SoundType.WATER_TOWER_SOUND -> MediaPlayer.create(context, R.raw.water_tower_sound)
            SoundType.BACKGROUND_MUSIC -> MediaPlayer.create(context, R.raw.background_music)
            SoundType.ALERT -> MediaPlayer.create(context, R.raw.alert_sound)
            // Add other sounds here as needed
        }

        mediaPlayer?.setVolume(volumeLevel, volumeLevel)
        mediaPlayer?.start()  // Play the sound
    }

    /**
     * Stops the currently playing sound, if any.
     */
    fun stopCurrentSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Mute all sound effects.
     */
    fun muteSounds() {
        isMuted = true
        stopCurrentSound()  // Stop any playing sound if muted
    }

    /**
     * Unmute all sound effects.
     */
    fun unmuteSounds() {
        isMuted = false
    }

    /**
     * Toggle between mute and unmute states.
     */
    fun toggleMute() {
        if (isMuted) {
            unmuteSounds()
        } else {
            muteSounds()
        }
    }

    /**
     * Adjust the volume of sound effects.
     *
     * @param volume The volume level (0.0f is mute, 1.0f is full volume).
     */
    fun setVolume(volume: Float) {
        volumeLevel = volume.coerceIn(0.0f, 1.0f)  // Ensure volume stays between 0.0 and 1.0
        mediaPlayer?.setVolume(volumeLevel, volumeLevel)
    }

    /**
     * Release resources when the game or activity ends.
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
