package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.PlayerRepository

/**
 * Factory class to instantiate PlayerListViewModel with the required parameters.
 */
class PlayerListViewModelFactory(
    private val application: Application,
    private val playerRepository: PlayerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerListViewModel(application, playerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}