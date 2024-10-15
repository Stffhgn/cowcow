package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.DialogWhosPlayingBinding
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.models.Player
import com.example.cow_cow.playerFragment.PlayerStatsDialogFragment
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.utils.PlayerIDGenerator
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory

class WhosPlayingDialogFragment : DialogFragment() {

    private lateinit var viewModel: PlayerListViewModel
    private lateinit var adapter: PlayerAdapter
    private var _binding: DialogWhosPlayingBinding? = null
    private val binding get() = _binding!!

    // Logging tag for debugging
    private val TAG = "WhosPlayingDialogFragment"

    // Interface to communicate with GameActivity
    private var listener: OnPlayerSelectedListener? = null

    companion object {
        const val TAG_DIALOG = "WhosPlayingDialogFragment"

        fun newInstance(): WhosPlayingDialogFragment {
            return WhosPlayingDialogFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlayerSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlayerSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: WhosPlayingDialogFragment view is being created.")
        _binding = DialogWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the repository
        val repository = PlayerRepository(requireContext())

        // Initialize ViewModel with PlayerListViewModelFactory
        val factory = PlayerListViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(PlayerListViewModel::class.java)

        // Log for ViewModel initialization
        Log.d(TAG, "onViewCreated: PlayerListViewModel initialized.")

        // Set up RecyclerView and Adapter
        setupRecyclerView()

        // Observe changes to the player list
        observePlayers()

        // Add Player Button Click Listener
        binding.addPlayerButton.setOnClickListener {
            addPlayer()
        }

        // Okay Button Click Listener to close the dialog
        binding.okayButton.setOnClickListener {
            dismiss()
        }

        // Log ViewModel observer setup
        Log.d(TAG, "ViewModel observer set up for players LiveData.")
    }

    override fun onStart() {
        super.onStart()
        // Adjust the size of the dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Ensure fresh data when the dialog is shown
        refreshPlayers()
    }

    // Set up RecyclerView and Adapter
    private fun setupRecyclerView() {
        adapter = PlayerAdapter { player -> onPlayerClick(player) } // Pass the onPlayerClick lambda
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Log RecyclerView setup
        Log.d(TAG, "RecyclerView set up with PlayerAdapter.")
    }

    // Observe LiveData from ViewModel for player list updates
    private fun observePlayers() {
        viewModel.players.observe(viewLifecycleOwner, Observer { players ->
            updateUI(players)
        })
    }

    // Update the UI based on the player list
    private fun updateUI(players: List<Player>) {
        if (players.isNotEmpty()) {
            binding.playerListTitle.text = "Who Is Playing"  // Set the title back to "Who Is Playing"
            binding.playerRecyclerView.isVisible = true // Show RecyclerView
            binding.playerListTitle.isVisible = true // Show title

            adapter.submitList(players.toList()) {
                // Callback after list submission to force RecyclerView update
                adapter.notifyDataSetChanged()
            }

            Log.d(TAG, "Player list updated. Number of players: ${players.size}")
        } else {
            Log.d(TAG, "No players found to display.")
            binding.playerListTitle.text = "No Players Available"  // Update the title when empty
            binding.playerRecyclerView.isVisible = false // Hide RecyclerView
        }
    }

    // Function called when a player is clicked
    private fun onPlayerClick(player: Player) {
        Log.d("WhosPlayingDialogFragment", "Player clicked: ${player.name}, ID: ${player.id}")

        // Create an instance of PlayerStatsDialogFragment
        val playerStatsDialog = PlayerStatsDialogFragment.newInstance(player.id)

        // Show the dialog
        playerStatsDialog.show(parentFragmentManager, PlayerStatsDialogFragment.TAG)
    }

    // Add a new player
    private fun addPlayer() {
        val playerName = binding.playerNameInput.text.toString().trim()
        if (playerName.isNotEmpty()) {
            Log.d(TAG, "Adding a new player with name: $playerName")
            val playerId = PlayerIDGenerator.generatePlayerID()
            val newPlayer = Player(id = playerId, name = playerName)

            // Add the player using the ViewModel
            viewModel.addPlayer(newPlayer)

            // Clear the input field
            binding.playerNameInput.text.clear()
        } else {
            Log.d(TAG, "Player name input is empty, not adding player.")
        }
    }

    // Refresh players whenever the dialog is opened
    private fun refreshPlayers() {
        viewModel.loadPlayers() // Trigger the ViewModel to reload players from the repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}