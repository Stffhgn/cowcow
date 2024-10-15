package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.GameRepository

class GameStatsViewModelFactory(
    private val application: Application,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameStatsViewModel(application, gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}