package com.example.cow_cow.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.repositories.GameRepository

class GameViewModelFactory(
    private val application: Application,
    private val repository: GameRepository  // Pass the repository
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(application, repository) as T  // Pass both Application and Repository
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
