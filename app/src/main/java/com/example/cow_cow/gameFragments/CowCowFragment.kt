package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.databinding.FragmentCowCowBinding

class CowCowFragment : Fragment() {

    private var _binding: FragmentCowCowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCowCowBinding.inflate(inflater, container, false)
        Log.d("CowCowFragment", "CowCowFragment created and view binding set")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CowCowFragment", "View created, setting up button listeners")
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.cowButton.setOnClickListener {
            Log.d("CowCowFragment", "Cow button clicked")
            sendObjectToActivity("Cow")
        }
        binding.churchButton.setOnClickListener {
            Log.d("CowCowFragment", "Church button clicked")
            sendObjectToActivity("Church")
        }
        binding.waterTowerButton.setOnClickListener {
            Log.d("CowCowFragment", "Water Tower button clicked")
            sendObjectToActivity("Water Tower")
        }
    }

    private fun sendObjectToActivity(objectType: String) {
        Log.d("CowCowFragment", "Sending object to activity: $objectType")

        val activity = requireActivity() as? GameActivity
        if (activity != null) {
            activity.receiveObject(objectType)  // Send the object to GameActivity
            Log.d("CowCowFragment", "Object sent to GameActivity successfully: $objectType")

            // Navigate to WhoCalledItFragment instead of popping back stack immediately
            navigateToWhoCalledItFragment()
        } else {
            Log.e("CowCowFragment", "Activity is not GameActivity, failed to send object")
        }
    }

    private fun navigateToWhoCalledItFragment() {
        try {
            Log.d("CowCowFragment", "Navigating to WhoCalledItFragment")
            val navController = findNavController()
            navController.navigate(R.id.action_cowCowFragment_to_whoCalledItFragment)
        } catch (e: Exception) {
            Log.e("CowCowFragment", "Error navigating to WhoCalledItFragment: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("CowCowFragment", "CowCowFragment view destroyed, binding set to null")
    }
}
