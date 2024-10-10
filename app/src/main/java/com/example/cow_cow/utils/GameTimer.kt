package com.example.cow_cow.utils

import android.util.Log
import java.util.Timer
import kotlin.concurrent.schedule

class GameTimer(
    private var durationMillis: Long,
    private val onTimerFinished: () -> Unit,
    private val onTimerTick: ((Long) -> Unit)? = null // Optional tick callback for real-time updates
) {
    private var timer: Timer? = null
    private var remainingMillis: Long = durationMillis
    private var isPaused: Boolean = false
    private var speedMultiplier: Double = 1.0
    private val TAG = "GameTimer"

    fun start() {
        if (isPaused) {
            isPaused = false
            startTimer(remainingMillis)
        } else {
            remainingMillis = durationMillis
            startTimer(durationMillis)
        }
        Log.d(TAG, "Timer started with duration: $durationMillis milliseconds")
    }

    fun stop() {
        timer?.cancel()
        remainingMillis = 0
        isPaused = false
        Log.d(TAG, "Timer stopped.")
    }

    fun pause() {
        timer?.cancel()
        isPaused = true
        Log.d(TAG, "Timer paused at remaining time: $remainingMillis milliseconds")
    }

    fun resume() {
        if (isPaused) {
            isPaused = false
            startTimer(remainingMillis)
            Log.d(TAG, "Timer resumed with remaining time: $remainingMillis milliseconds")
        }
    }

    private fun startTimer(initialTime: Long) {
        remainingMillis = initialTime
        timer = Timer().apply {
            schedule(0, (1000 / speedMultiplier).toLong()) {
                if (remainingMillis <= 0) {
                    onTimerFinished()
                    cancel()
                } else {
                    remainingMillis -= (1000 / speedMultiplier).toLong()
                    onTimerTick?.invoke(remainingMillis)
                    Log.d(TAG, "Timer tick: $remainingMillis milliseconds remaining")
                }
            }
        }
    }

    fun applyPenalty(penaltyMillis: Long) {
        remainingMillis -= penaltyMillis
        Log.d(TAG, "Penalty applied: $penaltyMillis milliseconds deducted. Remaining time: $remainingMillis milliseconds")
        if (remainingMillis <= 0) {
            stop()
            onTimerFinished()
        }
    }

    fun addTime(extraMillis: Long) {
        remainingMillis += extraMillis
        Log.d(TAG, "Extra time added: $extraMillis milliseconds. New remaining time: $remainingMillis milliseconds")
    }

    fun applySpeedMultiplier(multiplier: Double) {
        speedMultiplier = multiplier
        timer?.cancel() // Cancel the existing timer
        startTimer(remainingMillis) // Restart the timer with the new speed
        Log.d(TAG, "Speed multiplier applied: $multiplier. Timer restarted with new speed.")
    }
}