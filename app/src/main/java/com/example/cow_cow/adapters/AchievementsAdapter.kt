package com.example.cow_cow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.databinding.ItemAchievementBinding
import com.example.cow_cow.models.Achievement

class AchievementsAdapter(
    private var achievements: MutableList<Achievement>
) : RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: Achievement) {
            binding.achievementNameTextView.text = achievement.name
            binding.achievementDescriptionTextView.text = achievement.description
            binding.achievementProgressTextView.text = "Progress: ${achievement.currentProgress}/${achievement.goal}"

            // Optionally show whether the achievement is unlocked
            if (achievement.isUnlocked) {
                binding.root.alpha = 1.0f
            } else {
                binding.root.alpha = 0.5f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAchievementBinding.inflate(inflater, parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    // Update adapter data
    fun updateAchievements(newAchievements: MutableList<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}
