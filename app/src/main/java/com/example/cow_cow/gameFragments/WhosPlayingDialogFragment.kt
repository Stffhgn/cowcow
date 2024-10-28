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
    private lateinit var players: MutableList<Player>

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

        val playerRepository = PlayerRepository(requireContext())
        playerManager = PlayerManager(playerRepository)
        players = playerManager.getAllPlayers().toMutableList()

        setupRecyclerView()

        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                addPlayer(playerName)
                binding.playerNameInput.text.clear()
            }
        }

        binding.okayButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        val scoreManager = ScoreManager
        playerAdapter = PlayerAdapter(
            isWhoCalledItContext = false,
            onPlayerClick = { player ->
                onPlayerSelectedListener?.onPlayerSelected(player.id)
            },
            scoreManager = scoreManager
        )

        playerAdapter.submitList(players)
        binding.playerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }
    }

    /**
     * Adds a player to the list using PlayerController and updates the adapter.
     * @param playerName The name of the player to add.
     */
    private fun addPlayer(playerName: String) {
        // Create a new Player instance
        val newPlayer = Player(id = UUID.randomUUID().toString(), name = playerName)

        // Add player using PlayerController and check success
        val isAdded = playerController.addPlayer(newPlayer)
        if (isAdded) {
            players.add(newPlayer) // Update local list for display
            playerAdapter.submitList(players.toList()) // Refresh adapter with updated list
            playerAdapter.notifyDataSetChanged()
            Log.d(TAG_DIALOG, "Player added: $playerName")
        } else {
            Log.d(TAG_DIALOG, "Player with name $playerName already exists or duplicate ID.")
        }
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
