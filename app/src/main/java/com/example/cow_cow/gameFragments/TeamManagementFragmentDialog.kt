package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.cow_cow.R
import com.example.cow_cow.databinding.DialogTeamManagementBinding
import com.example.cow_cow.models.Player
import com.google.android.flexbox.FlexboxLayout

class TeamManagementFragmentDialog : DialogFragment() {

    private var _binding: DialogTeamManagementBinding? = null
    private val binding get() = _binding!!

    private var players: MutableList<Player> = mutableListOf()  // Use mutable list to allow changes

    companion object {
        const val TAG_DIALOG = "TeamManagementFragmentDialog"

        fun newInstance(players: List<Player>): TeamManagementFragmentDialog {
            val fragment = TeamManagementFragmentDialog()
            val args = Bundle().apply {
                putParcelableArrayList("players", ArrayList(players))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTeamManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the players list from arguments
        players = arguments?.getParcelableArrayList<Player>("players")?.toMutableList() ?: mutableListOf()

        // Log loaded players and their initial team status
        Log.d(TAG_DIALOG, "Loaded players:")
        players.forEach { player ->
            Log.d(TAG_DIALOG, "Player: \${player.name}, Is On Team: \${player.isOnTeam}")
        }

        // Set up the Flexbox and add players to the layout
        setupFlexboxLayout(players)

        // Setup Save and Reset buttons
        binding.saveTeamButton.setOnClickListener {
            Log.d(TAG_DIALOG, "Save button clicked. Team changes saved.")
            dismiss() // Save changes and close the dialog
        }

        binding.resetTeamButton.setOnClickListener {
            Log.d(TAG_DIALOG, "Reset button clicked. Resetting all players to not be on a team.")
            // Reset team logic - set all players as not on the team
            players.forEach {
                it.isOnTeam = false
                Log.d(TAG_DIALOG, "Player \${it.name} reset to not on team.")
            }
            setupFlexboxLayout(players)
        }
    }

    override fun onStart() {
        super.onStart()
        // Adjust the size of the dialog to be just under full screen
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupFlexboxLayout(players: List<Player>) {
        binding.playerFlexbox.removeAllViews() // Clear the existing views

        // Log the number of players being set up
        Log.d(TAG_DIALOG, "Setting up Flexbox layout with \${players.size} players.")

        // Iterate through each player and create a button for them
        for (player in players) {
            // Create a button for each player
            val playerButton = createPlayerButton(player)
            binding.playerFlexbox.addView(playerButton)

            // Log the player being added to the UI
            Log.d(TAG_DIALOG, "Added player button for: \${player.name}, Is On Team: \${player.isOnTeam}")
        }
    }

    // Function to create a button for each player
    private fun createPlayerButton(player: Player): View {
        val button = Button(requireContext()).apply {
            text = player.name
            // Set background color based on team status
            setBackgroundColor(
                if (player.isOnTeam) requireContext().getColor(R.color.teal_200)
                else requireContext().getColor(android.R.color.holo_red_light)
            )
            setOnClickListener {
                // Toggle player's team status
                togglePlayerTeamStatus(player)
                // Update button color after toggling
                setBackgroundColor(
                    if (player.isOnTeam) requireContext().getColor(R.color.teal_200)
                    else requireContext().getColor(android.R.color.holo_red_light)
                )
                dismiss() // Close dialog after selecting player
            }
        }
        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 8, 8, 8) // Add some margin to each button
        }
        button.layoutParams = params
        return button
    }

    private fun togglePlayerTeamStatus(player: Player) {
        // Toggle the team status of the player
        player.isOnTeam = !player.isOnTeam
        Log.d(TAG_DIALOG, "Player \${player.name} team status toggled to: \${player.isOnTeam}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
