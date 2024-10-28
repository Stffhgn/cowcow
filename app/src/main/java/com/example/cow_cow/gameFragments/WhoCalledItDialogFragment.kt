package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.interfaces.OnPlayerAndObjectSelectedListener

class WhoCalledItDialogFragment : DialogFragment() {

    // View binding for the fragment layout
    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    // Listener to communicate with the controller
    private var listener: OnPlayerAndObjectSelectedListener? = null

    // List of players passed from the GameActivity
    private var players: MutableList<Player> = mutableListOf()
    private var calledObject: String = ""

    companion object {
        const val TAG = "WhoCalledItDialogFragment" // This should be TAG for accessibility

        fun newInstance(players: List<Player>, objectType: String): WhoCalledItDialogFragment {
            val fragment = WhoCalledItDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList("players", ArrayList(players))
            args.putString("object_type", objectType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameActivity && context.cowController is OnPlayerAndObjectSelectedListener) {
            listener = context.cowController
        } else {
            throw RuntimeException("CowCowController must implement OnPlayerAndObjectSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView called")
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve player list and object type from arguments
        players = (arguments?.getParcelableArrayList<Player>("players") as? MutableList<Player>) ?: mutableListOf()
        calledObject = arguments?.getString("object_type") ?: ""

        // Log the number of players received
        Log.d(TAG, "Number of players received: ${players.size}")

        // Log the object type received
        Log.d(TAG, "Object type received: $calledObject")

        // Setup UI elements
        setupHeaderText(calledObject)
        setupRecyclerView()

        // Set up the Add Player Button
        binding.addPlayerButton.setOnClickListener {
            openAddPlayerDialog()
        }

        // Set up the False Call Button
        binding.falseCallButton.setOnClickListener {
            dismiss() // Simply dismiss the dialog
        }
    }

    // Sets up the header text indicating which object was called
    private fun setupHeaderText(calledObject: String) {
        if (calledObject.isNotEmpty()) {
            binding.headerTextView.text = getString(R.string.who_called_it_header, calledObject)
            Log.d(TAG, "Header text set to indicate: Who called the object $calledObject")
        } else {
            binding.headerTextView.text = getString(R.string.who_called_it_header_default)
            Log.d(TAG, "Header text set to default.")
        }
    }

    // Sets up the RecyclerView to display the list of players
    private fun setupRecyclerView() {
        if (players.isNotEmpty()) {
            // Access the ScoreManager directly if it's a singleton object
            val scoreManager = com.example.cow_cow.managers.ScoreManager

            // Initialize the adapter
            val adapter = PlayerAdapter(
                isWhoCalledItContext = true,
                onPlayerClick = { player -> onPlayerSelected(player) },
                scoreManager = scoreManager // Pass the ScoreManager instance here
            )
            binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.playerRecyclerView.adapter = adapter

            // Update adapter with player list
            adapter.submitList(players)
            Log.d(TAG, "Displaying ${players.size} players.")
        } else {
            Log.d(TAG, "No players found to display.")
            binding.noPlayersMessage.visibility = View.VISIBLE  // Show "No players found" message
        }
    }

    // Handles player selection from the list
    private fun onPlayerSelected(player: Player) {
        Log.d(TAG, "Player selected: ${player.name}")

        // Notify the listener about the selected player and the object type
        listener?.onPlayerSelected(player.id, calledObject)

        // Dismiss the dialog after selection
        dismiss()
    }

    // Opens a dialog to add a new player
    private fun openAddPlayerDialog() {
        val playerController = PlayerController(players)
        val addPlayerDialog = AddPlayerDialogFragment.newInstance(playerController)
        addPlayerDialog.show(parentFragmentManager, AddPlayerDialogFragment.TAG)
    }

    override fun onStart() {
        super.onStart()
        // Adjust the size of the dialog
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear binding when the view is destroyed to prevent memory leaks
        Log.d(TAG, "WhoCalledItDialogFragment view destroyed, binding set to null")
    }
}
