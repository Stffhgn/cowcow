package com.example.cow_cow.playerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cow_cow.enums.RewardType
import com.example.cow_cow.models.Achievement
import com.example.cow_cow.ui.theme.CowCowTheme
import com.example.cow_cow.viewModels.AchievementViewModel

class AchievementsFragment : Fragment() {

    // ViewModel to manage the data
    private val achievementViewModel: AchievementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CowCowTheme {
                    // Pass the observed achievements to the AchievementsScreen
                    AchievementsScreen(
                        achievementViewModel = achievementViewModel
                    )
                }
            }
        }
    }

    @Composable
    fun AchievementsScreen(achievementViewModel: AchievementViewModel) {
        val achievements by achievementViewModel.unlockedAchievements.observeAsState(emptyList())
        val isLoading by achievementViewModel.isLoading.observeAsState(false)
        val errorMessage by achievementViewModel.errorMessage.observeAsState("")

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    errorMessage.isNotEmpty() -> ErrorScreen(errorMessage)
                    else -> AchievementList(achievements)
                }
            }
        }
    }

    @Composable
    fun AchievementList(achievements: List<Achievement>) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { achievement ->
                AchievementItem(achievement)
            }
        }
    }

    @Composable
    fun AchievementItem(achievement: Achievement) {
        val isUnlockedText = if (achievement.isUnlocked) "Unlocked" else "Locked"
        val progressText = if (achievement.goal > 1) "${achievement.currentProgress}/${achievement.goal}" else ""
        val rewardText = when (achievement.rewardType) {
            RewardType.POINTS -> "${achievement.rewardValue} Points"
            RewardType.POWER_UP -> "Power-Up: ${achievement.rewardValue} secs"
            RewardType.BADGE -> "Badge"
            else -> "Unknown Reward"  // This ensures the 'when' expression is exhaustive
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (achievement.isSecret && !achievement.isUnlocked) "??? (Secret)" else achievement.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (progressText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Progress: $progressText",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Status: $isUnlockedText",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reward: $rewardText",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    @Composable
    fun ErrorScreen(message: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
