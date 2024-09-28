package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.ScavengerHuntAdapter
import com.example.cow_cow.databinding.FragmentScavengerHuntBinding
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.viewModels.ScavengerHuntViewModel

class ScavengerHuntFragment : Fragment() {

    private var _binding: FragmentScavengerHuntBinding? = null
    private val binding get() = _binding!!

    private lateinit var scavengerHuntViewModel: ScavengerHuntViewModel
    private lateinit var scavengerHuntAdapter: ScavengerHuntAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScavengerHuntBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        scavengerHuntViewModel = ViewModelProvider(this).get(ScavengerHuntViewModel::class.java)

        // Set up RecyclerView
        scavengerHuntAdapter = ScavengerHuntAdapter()
        binding.scavengerHuntRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scavengerHuntAdapter
        }

        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE

        // Observe scavenger hunt items and update UI
        scavengerHuntViewModel.scavengerHuntItems.observe(viewLifecycleOwner) { items ->
            binding.progressBar.visibility = View.GONE
            if (items.isNotEmpty()) {
                scavengerHuntAdapter.submitList(items)
                binding.emptyStateTextView.visibility = View.GONE
            } else {
                // Show empty state message
                binding.emptyStateTextView.visibility = View.VISIBLE
            }
        }

        // Observe errors from ViewModel and show a toast if any error occurs
        scavengerHuntViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Load scavenger hunt items
        scavengerHuntViewModel.loadScavengerHuntItems(requireContext())

        // Add a scavenger hunt item when the button is clicked
        binding.addScavengerHuntButton.setOnClickListener {
            val newItem = ScavengerHuntItem(name = "Find a blue car", difficultyLevel = DifficultyLevel.EASY)
            scavengerHuntViewModel.addScavengerHuntItem(newItem, requireContext())
            Toast.makeText(requireContext(), "Scavenger hunt item added!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
