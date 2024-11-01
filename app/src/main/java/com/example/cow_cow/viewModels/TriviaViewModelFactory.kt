package com.example.cow_cow.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.Player

class TriviaViewModelFactory(
    private val triviaManager: TriviaManager,
    private val player: Player,
    private val scoreManager: ScoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TriviaViewModel::class.java)) {
            return TriviaViewModel(triviaManager, player, scoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}