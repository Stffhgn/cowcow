package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.repositories.PlayerRepository

class PlayerViewModelFactory(
    private val application: Application,
    private val playerRepository: PlayerRepository,
    private val playerManager: PlayerManager,
    private val penaltyManager: PenaltyManager,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(application, playerRepository, playerManager, penaltyManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
