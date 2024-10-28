package com.example.cow_cow.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.cow_cow.models.Player

// DiffUtil Callback to handle efficient updates
class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Player ID should be unique
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        // Compare all fields to check if content is the same
        return oldItem == newItem
    }
}