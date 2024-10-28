package com.example.cow_cow.managers

import android.util.Log
import android.os.Handler

class GameNewsManager {

    private val newsQueue = mutableListOf<String>()
    private var currentIndex = 0

    private val TAG = "GameNewsManager"


    /**
     * Initializes the game news with default messages.
     */
    fun initializeGameNews() {
        addNewsMessage("Dad is Awesome")
        addNewsMessage("Tanner is Amazing!")
        addNewsMessage("Solo thinks you stink")
        addNewsMessage("Zoey is a RockStar")
        Log.d(TAG, "Game news initialized with default messages.")
    }

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
     * Starts rotating the news using the provided handler.
     * @param handler The handler used to schedule news updates.
     */
    fun startRotatingNews(handler: Handler) {
        val newsRunnable = object : Runnable {
            override fun run() {
                getNextNewsMessage()
                handler.postDelayed(this, 5000) // Update news every 5 seconds
            }
        }
        handler.post(newsRunnable)
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
