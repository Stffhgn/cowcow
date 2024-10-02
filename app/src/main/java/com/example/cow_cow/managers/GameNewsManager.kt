package com.example.cow_cow.managers

class GameNewsManager {

    private val newsQueue = mutableListOf<String>()
    private var currentIndex = 0

    // Add new message to the news feed
    fun addNewsMessage(message: String) {
        newsQueue.add(message)
    }

    // Get the next message in the queue
    fun getNextNewsMessage(): String {
        if (newsQueue.isEmpty()) {
            return "No updates"
        }
        currentIndex = (currentIndex + 1) % newsQueue.size
        return newsQueue[currentIndex]
    }

    // Optionally reset the news queue
    fun resetNews() {
        newsQueue.clear()
        currentIndex = 0
    }
}