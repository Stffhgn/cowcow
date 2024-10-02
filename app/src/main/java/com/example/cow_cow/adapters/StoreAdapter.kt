package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemStoreBinding
import com.example.cow_cow.models.StoreItem

class StoreAdapter(
    private val onItemClick: (StoreItem) -> Unit // Lambda for handling item click
) : ListAdapter<StoreItem, StoreAdapter.StoreViewHolder>(DiffCallback()) {

    inner class StoreViewHolder(private val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storeItem: StoreItem) {
            binding.itemNameTextView.text = storeItem.name
            binding.itemPriceTextView.text = "Price: ${storeItem.price}"

            // Handle item clicks
            binding.root.setOnClickListener {
                onItemClick(storeItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoreBinding.inflate(inflater, parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val storeItem = getItem(position)
        holder.bind(storeItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<StoreItem>() {
        override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
            return oldItem == newItem
        }
    }
}
