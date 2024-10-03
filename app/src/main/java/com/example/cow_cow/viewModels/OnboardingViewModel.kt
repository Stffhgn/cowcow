package com.example.cow_cow.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    // Define the onboarding steps
    enum class OnboardingStep {
        INTRODUCTION,
        SELECT_PROFILE,
        GAME_SETUP,
        TUTORIAL
    }

    // LiveData to track the current step in the onboarding process
    private val _currentStep = MutableLiveData<OnboardingStep>()
    val currentStep: LiveData<OnboardingStep> get() = _currentStep

    // Start onboarding by setting the initial step
    fun startOnboarding() {
        _currentStep.value = OnboardingStep.INTRODUCTION
    }

    // Move to the next step
    fun moveToNextStep() {
        when (_currentStep.value) {
            OnboardingStep.INTRODUCTION -> _currentStep.value = OnboardingStep.SELECT_PROFILE
            OnboardingStep.SELECT_PROFILE -> _currentStep.value = OnboardingStep.GAME_SETUP
            OnboardingStep.GAME_SETUP -> _currentStep.value = OnboardingStep.TUTORIAL
            OnboardingStep.TUTORIAL -> completeOnboarding()
            else -> {
                // Handle any unexpected or undefined onboarding step
                Log.e("Onboarding", "Unexpected step: ${_currentStep.value}. Resetting to INTRODUCTION.")
                _currentStep.value = OnboardingStep.INTRODUCTION
            }
        }
    }

    // Mark onboarding as complete
    private fun completeOnboarding() {
        // Trigger event to navigate to the main game screen
    }
}
