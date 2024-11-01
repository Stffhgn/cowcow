package com.example.cow_cow.mainFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhosPlayingBinding
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory

class WhosPlayingFragment : Fragment() {

    private lateinit var viewModel: PlayerListViewModel
    private lateinit var adapter: PlayerAdapter
    private var _binding: FragmentWhosPlayingBinding? = null
    private val binding get() = _binding!!

    private val TAG = "WhosPlayingFragment"
    private var listener: OnPlayerSelectedListener? = null
    private lateinit var scoreManager: ScoreManager // Initialize ScoreManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlayerSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlayerSelectedListener")
        }
        Log.d(TAG, "WhosPlayingFragment attached with listener.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = PlayerRepository(requireContext())
        val factory = PlayerListViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, factory).get(PlayerListViewModel::class.java)

        // Initialize ScoreManager
        scoreManager = ScoreManager(playerManager = PlayerManager(repository))

        setupRecyclerView()
        setupViewModelObservers()
        setupButtonListeners()

        Log.d(TAG, "ViewModel and UI components initialized.")
    }

    private fun setupRecyclerView() {
        adapter = PlayerAdapter(
            isWhoCalledItContext = true,
            onPlayerClick = { player -> onPlayerClick(player) },
            scoreManager = scoreManager // Pass the instance of ScoreManager
        )
        binding.playerRecyclerView.apply {
            adapter = this@WhosPlayingFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        Log.d(TAG, "RecyclerView and PlayerAdapter set up.")
    }

    private fun setupViewModelObservers() {
        viewModel.players.observe(viewLifecycleOwner) { players ->
            if (players.isNotEmpty()) {
                binding.playerListTitle.text = getString(R.string.players)
                binding.playerRecyclerView.isVisible = true
                binding.startGameButton.isEnabled = true
                adapter.submitList(players)
                Log.d(TAG, "Players list updated: ${players.size} players displayed.")
            } else {
                binding.playerListTitle.text = getString(R.string.no_players_available)
                binding.playerRecyclerView.isVisible = false
                binding.startGameButton.isEnabled = false
                Log.d(TAG, "No players available.")
            }
        }
    }

    private fun setupButtonListeners() {
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                val playerId = PlayerIDGenerator.generatePlayerID()
                val newPlayer = Player(id = playerId, name = playerName)
                viewModel.addPlayer(newPlayer)
                adapter.notifyDataSetChanged()
                binding.playerNameInput.text.clear()
                Log.d(TAG, "Player added with name: $playerName, ID: $playerId")
            } else {
                Log.d(TAG, "Player name input is empty. No player added.")
            }
        }

        binding.startGameButton.setOnClickListener {
            Log.d(TAG, "Start Game button clicked.")
            navigateToGameOrAddPlayer()
        }

        binding.backButton.setOnClickListener {
            Log.d(TAG, "Back button clicked.")
            findNavController().navigateUp()
        }
    }

    private fun onPlayerClick(player: Player) {
        Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")
        listener?.onPlayerSelected(player.id)
    }

    private fun navigateToGameOrAddPlayer() {
        viewModel.players.value?.let { players ->
            if (players.isNotEmpty()) {
                val action = WhosPlayingFragmentDirections.actionWhosPlayingFragmentToCowCowFragment()
                findNavController().navigate(action)
                Log.d(TAG, "Navigated to CowCowFragment with ${players.size} players.")
            } else {
                Log.d(TAG, "No players found. Unable to start the game.")
                // Optional: show a Toast or dialog here
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlayers()
        Log.d(TAG, "Players reloaded in onResume to reflect any changes.")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "View binding cleared in onDestroyView.")
    }
}
