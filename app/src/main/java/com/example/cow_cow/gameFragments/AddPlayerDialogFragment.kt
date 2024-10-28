package com.example.cow_cow.gameFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.cow_cow.databinding.DialogAddPlayerBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.utils.PlayerIDGenerator

class AddPlayerDialogFragment(private val playerController: PlayerController) : DialogFragment() {

    private var _binding: DialogAddPlayerBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "AddPlayerDialogFragment"

        // Create a new instance of AddPlayerDialogFragment
        fun newInstance(playerController: PlayerController): AddPlayerDialogFragment {
            return AddPlayerDialogFragment(playerController)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add Player Button Click Listener
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerNameInput.text.toString().trim()
            if (playerName.isNotEmpty()) {
                val playerId = PlayerIDGenerator.generatePlayerID() // Generate a unique ID for the new player
                val newPlayer = Player(id = playerId, name = playerName)

                // Use PlayerController to add the player
                val added = playerController.addPlayer(newPlayer)
                if (added) {
                    // Notify the user that the player was added successfully
                    Toast.makeText(context, "Player added successfully", Toast.LENGTH_SHORT).show()

                    // Dismiss the dialog after adding the player
                    dismiss()
                } else {
                    // Notify the user that the player could not be added (e.g., duplicate)
                    Toast.makeText(context, "Player with the same ID already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Player name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}
