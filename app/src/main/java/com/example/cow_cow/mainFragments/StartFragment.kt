package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button navigation actions
        setupNavigationButtons()
    }

    private fun setupNavigationButtons() {
        binding.startGameButton.setOnClickListener {
            // Navigate to the game fragment or start a new activity
            findNavController().navigate(R.id.action_startFragment_to_gameFragment)
        }

        binding.settingsButton.setOnClickListener {
            // Navigate to the settings fragment
            findNavController().navigate(R.id.action_startFragment_to_appSettingsFragment)
        }

        binding.howToPlayButton.setOnClickListener {
            // Navigate to the How To Play fragment
            findNavController().navigate(R.id.action_startFragment_to_howToPlayFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
