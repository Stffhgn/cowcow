package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cow_cow.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        Log.d("GameFragment", "GameFragment created and view binding set")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GameFragment", "GameFragment view created")
        // Placeholder logic for loading CowCowFragment or any future fragments
        if (savedInstanceState == null) {
            Log.d("GameFragment", "Placeholder for loading CowCowFragment or other logic")
        }
    }

    // Placeholder for future game object processing
    fun processGameObject(objectType: String) {
        Log.d("GameFragment", "Received object: $objectType (Placeholder function)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("GameFragment", "GameFragment view destroyed, binding set to null")
    }
}
