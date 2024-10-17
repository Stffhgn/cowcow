package com.example.cow_cow.gameFragments

import android.content.Context
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
import com.example.cow_cow.interfaces.OnObjectSelectedListener

class CowCowFragment : Fragment() {

    // View binding for the fragment
    private var _binding: FragmentCowCowBinding? = null
    private val binding get() = _binding!!
    private var listener: OnObjectSelectedListener? = null

    // Attach listener when fragment is attached to an activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnObjectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnObjectSelectedListener")
        }
    }

    // Inflate the layout for this fragment and set up binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCowCowBinding.inflate(inflater, container, false)
        Log.d("CowCowFragment", "CowCowFragment created and view binding set")
        return binding.root
    }

    // Set up button listeners when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CowCowFragment", "CowCowFragment view created, setting up button listeners")
        setupButtonListeners()
    }

    // Set up click listeners for the buttons
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
        binding.whiteFenceButton.setOnClickListener {
            Log.d("CowCowFragment", "White Fence button clicked")
            openTeamManagementDialog()
        }
    }

    // CowCowFragment: Send the selected object type to the activity
    private fun sendObjectToActivity(objectType: String) {
        Log.d("CowCowFragment", "Sending object to activity: $objectType")

        // Send the object to the listener (GameActivity)
        listener?.onObjectSelected(objectType)
            ?: Log.e("CowCowFragment", "OnObjectSelectedListener is not attached")
    }

    // Function to open Team Management Dialog
    private fun openTeamManagementDialog() {
        // Get the GameActivity instance and call the method to open the dialog
        (activity as? GameActivity)?.openTeamManagementDialog()
            ?: Log.e("CowCowFragment", "GameActivity not found!")
    }

    // Clean up view binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("CowCowFragment", "CowCowFragment view destroyed, binding set to null")
    }
    // Companion object to handle instance creation of CowCowFragment
    companion object {
        @JvmStatic
        fun newInstance(): CowCowFragment {
            return CowCowFragment()
        }
    }
}