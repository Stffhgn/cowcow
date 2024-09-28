package com.example.cow_cow.enums

data class SoundSettings(
    val volume: Float = 1.0f, // Volume range: 0.0 (mute) to 1.0 (max)
    val isMuted: Boolean = false
)