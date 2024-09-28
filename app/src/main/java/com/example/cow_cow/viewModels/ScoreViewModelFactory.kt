package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.PlayerRepository

/**
 * Factory class for creating an instance of ScoreViewModel with Application and Repository.
 */
class ScoreViewModelFactory(
    private val application: Application,
    private val playerRepository: PlayerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(application, playerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
