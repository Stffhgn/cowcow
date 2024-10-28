package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.models.Player

/**
 * Factory class for creating an instance of ScavengerHuntViewModel with Application.
 */
class ScavengerHuntViewModelFactory(
    private val application: Application,
    private val players: List<Player>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScavengerHuntViewModel::class.java)) {
            return ScavengerHuntViewModel(application, players) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

