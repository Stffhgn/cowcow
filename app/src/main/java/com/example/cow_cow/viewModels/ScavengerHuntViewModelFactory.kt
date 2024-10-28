package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.ScavengerHuntRepository

/**
 * Factory class for creating an instance of ScavengerHuntViewModel with Application, players, and repository.
 */
class ScavengerHuntViewModelFactory(
    private val application: Application,
    private val players: List<Player>,
    private val repository: ScavengerHuntRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScavengerHuntViewModel::class.java)) {
            return ScavengerHuntViewModel(application, players, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
