package com.example.cow_cow.utils

import java.util.UUID

object PlayerIDGenerator {

    /**
     * Generate a unique player ID that can be used across devices and synced to a central server.
     * The ID format includes UUID + teamName or userLogin (if available) for future extensibility.
     */
    fun generatePlayerID(teamName: String? = null, userLogin: String? = null): String {
        val uuid = UUID.randomUUID().toString()

        // Optional: Add teamName or userLogin for more context
        return when {
            teamName != null -> "$teamName-$uuid"
            userLogin != null -> "$userLogin-$uuid"
            else -> uuid
        }
    }
}
