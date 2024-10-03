package com.example.cow_cow.controllers

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.lang.IllegalStateException

class SoundController(private val context: Context) {

    // MediaPlayer to handle sound playback
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "SoundController"

    /**
     * Plays a sound based on the resource ID.
     *
     * @param soundResId The resource ID of the sound to be played.
     */
    fun playSound(soundResId: Int) {
        Log.d(TAG, "playSound: Playing sound with resource ID: $soundResId")
        stopAndRelease() // Release any currently playing sound

        try {
            mediaPlayer = MediaPlayer.create(context, soundResId)
            mediaPlayer?.apply {
                setOnCompletionListener {
                    stopAndRelease()
                    Log.d(TAG, "playSound: Sound playback completed.")
                }
                start()
                Log.d(TAG, "playSound: Sound playback started.")
            } ?: run {
                Log.e(TAG, "playSound: Failed to create MediaPlayer. Resource ID: $soundResId")
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "playSound: Error occurred while starting sound playback. ${e.message}")
        }
    }

    /**
     * Stops and releases the MediaPlayer resources.
     */
    fun stopAndRelease() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()
                    Log.d(TAG, "stopAndRelease: Stopping currently playing sound.")
                }
                release() // Free up MediaPlayer resources
                Log.d(TAG, "stopAndRelease: MediaPlayer resources released.")
            } catch (e: IllegalStateException) {
                Log.e(TAG, "stopAndRelease: Error while stopping or releasing MediaPlayer. ${e.message}")
            }
        }
        mediaPlayer = null
    }

    /**
     * Clean up resources when no longer needed.
     */
    fun cleanup() {
        Log.d(TAG, "cleanup: Cleaning up MediaPlayer resources.")
        stopAndRelease()
    }
}
