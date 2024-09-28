package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemScavengerHuntBinding
import com.example.cow_cow.models.ScavengerHuntItem

class ScavengerHuntAdapter(
    private var scavengerHuntItems: List<ScavengerHuntItem>,
    private val onItemFound: (ScavengerHuntItem) -> Unit // Callback for when an item is found
) : RecyclerView.Adapter<ScavengerHuntAdapter.ScavengerHuntViewHolder>() {

    inner class ScavengerHuntViewHolder(private val binding: ItemScavengerHuntBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scavengerHuntItem: ScavengerHuntItem) {
            binding.itemNameTextView.text = scavengerHuntItem.name
            binding.itemDifficultyTextView.text = "Difficulty: ${scavengerHuntItem.difficultyLevel}"
            binding.itemLocationTextView.text = "Location: ${scavengerHuntItem.locationType}"

            // Handle the click when an item is found
            binding.root.setOnClickListener {
                onItemFound(scavengerHuntItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScavengerHuntViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScavengerHuntBinding.inflate(inflater, parent, false)
        return ScavengerHuntViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScavengerHuntViewHolder, position: Int) {
        holder.bind(scavengerHuntItems[position])
    }

    override fun getItemCount(): Int = scavengerHuntItems.size

    fun updateData(newItems: List<ScavengerHuntItem>) {
        scavengerHuntItems = newItems
        notifyDataSetChanged()
    }
}