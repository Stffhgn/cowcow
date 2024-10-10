package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.PlayerRepository

class PlayerStatsViewModelFactory(
    private val application: Application,
    private val playerRepository: PlayerRepository,
    private val playerId: String  // Add playerId to load specific player data
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerStatsViewModel(application, playerRepository, playerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
