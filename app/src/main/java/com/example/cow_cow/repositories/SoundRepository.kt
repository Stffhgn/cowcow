package com.example.cow_cow.repositories

import android.content.Context
import android.media.MediaPlayer
import com.example.cow_cow.enums.SoundType
import com.example.cow_cow.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class SoundRepository(private val context: Context) {

    private val soundFiles: MutableMap<SoundType, File> = mutableMapOf()

    /**
     * Loads a sound file based on the SoundType.
     * This checks local storage for the sound file or returns a default file if none exists.
     */
    fun loadSound(soundType: SoundType): File? {
        return soundFiles[soundType] ?: getDefaultSound(soundType)
    }

    /**
     * Get default sound file for a given sound type.
     * You can define these defaults to always be available in your assets folder or resource folder.
     */
    private fun getDefaultSound(soundType: SoundType): File? {
        val resId = when (soundType) {
            SoundType.COW_SOUND -> R.raw.cow_sound
            SoundType.CHURCH_SOUND -> R.raw.church_sound
            SoundType.WATER_TOWER_SOUND -> R.raw.water_tower_sound
            SoundType.BACKGROUND_MUSIC -> R.raw.background_music
            SoundType.ALERT -> R.raw.alert_sound
        }
        return try {
            FileUtils.getFileFromResource(context, resId)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Update sound files from an OTA (Over-The-Air) update.
     * Downloads the sound files from a remote server, replacing old files.
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

    /**
     * Save sound files to local storage after OTA update.
     */
    fun saveUpdatedSound(soundType: SoundType, file: File) {
        soundFiles[soundType] = file
    }

    /**
     * Play a sound file directly from the repository.
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
            null
        }
    }

    /**
     * Clear outdated sound files from local storage.
     * This can be used to free up space or reset sound assets to default.
     */
    fun clearOldSoundFiles() {
        soundFiles.clear()
        FileUtils.clearLocalCache(context)
    }
}
