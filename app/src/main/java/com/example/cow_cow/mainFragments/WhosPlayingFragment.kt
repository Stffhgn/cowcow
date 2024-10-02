package com.example.cow_cow.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhosPlayingBinding
import com.example.cow_cow.viewModels.PlayerViewModel

class WhosPlayingFragment : Fragment() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var adapter: PlayerAdapter
    private var _binding: FragmentWhosPlayingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        // Set up RecyclerView and Adapter
        adapter = PlayerAdapter()
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe changes to the player list
        viewModel.players.observe(viewLifecycleOwner) { players ->
            adapter.submitList(players.toList())
        }

        // Add Player Button Click Listener
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            viewModel.addPlayer(playerName)
            binding.playerNameInput.text.clear()
        }

        // Fetch the player data using playerId and observe changes
        viewModel.getPlayerById(playerId)?.observe(viewLifecycleOwner) { player ->
            player?.let {
                bindPlayerData(it)
            } ?: showError("Player not found")
        }

        // Observe for error messages and show a toast if any
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage() // Clear the error after displaying it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
