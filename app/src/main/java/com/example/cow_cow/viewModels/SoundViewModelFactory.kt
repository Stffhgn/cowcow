package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.SoundRepository

/**
 * Factory class for creating an instance of SoundViewModel with Application and Repository.
 */
class SoundViewModelFactory(
    private val application: Application,
    private val soundRepository: SoundRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SoundViewModel::class.java)) {
            return SoundViewModel(application, soundRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
