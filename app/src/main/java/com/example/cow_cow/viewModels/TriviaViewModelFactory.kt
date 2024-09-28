package com.example.cow_cow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.Player

class TriviaViewModelFactory(
    private val triviaManager: TriviaManager,
    private val player: Player
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TriviaViewModel::class.java)) {
            return TriviaViewModel(triviaManager, player) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}