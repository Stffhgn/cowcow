package com.example.cow_cow.repositories

import android.content.Context
import android.media.MediaPlayer
import com.example.cow_cow.R
import com.example.cow_cow.enums.SoundType
import com.example.cow_cow.managers.SoundManager
import com.example.cow_cow.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class SoundRepository(private val context: Context) {

    private val soundFiles: MutableMap<SoundType, File> = mutableMapOf()

    /**
     * Play a sound based on the SoundType.
     */
    fun playSoundFromRepository(soundType: SoundType): MediaPlayer? {
        val soundFile = loadSound(soundType)
        return if (soundFile != null) {
            MediaPlayer().apply {
                setDataSource(soundFile.absolutePath)
                prepare()
                start()
            }
        } else {
            // Fallback to default sound from SoundManager if no local file exists
            SoundManager.playSound(context, soundType)
            null
        }
    }

    /**
     * Load a sound file based on the SoundType, or return the default sound.
     */
    private fun loadSound(soundType: SoundType): File? {
        return soundFiles[soundType] ?: getDefaultSound(soundType)
    }

    /**
     * Get default sound file for a given sound type.
     */
    private fun getDefaultSound(soundType: SoundType): File? {
        val resId = when (soundType) {
            SoundType.COW_SOUND -> R.raw.cow_sound
            SoundType.CHURCH_SOUND -> R.raw.church_sound
            SoundType.WATER_TOWER_SOUND -> R.raw.water_tower_sound
            //SoundType.BACKGROUND_MUSIC -> R.raw.background_music
            //SoundType.ALERT -> R.raw.alert_sound
        }
        return try {
            FileUtils.getFileFromResource(context, resId)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Stop the currently playing sound.
     */
    fun stopCurrentSound() {
        SoundManager.stopCurrentSound()
    }

    /**
     * Set the volume for all sounds.
     */
    fun setVolume(volume: Float) {
        SoundManager.setVolume(volume)
    }

    /**
     * Mute or unmute all sounds.
     */
    fun mute(isMuted: Boolean) {
        if (isMuted) {
            SoundManager.muteSounds()
        } else {
            SoundManager.unmuteSounds()
        }
    }

    /**
     * Reset sound settings (mute status, volume).
     */
    fun reset() {
        SoundManager.setVolume(1.0f) // Reset to full volume
        SoundManager.unmuteSounds()   // Unmute all sounds
    }

    /**
     * Update sound files from an OTA (Over-The-Air) update.
     */
    suspend fun updateSoundsFromOTA(otaUrls: Map<SoundType, String>) {
        withContext(Dispatchers.IO) {
            for ((soundType, url) in otaUrls) {
                try {
                    val newSoundFile = FileUtils.downloadFile(context, url, soundType.name)
                    soundFiles[soundType] = newSoundFile
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
