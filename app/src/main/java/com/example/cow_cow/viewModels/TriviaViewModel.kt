package com.example.cow_cow.viewModels

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
    private val scoreManager: ScoreManager,

) : ViewModel() {

    private val _currentQuestion = MutableLiveData<TriviaQuestion?>()
    val currentQuestion: LiveData<TriviaQuestion?> get() = _currentQuestion

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect: LiveData<Boolean> get() = _isAnswerCorrect

    private val _triviaCompleted = MutableLiveData<Boolean>()
    val triviaCompleted: LiveData<Boolean> get() = _triviaCompleted

    init {
        _score.value = scoreManager.calculatePlayerScore(player)
        loadTriviaQuestions()
    }

    /**
     * Load trivia questions from the manager.
     */
    private fun loadTriviaQuestions() {
        triviaManager.loadQuestions(shuffle = true)  // Shuffle questions if needed
        loadNextQuestion()
    }

    /**
     * Load the next trivia question.
     * If there are no more questions, mark the trivia game as completed.
     */
    fun loadNextQuestion() {
        val nextQuestion = triviaManager.getNextQuestion()
        if (nextQuestion == null) {
            _triviaCompleted.value = true
        } else {
            _currentQuestion.value = nextQuestion
        }
    }

    /**
     * Submit the player's answer and validate it.
     * Update score and whether the answer was correct.
     */
    fun submitAnswer(selectedAnswer: String) {
        val isCorrect = triviaManager.validateAnswer(player, selectedAnswer)
        _isAnswerCorrect.value = isCorrect
        _score.value = scoreManager.calculatePlayerScore(player)

        // Load next question after validation
        loadNextQuestion()
    }

    /**
     * Reset the trivia game and reload questions.
     */
    fun resetTriviaGame() {
        triviaManager.resetTriviaGame()
        _triviaCompleted.value = false
        loadTriviaQuestions()
    }
}
