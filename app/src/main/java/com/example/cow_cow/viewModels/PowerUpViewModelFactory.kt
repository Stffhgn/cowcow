package com.example.cow_cow.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PowerUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PowerUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PowerUpViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}