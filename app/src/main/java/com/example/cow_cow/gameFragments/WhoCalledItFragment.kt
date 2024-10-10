package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cow_cow.R
import com.example.cow_cow.adapters.PlayerAdapter
import com.example.cow_cow.databinding.FragmentWhoCalledItBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.activity.GameActivity

class WhoCalledItFragment : Fragment() {

    // View binding for the fragment layout
    private var _binding: FragmentWhoCalledItBinding? = null
    private val binding get() = _binding!!

    // List of players passed from the GameActivity
    private lateinit var players: List<Player>

    // Adapter for displaying players
    private lateinit var adapter: PlayerAdapter

    companion object {
        private const val PLAYER_LIST_KEY = "players"
        private const val OBJECT_TYPE_KEY = "object_type"

        // Create a new instance and pass the player list and object type as arguments
        fun newInstance(players: List<Player>, objectType: String): WhoCalledItFragment {
            val fragment = WhoCalledItFragment()
            val args = Bundle()
            args.putParcelableArrayList(PLAYER_LIST_KEY, ArrayList(players))
            args.putString(OBJECT_TYPE_KEY, objectType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("WhoCalledItFragment", "onCreateView called")
        _binding = FragmentWhoCalledItBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve player list and object type from arguments
        players = arguments?.getParcelableArrayList(PLAYER_LIST_KEY) ?: emptyList()
        val calledObject = arguments?.getString(OBJECT_TYPE_KEY) ?: ""

        // Log the number of players received
        Log.d("WhoCalledItFragment", "Number of players received: ${players.size}")

        // Log the object type received
        Log.d("WhoCalledItFragment", "Object type received: $calledObject")

        // Log each player's name
        players.forEach { player ->
            Log.d("WhoCalledItFragment", "Player received: ${player.name}")
        }

        // Setup UI elements
        setupHeaderText(calledObject)
        setupRecyclerView()
    }

    // Sets up the RecyclerView to display the list of players
    private fun setupRecyclerView() {
        // Initialize the adapter with isWhoCalledItContext set to true for this fragment
        adapter = PlayerAdapter(
            isWhoCalledItContext = true,
            onPlayerClick = { player -> onPlayerSelected(player) }
        )
        binding.playerRecyclerView.adapter = adapter
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (players.isNotEmpty()) {
            // Update adapter with player list
            adapter.submitList(players)
            Log.d("WhoCalledItFragment", "Displaying ${players.size} players.")
        } else {
            Log.d("WhoCalledItFragment", "No players found to display.")
            binding.noPlayersMessage.visibility = View.VISIBLE  // Show "No players found" message
        }
    }

    // Sets up the header text indicating which object was called
    private fun setupHeaderText(calledObject: String) {
        if (calledObject.isNotEmpty()) {
            binding.headerTextView.text = getString(R.string.who_called_it_header, calledObject)
            Log.d("WhoCalledItFragment", "Header text set to indicate: Who called the object $calledObject")
        } else {
            binding.headerTextView.text = getString(R.string.who_called_it_header_default)
            Log.d("WhoCalledItFragment", "Header text set to default.")
        }
    }

    // Handles player selection from the list
    private fun onPlayerSelected(player: Player) {
        // Log the beginning of player selection
        Log.d("WhoCalledItFragment", "onPlayerSelected() called")

        // Log which player was selected
        Log.d("WhoCalledItFragment", "Player selected: ${player.name}")

        // Determine which object was called (for example: "Cow", "Church", "Water Tower")
        val objectCalled = "Cow"  // Replace this with the actual object called, which can be passed or obtained based on your logic

        // Update player stats based on the object called
        player.addObjectCalled(objectCalled)

        // Log the updated state
        Log.d("WhoCalledItFragment", "Object called: $objectCalled for player ${player.name}. Current base points: ${player.basePoints}")

        // Pass the selected player back to GameActivity (if needed)
        (activity as? GameActivity)?.receiveSelectedPlayer(player)
        Log.d("WhoCalledItFragment", "Selected player passed back to GameActivity")

        // Set adapter context back to false
        adapter.setWhoCalledItContext(false)

        // Use post to ensure that the fragment transaction occurs after the view is ready
        binding.root.post {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CowCowFragment())
                .addToBackStack(null)
                .commit()
            Log.d("WhoCalledItFragment", "Replaced WhoCalledItFragment with CowCowFragment.")
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear binding when the view is destroyed to prevent memory leaks
        Log.d("WhoCalledItFragment", "WhoCalledItFragment view destroyed, binding set to null")
    }
}