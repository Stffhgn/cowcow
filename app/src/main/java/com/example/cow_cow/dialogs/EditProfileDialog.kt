package com.example.cow_cow.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.DialogEditProfileBinding
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.PlayerViewModel

class EditProfileDialog : DialogFragment() {

    private lateinit var binding: DialogEditProfileBinding
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
        binding = DialogEditProfileBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        // Load current player data
        playerViewModel.selectedPlayer.observe(this, { player ->
            player?.let {
                binding.playerNameEditText.setText(it.name)
                // Set current avatar (placeholder logic)
                binding.avatarImageView.setImageResource(R.drawable.ic_launcher_round)
            }
        })

        // Save button logic
        binding.saveButton.setOnClickListener {
            val updatedName = binding.playerNameEditText.text.toString()
            // Update player data in ViewModel
            playerViewModel.selectedPlayer.value?.let { player ->
                player.name = updatedName
                playerViewModel.updatePlayer(player)
            }
            dismiss()
        }

        return dialog
    }
}
