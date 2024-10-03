// C:\Users\steff\AndroidStudioProjects\Cow_Cow\app\src\main\java\com\example\cow_cow\PlayerFragment\PlayerListFragment.kt

package com.example.cow_cow.PlayerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentPlayerListBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerViewModel

class PlayerListFragment : Fragment() {

    private var _binding: FragmentPlayerListBinding? = null
    private val binding get() = _binding!!

    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerAdapter: PlayerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerListBinding.bind(view)

        // Initialize ViewModel
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)

        // Initialize PlayerAdapter with click listener
        playerAdapter = PlayerAdapter { player ->
            // Handle player click - navigate to PlayerStatsFragment with Safe Args
            val action = PlayerListFragmentDirections
                .actionPlayerListFragmentToPlayerStatsFragment(player.name, player.id)

            // Navigate to the player stats fragment
            findNavController().navigate(action)
        }

        // Setup RecyclerView
        binding.playerRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Observe LiveData for players
        playerViewModel.players.observe(viewLifecycleOwner) { players ->
            // Update PlayerAdapter with new data
            playerAdapter.submitList(players)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}