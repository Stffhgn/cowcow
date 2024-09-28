package com.example.cow_cow.controllers

import android.content.Context
import android.media.MediaPlayer
import java.lang.IllegalStateException

class SoundController(private val context: Context) {

    // MediaPlayer to handle sound playback
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Plays a sound based on the resource ID.
     *
     * @param soundResId The resource ID of the sound to be played.
     */
    fun playSound(soundResId: Int) {
        stopAndRelease() // Release any currently playing sound
        mediaPlayer = MediaPlayer.create(context, soundResId)

        mediaPlayer?.apply {
            setOnCompletionListener {
                stopAndRelease()
            }
            start()
        } ?: run {
            // Log or handle the case where MediaPlayer creation fails
            // E.g., show an error or fallback behavior
        }
    }

    /**
     * Stops and releases the MediaPlayer resources.
     */
    fun stopAndRelease() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release() // Free up MediaPlayer resources
        }
        mediaPlayer = null
    }

    /**
     * Clean up resources when no longer needed.
     */
    fun cleanup() {
        stopAndRelease()
    }
}
