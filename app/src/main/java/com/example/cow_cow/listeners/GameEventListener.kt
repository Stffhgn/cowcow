package com.example.cow_cow.listeners

import com.example.cow_cow.enums.GameMode

interface GameEventListener {
    fun onGameStarted(gameMode: GameMode)
    fun onGameStopped()
    fun onGamePaused()
    fun onGameResumed()
    fun onGameTimeExpired()  // For time-based modes
}
