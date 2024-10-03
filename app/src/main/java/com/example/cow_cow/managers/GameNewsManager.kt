package com.example.cow_cow.managers

import android.util.Log

class GameNewsManager {

    private val newsQueue = mutableListOf<String>()
    private var currentIndex = 0

    private val TAG = "GameNewsManager"

    /**
     * Adds a new message to the news feed.
     * @param message The message to add to the news queue.
     */
    fun addNewsMessage(message: String) {
        newsQueue.add(message)
        Log.d(TAG, "Message added to newsQueue: $message")
    }

    /**
     * Retrieves the next message in the news queue.
     * @return The next message in the queue or "No updates" if the queue is empty.
     */
    fun getNextNewsMessage(): String {
        if (newsQueue.isEmpty()) {
            Log.d(TAG, "No updates in the newsQueue.")
            return "No updates"
        }
        currentIndex = (currentIndex + 1) % newsQueue.size
        val message = newsQueue[currentIndex]
        Log.d(TAG, "Retrieved next news message: $message")
        return message
    }

    /**
     * Resets the news queue, clearing all messages and resetting the index.
     */
    fun resetNews() {
        newsQueue.clear()
        currentIndex = 0
        Log.d(TAG, "News queue has been reset.")
    }
}
