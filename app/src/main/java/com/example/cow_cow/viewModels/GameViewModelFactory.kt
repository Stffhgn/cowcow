package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.TeamRepository

class GameViewModelFactory(
    private val application: Application,
    private val gameRepository: GameRepository,
    private val teamRepository: TeamRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(application, gameRepository, teamRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
