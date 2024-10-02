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
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.models.AgeGroup
import com.example.cow_cow.models.LocationType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.models.Season
import com.example.cow_cow.models.TimeOfDay
import com.example.cow_cow.models.WeatherCondition
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

        scavengerHuntViewModel = ViewModelProvider(this).get(ScavengerHuntViewModel::class.java)

        // Initialize the adapter with an empty list and the click handler
        scavengerHuntAdapter = ScavengerHuntAdapter(emptyList()) { item ->
            scavengerHuntViewModel.markItemAsFound(item, getCurrentPlayer())
            Toast.makeText(requireContext(), "${item.name} found!", Toast.LENGTH_SHORT).show()
        }

        // Set up RecyclerView with the scavenger hunt adapter
        binding.scavengerHuntRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scavengerHuntAdapter
        }

        // Observe the scavenger hunt items and update the RecyclerView when they change
        scavengerHuntViewModel.scavengerHuntItems.observe(viewLifecycleOwner) { items ->
            scavengerHuntAdapter.updateData(items)
        }

        // Show status messages from the ViewModel
        scavengerHuntViewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Load scavenger hunt items when the fragment is created
        scavengerHuntViewModel.loadScavengerHuntItems()

        // Add a scavenger hunt item when the button is clicked
        binding.addScavengerHuntButton.setOnClickListener {
            val newItem = ScavengerHuntItem(
                name = "Find a blue car",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.TEENS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.FALL
            )
            scavengerHuntViewModel.addScavengerHuntItem(newItem, requireContext()) // Pass the context here
            Toast.makeText(requireContext(), "Scavenger hunt item added!", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * This method returns the current player. Replace this logic with the actual way
     * you retrieve the active player in your app.
     */
    private fun getCurrentPlayer(): Player {
        return Player(1, "Player 1") // Replace this with the real player retrieval logic
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
