package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PenaltyViewModelFactory(
    private val application: Application // Pass Application as a parameter to the factory
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PenaltyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PenaltyViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
