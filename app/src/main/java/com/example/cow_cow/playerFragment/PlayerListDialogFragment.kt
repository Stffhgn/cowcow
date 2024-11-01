package com.example.cow_cow.playerFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.DialogPlayerListBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PlayerListViewModel
import com.example.cow_cow.viewModels.PlayerListViewModelFactory
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager

class PlayerListDialogFragment : DialogFragment() {

    private var _binding: DialogPlayerListBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing player data
    private lateinit var playerListViewModel: PlayerListViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private var calledObjectType: String = ""

    // Listener to communicate with GameActivity
    private var listener: OnPlayerAndObjectSelectedListener? = null

    companion object {
        const val TAG = "PlayerListDialogFragment"
        private const val OBJECT_TYPE_KEY = "object_type"

        fun newInstance(objectType: String): PlayerListDialogFragment {
            val fragment = PlayerListDialogFragment()
            val args = Bundle()
            args.putString(OBJECT_TYPE_KEY, objectType)
            fragment.arguments = args
            return fragment
        }
    }

    // Attach listener when fragment is attached to an activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlayerAndObjectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlayerAndObjectSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPlayerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the object type from arguments
        calledObjectType = arguments?.getString(OBJECT_TYPE_KEY) ?: ""

        // Set the header text with the called object type
        binding.headerTextView.text = getString(R.string.who_called_it_header, calledObjectType)

        // Initialize ViewModel using PlayerListViewModelFactory
        val playerRepository = PlayerRepository(requireActivity().applicationContext)
        val playerManager = PlayerManager(playerRepository)
        val factory = PlayerListViewModelFactory(requireActivity().application, playerRepository)
        playerListViewModel = ViewModelProvider(requireActivity(), factory).get(PlayerListViewModel::class.java)

        // Initialize PlayerAdapter with all required parameters
        playerAdapter = PlayerAdapter(
            isWhoCalledItContext = true,
            onPlayerClick = { player -> onPlayerClick(player) },
            scoreManager = ScoreManager(playerManager)
        )

        // Set up RecyclerView
        setupRecyclerView()

        // Observe players data from ViewModel
        observePlayers()
    }

    override fun onStart() {
        super.onStart()
        // Adjust the size of the dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Refresh player data when the dialog is shown
        refreshPlayers()
    }

    // Set up RecyclerView and Adapter
    private fun setupRecyclerView() {
        binding.playerRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        Log.d(TAG, "RecyclerView set up with PlayerAdapter.")
    }

    // Observe LiveData from ViewModel for player list updates
    private fun observePlayers() {
        playerListViewModel.players.observe(viewLifecycleOwner) { players ->
            playerAdapter.submitList(players)
            Log.d(TAG, "Player list updated. Number of players: ${players.size}")
        }
    }

    // Function called when a player is clicked
    private fun onPlayerClick(player: Player) {
        Log.d(TAG, "Player clicked: ${player.name}, ID: ${player.id}")

        // Notify GameActivity of the selected player and object
        listener?.onPlayerSelected(player.id, calledObjectType)

        // Close the dialog after selecting a player
        dismiss()
    }

    // Refresh players whenever the dialog is opened
    private fun refreshPlayers() {
        playerListViewModel.loadPlayers() // Trigger the ViewModel to reload players from the repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: View destroyed and binding cleared.")
    }
}
