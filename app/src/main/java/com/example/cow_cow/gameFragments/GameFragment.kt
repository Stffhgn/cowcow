package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button click listeners
        setupButtons()
    }

    // Set up click listeners for the game buttons
    private fun setupButtons() {
        binding.apply {
            // When the Cow button is clicked, navigate to WhoCalledItFragment
            cowButton.setOnClickListener {
                navigateToWhoCalledIt("Cow")
            }

            // When the Church button is clicked, navigate to WhoCalledItFragment
            churchButton.setOnClickListener {
                navigateToWhoCalledIt("Church")
            }

            // When the Water Tower button is clicked, navigate to WhoCalledItFragment
            waterTowerButton.setOnClickListener {
                navigateToWhoCalledIt("Water Tower")
            }

            // When White Fence button is clicked, navigate to team selection fragment
            whiteFenceButton.setOnClickListener {
                findNavController().navigate(R.id.action_gameFragment_to_teamManagementFragment)
            }
        }
    }

    // Function to navigate to WhoCalledItFragment and pass the called object type
    private fun navigateToWhoCalledIt(objectType: String) {
        val action = GameFragmentDirections.actionGameFragmentToWhoCalledItFragment(objectType)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
