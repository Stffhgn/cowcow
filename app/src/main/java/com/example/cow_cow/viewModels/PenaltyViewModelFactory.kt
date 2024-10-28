package com.example.cow_cow.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PenaltyViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PenaltyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PenaltyViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
