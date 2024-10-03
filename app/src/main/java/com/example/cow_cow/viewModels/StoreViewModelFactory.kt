package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.StoreRepository
import com.example.cow_cow.repositories.PlayerRepository

/**
 * Factory class for creating an instance of StoreViewModel with Application and Repository.
 */
class StoreViewModelFactory(
    private val application: Application,
    private val storeRepository: StoreRepository,
    private val playerRepository: PlayerRepository  // Add PlayerRepository here
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            return StoreViewModel(application, storeRepository, playerRepository) as T  // Pass both repositories
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
