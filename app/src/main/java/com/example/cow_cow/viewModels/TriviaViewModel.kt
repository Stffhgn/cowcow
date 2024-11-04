package com.example.cow_cow.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.managers.ScoreManager
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.TriviaQuestion

class TriviaViewModel(
    private val triviaManager: TriviaManager,
    private val player: Player,
    private val scoreManager: ScoreManager
) : ViewModel() {

    private val _currentQuestion = MutableLiveData<TriviaQuestion?>()
    val currentQuestion: LiveData<TriviaQuestion?> get() = _currentQuestion

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect: LiveData<Boolean> get() = _isAnswerCorrect

    private val _triviaCompleted = MutableLiveData<Boolean>()
    val triviaCompleted: LiveData<Boolean> get() = _triviaCompleted

    private val _travelingSalesmanLog = MutableLiveData<String>()
    val travelingSalesmanLog: LiveData<String> get() = _travelingSalesmanLog

    // New property to track the previously loaded question
    private var _previousQuestion: TriviaQuestion? = null

    init {
        _score.value = scoreManager.calculatePlayerScore(player)
        loadNextQuestion()
        _travelingSalesmanLog.value = triviaManager.getTravelingSalesmenLog()
    }

    /**
     * Load the next trivia question.
     * If there are no more questions, mark the trivia game as completed.
     */
    fun loadNextQuestion() {
        triviaManager.loadNextQuestion() // Load the next question
        val loadedQuestion = triviaManager.getCurrentQuestion()
        _currentQuestion.value = loadedQuestion
        _travelingSalesmanLog.value = triviaManager.getTravelingSalesmenLog() // Update log

        if (loadedQuestion == null) {
            _triviaCompleted.value = true // Mark the game as completed if no question is loaded
        } else {
            // Ensure that the loaded question is not the same as the previously completed question
            if (_previousQuestion != null && loadedQuestion.questionText == _previousQuestion?.questionText) {
                _currentQuestion.value = null
                _triviaCompleted.value = true
                Log.d("TriviaViewModel", "No new unique question available. Trivia game marked as completed.")
            }
            _previousQuestion = loadedQuestion // Update the previous question reference
        }
    }

    /**
     * Reset the trivia game and reload questions.
     */
    fun resetTriviaGame() {
        triviaManager.resetTriviaGame()
        _triviaCompleted.value = false
        _currentQuestion.value = null
        _score.value = scoreManager.calculatePlayerScore(player)
        _travelingSalesmanLog.value = triviaManager.getTravelingSalesmenLog() // Reset log
        _previousQuestion = null // Reset the previous question
        loadNextQuestion()
    }
}
