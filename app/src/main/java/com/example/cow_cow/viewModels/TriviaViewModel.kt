package com.example.cow_cow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.TriviaQuestion

class TriviaViewModel(private val triviaManager: TriviaManager, private val player: Player) : ViewModel() {

    private val _currentQuestion = MutableLiveData<TriviaQuestion?>()
    val currentQuestion: LiveData<TriviaQuestion?> get() = _currentQuestion

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect: LiveData<Boolean> get() = _isAnswerCorrect

    init {
        _score.value = player.calculateTotalPoints()
    }

    /**
     * Load the next trivia question.
     */
    fun loadNextQuestion() {
        _currentQuestion.value = triviaManager.getNextQuestion()
    }

    /**
     * Submit answer and validate it.
     */
    fun submitAnswer(selectedAnswer: String) {
        val isCorrect = triviaManager.validateAnswer(player, selectedAnswer)
        _isAnswerCorrect.value = isCorrect
        _score.value = player.calculateTotalPoints()
    }
}
