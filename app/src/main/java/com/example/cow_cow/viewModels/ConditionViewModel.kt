package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.enums.ConditionType
import com.example.cow_cow.models.Condition

class ConditionViewModel : ViewModel() {

    private val _conditions = MutableLiveData<List<Condition>>()
    val conditions: LiveData<List<Condition>> get() = _conditions

    fun loadConditions(achievementId: Int) {
        // Load conditions based on an achievement or game mode, etc.
        val loadedConditions = listOf(
            Condition(ConditionType.SCORE_THRESHOLD, 1000, "Reach a score of 1000 points"),
            Condition(ConditionType.WIN_STREAK, 3, "Win 3 consecutive games")
        )
        _conditions.value = loadedConditions
    }
}