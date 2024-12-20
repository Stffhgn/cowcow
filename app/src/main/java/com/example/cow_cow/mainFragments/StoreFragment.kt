package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.StoreAdapter
import com.example.cow_cow.databinding.FragmentStoreBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PurchaseResult
import com.example.cow_cow.viewModels.StoreViewModel

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    // ViewModel for the Store
    private val storeViewModel: StoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView to display store items
        setupStoreRecyclerView()

        // Observe store items from the ViewModel and update the UI accordingly
        storeViewModel.storeItems.observe(viewLifecycleOwner) { items ->
            (binding.storeRecyclerView.adapter as StoreAdapter).submitList(items)
        }

        // Observe any error or purchase results
        storeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            // Show error message if any
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        storeViewModel.purchaseResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PurchaseResult.Success -> {
                    // Handle successful purchase
                    Toast.makeText(context, "${result.item.name} purchased!", Toast.LENGTH_SHORT).show()
                }
                is PurchaseResult.Failure -> {
                    // Handle failed purchase
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    /**
     * Initialize RecyclerView for displaying store items.
     */
    private fun setupStoreRecyclerView() {
        binding.storeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = StoreAdapter { storeItem ->
                // Handle store item purchase action
                storeViewModel.purchaseItem(storeItem)
            }
        }
    }
}
