package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cow_cow.databinding.FragmentHowToPlayBinding

class HowToPlayFragment : Fragment() {

    private var _binding: FragmentHowToPlayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHowToPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up dynamic content for "How To Play"
        setupInstructions()
    }

    private fun setupInstructions() {
        binding.howToPlayTitle.text = getString(R.string.how_to_play_title)

        // The game's instructions, can be dynamically set or updated in the future
        binding.howToPlayText.text = """
            Welcome to CowCow! 
            
            Here’s how you play:
            - The goal is to spot certain objects while driving: Cows, Churches, Water Towers, and more.
            - Press the corresponding buttons on the game screen when you see these objects.
            - Earn points for correct calls and challenge your friends and family for the highest score!
            - You can customize game rules, join teams, or play solo.
            - Beware: incorrect calls might have penalties, so make sure you're paying attention!
            
            Enjoy the ride and happy spotting!
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
