package com.example.cow_cow.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class PlayerSettings(
    var soundEnabled: Boolean = true,     // Whether sound effects are enabled
    var notificationsEnabled: Boolean = true, // Whether notifications are enabled
    var difficultyLevel: String = "Normal",   // Difficulty level (Easy, Normal, Hard)
    var hintsEnabled: Boolean = true         // Whether hints are enabled
) : Parcelable {

    private val TAG = "PlayerSettings"

    init {
        Log.d(TAG, "PlayerSettings initialized with soundEnabled: $soundEnabled, notificationsEnabled: $notificationsEnabled, difficultyLevel: $difficultyLevel, hintsEnabled: $hintsEnabled")
    }

    /**
     * Parcelable constructor for recreating the object from a Parcel.
     * Reads each field from the Parcel in the same order it was written.
     */
    constructor(parcel: Parcel) : this(
        soundEnabled = parcel.readByte() != 0.toByte(),
        notificationsEnabled = parcel.readByte() != 0.toByte(),
        difficultyLevel = parcel.readString() ?: "Normal",
        hintsEnabled = parcel.readByte() != 0.toByte()
    ) {
        Log.d(TAG, "PlayerSettings created from Parcel with soundEnabled: $soundEnabled, notificationsEnabled: $notificationsEnabled, difficultyLevel: $difficultyLevel, hintsEnabled: $hintsEnabled")
    }

    /**
     * Writes the PlayerSettings object into a Parcel so it can be passed between activities or fragments.
     * Each field is written in the same order that the constructor reads them back.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Log.d(TAG, "Writing PlayerSettings to Parcel with soundEnabled: $soundEnabled, notificationsEnabled: $notificationsEnabled, difficultyLevel: $difficultyLevel, hintsEnabled: $hintsEnabled")
        parcel.writeByte(if (soundEnabled) 1 else 0)
        parcel.writeByte(if (notificationsEnabled) 1 else 0)
        parcel.writeString(difficultyLevel)
        parcel.writeByte(if (hintsEnabled) 1 else 0)
    }

    /**
     * Describes the kinds of special objects contained in this Parcelable instance.
     */
    override fun describeContents(): Int = 0

    /**
     * Companion object needed for Parcelable implementation.
     * It contains methods to create the object from a Parcel and to create an array of such objects.
     */
    companion object CREATOR : Parcelable.Creator<PlayerSettings> {
        private val TAG1 = "PlayerSettings"
        override fun createFromParcel(parcel: Parcel): PlayerSettings {
            Log.d(TAG1, "Creating PlayerSettings from Parcel")
            return PlayerSettings(parcel)
        }

        override fun newArray(size: Int): Array<PlayerSettings?> {
            Log.d(TAG1, "Creating array of PlayerSettings with size: $size")
            return arrayOfNulls(size)
        }
    }
}
