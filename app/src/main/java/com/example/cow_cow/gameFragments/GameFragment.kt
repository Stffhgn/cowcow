package com.example.cow_cow.gameFragments

import android.os.Bundle
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
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button click listeners
        binding.cowButton.setOnClickListener {
            // Logic for handling "Cow" button click
            // For example, navigate to WhoCalledItFragment to select the player
        }

        binding.churchButton.setOnClickListener {
            // Logic for handling "Church" button click
        }

        binding.waterTowerButton.setOnClickListener {
            // Logic for handling "Water Tower" button click
        }

        binding.whiteFenceButton.setOnClickListener {
            // Logic for handling "White Fence" button click (team selection)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}