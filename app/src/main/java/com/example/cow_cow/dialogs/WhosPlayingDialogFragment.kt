package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.databinding.DialogWhosPlayingBinding
import com.example.cow_cow.interfaces.OnPlayerSelectedListener
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.repositories.PlayerRepository
import java.util.UUID

class WhosPlayingDialogFragment : DialogFragment() {

    private var _binding: DialogWhosPlayingBinding? = null
    private val binding get() = _binding!!

    private var onPlayerSelectedListener: OnPlayerSelectedListener? = null
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var playerManager: PlayerManager
    private lateinit var playerController: PlayerController
    private lateinit var scoreManager: ScoreManager
    private lateinit var penaltyManager: PenaltyManager
    private var players: MutableList<Player> = mutableListOf()

    companion object {
        const val TAG_DIALOG = "WhosPlayingDialogFragment"

        fun newInstance(): WhosPlayingDialogFragment {
            return WhosPlayingDialogFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onPlayerSelectedListener = context as? OnPlayerSelectedListener
            ?: throw ClassCastException("$context must implement OnPlayerSelectedListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWhosPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repositories, manager, and controller
        val playerRepository = PlayerRepository(requireContext())
        playerManager = PlayerManager(playerRepository)
        playerController = PlayerController(players, penaltyManager) // Ensure playerController is initialized
        scoreManager = ScoreManager(playerManager)

        players = playerManager.getAllPlayers().toMutableList()
        setupRecyclerView()

        // Set up add player button functionality
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                addPlayer(playerName)
                binding.playerNameInput.text.clear()
            }
        }

        // Set up dialog close button functionality
        binding.okayButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        playerAdapter = PlayerAdapter(
            isWhoCalledItContext = false,
            onPlayerClick = { player -> onPlayerSelectedListener?.onPlayerSelected(player.id) },
            scoreManager = scoreManager
        )

        // Submit players list and configure the RecyclerView layout
        playerAdapter.submitList(players)
        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        // Show message if no players are available
        if (players.isEmpty()) {
            //binding.noPlayersMessage.visibility = View.VISIBLE
        } else {
            //binding.noPlayersMessage.visibility = View.GONE
        }
    }

    private fun addPlayer(playerName: String) {
        val newPlayer = Player(id = UUID.randomUUID().toString(), name = playerName)
        val isAdded = playerController.addPlayer(newPlayer)

        if (isAdded) {
            players.add(newPlayer)
            playerAdapter.submitList(players.toList())
            playerAdapter.notifyDataSetChanged()
            Log.d(TAG_DIALOG, "Player added: $playerName")
        } else {
            Log.d(TAG_DIALOG, "Player with name $playerName already exists or duplicate ID.")
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ) // Makes the dialog width match the parent width
    }

    override fun onDetach() {
        super.onDetach()
        onPlayerSelectedListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
