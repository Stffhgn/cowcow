package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentOnboardingBinding
import com.example.cow_cow.viewModels.OnboardingViewModel

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe onboarding progress to show correct screen
        viewModel.currentStep.observe(viewLifecycleOwner, Observer { step ->
            when (step) {
                OnboardingViewModel.OnboardingStep.INTRODUCTION -> showIntroduction()
                OnboardingViewModel.OnboardingStep.SELECT_PROFILE -> showProfileSelection()
                OnboardingViewModel.OnboardingStep.GAME_SETUP -> showGameSetup()
                OnboardingViewModel.OnboardingStep.TUTORIAL -> showTutorial()
            }
        })

        // Start onboarding process
        viewModel.startOnboarding()

        // Handle 'Next' button clicks to progress the onboarding steps
        binding.nextButton.setOnClickListener {
            viewModel.moveToNextStep()
        }

        // Optionally, add a 'Skip' button to go directly to the main game screen
        binding.skipButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_mainGameFragment)
        }
    }

    private fun showIntroduction() {
        // Show introduction text and graphics
        binding.onboardingTextView.text = getString(R.string.onboarding_introduction)
        // Set relevant visuals or animations
    }

    private fun showProfileSelection() {
        // Show UI for profile selection or creation
        binding.onboardingTextView.text = getString(R.string.onboarding_profile_selection)
        // Set relevant visuals or animations
    }

    private fun showGameSetup() {
        // Show game setup options, such as rules or preferences
        binding.onboardingTextView.text = getString(R.string.onboarding_game_setup)
        // Set relevant visuals or animations
    }

    private fun showTutorial() {
        // Show tutorial or game overview
        binding.onboardingTextView.text = getString(R.string.onboarding_tutorial)
        // Set relevant visuals or animations
    }
}
