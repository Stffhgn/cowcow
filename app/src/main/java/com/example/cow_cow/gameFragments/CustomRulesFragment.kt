package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.CustomRulesAdapter
import com.example.cow_cow.databinding.FragmentCustomRulesBinding
import com.example.cow_cow.models.CustomRule

class CustomRulesFragment : Fragment() {

    private var _binding: FragmentCustomRulesBinding? = null
    private val binding get() = _binding!!

    private lateinit var customRulesAdapter: CustomRulesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter
        customRulesAdapter = CustomRulesAdapter(emptyList()) { customRule ->
            // Handle rule editing here
        }

        // Setup RecyclerView
        binding.customRulesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.customRulesRecyclerView.adapter = customRulesAdapter

        // Load or add rules here
        // e.g., customRulesAdapter.updateData(listOf(CustomRule("Rule 1", "Description of rule 1")))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
