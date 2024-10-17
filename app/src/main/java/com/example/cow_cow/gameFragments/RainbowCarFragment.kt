package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.databinding.FragmentRainbowCarBinding
import com.example.cow_cow.managers.TeamManager

class RainbowCarFragment : Fragment() {

    private var _binding: FragmentRainbowCarBinding? = null
    private val binding get() = _binding!!

    // List of rainbow colors (in order)
    private val rainbowColors = listOf(
        R.color.red,
        R.color.orange,
        R.color.yellow,
        R.color.green,
        R.color.blue,
        R.color.indigo,
        R.color.violet
    )

    // Keep track of the current color index
    private var currentColorIndex = 0

    // Tag for logging
    private val TAG = "RainbowCarFragment"

    companion object {
        // Static method to create a new instance of the fragment
        fun newInstance(): RainbowCarFragment {
            return RainbowCarFragment().apply {
                arguments = Bundle().apply {
                    // You can add any parameters here if needed in the future
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRainbowCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Log the team members at the start of the Rainbow Car game
        logTeamPlayers()

        // Start showing the first color
        showColor()
    }

    // Function to log the team members at the start of the Rainbow Car game
    private fun logTeamPlayers() {
        val team = TeamManager.getTeam()  // Retrieve the current team from TeamManager
        val teamPlayers = team.members

        Log.d(TAG, "Total Players: ${teamPlayers.size}")
        teamPlayers.forEach { player ->
            Log.d(TAG, "Player ${player.name} is on the team")
        }
    }

    // Function to show the current color and update the button
    private fun showColor() {
        // Get the current color resource
        val currentColorRes = rainbowColors[currentColorIndex]

        // Set the button background color to the current color
        binding.foundCarButton.setBackgroundColor(ContextCompat.getColor(requireContext(), currentColorRes))

        // Set the button text to prompt the user to find a car of the current color
        binding.foundCarButton.text = getString(R.string.find_car_in_color, getColorName(currentColorRes))

        // Set onClickListener to handle button click
        binding.foundCarButton.setOnClickListener {
            // Advance to the next color or finish the game
            if (currentColorIndex < rainbowColors.size - 1) {
                currentColorIndex++
                showColor() // Show the next color
            } else {
                // All colors found, notify the activity
                handleGameCompletion()
            }
        }
    }

    // Function to get the name of the color as a string
    private fun getColorName(colorRes: Int): String {
        return when (colorRes) {
            R.color.red -> "Red"
            R.color.orange -> "Orange"
            R.color.yellow -> "Yellow"
            R.color.green -> "Green"
            R.color.blue -> "Blue"
            R.color.indigo -> "Indigo"
            R.color.violet -> "Violet"
            else -> "Unknown Color"
        }
    }

    // Function to handle game completion
    private fun handleGameCompletion() {
        Log.d(TAG, "Rainbow Car game completed")

        // Notify GameActivity about the game completion
        (activity as? GameActivity)?.onRainbowCarGameCompleted()

        // Replace RainbowCarFragment with CowCowFragment
        replaceWithCowCowFragment()
    }

    private fun replaceWithCowCowFragment() {
        // Create a new instance of CowCowFragment
        val cowCowFragment = CowCowFragment.newInstance() // Ensure you have a newInstance() method or similar in your fragment to create a new instance

        // Begin the fragment transaction
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, cowCowFragment) // Replace RainbowCarFragment with CowCowFragment
            .addToBackStack(null) // Add the transaction to the back stack if you want to be able to navigate back
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
