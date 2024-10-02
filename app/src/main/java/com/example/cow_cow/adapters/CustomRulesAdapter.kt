package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemCustomRuleBinding
import com.example.cow_cow.models.CustomRule

class CustomRulesAdapter(
    private var customRules: List<CustomRule>,
    private val onEditRule: (CustomRule) -> Unit // Lambda for handling rule editing
) : RecyclerView.Adapter<CustomRulesAdapter.CustomRuleViewHolder>() {

    inner class CustomRuleViewHolder(private val binding: ItemCustomRuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customRule: CustomRule) {
            binding.ruleTitleTextView.text = customRule.name
            binding.ruleDescriptionTextView.text = customRule.description

            // Handle click for editing the rule
            binding.root.setOnClickListener {
                onEditRule(customRule)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRuleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCustomRuleBinding.inflate(inflater, parent, false)
        return CustomRuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomRuleViewHolder, position: Int) {
        holder.bind(customRules[position])
    }

    override fun getItemCount(): Int = customRules.size

    fun updateData(newRules: List<CustomRule>) {
        customRules = newRules
        notifyDataSetChanged()
    }
}
