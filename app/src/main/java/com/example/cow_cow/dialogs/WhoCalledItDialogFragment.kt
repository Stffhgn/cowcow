package com.example.cow_cow.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.gameFragments.AddPlayerDialogFragment
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.models.Player

class WhoCalledItDialogFragment : DialogFragment() {
    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    private var listener: OnPlayerAndObjectSelectedListener? = null
    private var players: MutableList<Player> = mutableListOf()
    private var calledObject: String = ""
    private lateinit var scoreManager: ScoreManager

    companion object {
        const val TAG = "WhoCalledItDialogFragment"

        fun newInstance(
            players: List<Player>,
            listener: OnPlayerAndObjectSelectedListener,
            scoreManager: ScoreManager,
            calledObject: String // Add calledObject parameter here
        ): WhoCalledItDialogFragment {
            val fragment = WhoCalledItDialogFragment()
            fragment.listener = listener
            fragment.scoreManager = scoreManager
            val args = Bundle()
            args.putParcelableArrayList("players", ArrayList(players))
            args.putString("calledObject", calledObject) // Pass calledObject to arguments
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        players = arguments?.getParcelableArrayList("players") ?: mutableListOf()
        calledObject = arguments?.getString("calledObject") ?: "" // Retrieve calledObject

        setupRecyclerView()
        binding.addPlayerButton.setOnClickListener { AddPlayerDialogFragment }
        binding.falseCallButton.setOnClickListener { dismiss() }
    }

    private fun setupRecyclerView() {
        if (players.isNotEmpty()) {
            val adapter = PlayerAdapter(
                isWhoCalledItContext = true,
                onPlayerClick = { player -> onPlayerSelected(player) },
                scoreManager = scoreManager
            )
            binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.playerRecyclerView.adapter = adapter
            adapter.submitList(players)
        } else {
            binding.noPlayersMessage.visibility = View.VISIBLE
        }
    }

    private fun onPlayerSelected(player: Player) {
        Log.d(TAG, "Player selected: ${player.name}")
        listener?.onPlayerSelected(player.id, calledObject) // Use calledObject when notifying listener
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "WhoCalledItDialogFragment view destroyed, binding set to null")
    }
}
