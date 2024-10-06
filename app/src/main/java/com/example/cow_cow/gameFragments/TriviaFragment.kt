package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.databinding.FragmentTriviaBinding
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.repositories.TriviaRepository
import com.example.cow_cow.viewmodel.TriviaViewModel
import com.example.cow_cow.viewmodel.TriviaViewModelFactory

class TriviaFragment : Fragment() {

    private var _binding: FragmentTriviaBinding? = null
    private val binding get() = _binding!!

    private lateinit var triviaViewModel: TriviaViewModel
    private lateinit var triviaManager: TriviaManager
    private lateinit var player: Player

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTriviaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the TriviaRepository and TriviaManager
        val triviaRepository = TriviaRepository()
        triviaManager = TriviaManager(triviaRepository)

        // Assume player data is passed in via Fragment arguments
        val playerId = arguments?.getInt("playerId") ?: 1 // Example of how player ID could be passed
        val playerName = arguments?.getString("playerName") ?: "John Doe" // Example of how name could be passed
        player = Player(playerId, playerName)

        // Create ViewModel using the factory
        val factory = TriviaViewModelFactory(triviaManager, player)
        triviaViewModel = ViewModelProvider(this, factory).get(TriviaViewModel::class.java)

        // Observe ViewModel data
        triviaViewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            question?.let { displayQuestion(it) }
        }

        triviaViewModel.score.observe(viewLifecycleOwner) { score ->
            binding.playerScore.text = getString(R.string.score_text, score)
        }

        triviaViewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            isCorrect?.let { handleAnswerFeedback(it) }
        }

        // Load the first trivia question
        triviaViewModel.loadNextQuestion()

        // Submit answer button listener
        binding.submitAnswerButton.setOnClickListener {
            val selectedAnswer = getSelectedAnswer() // Get the selected answer
            if (selectedAnswer != null) {
                triviaViewModel.submitAnswer(selectedAnswer)
            } else {
                Toast.makeText(requireContext(), "Please select an answer.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Display trivia question and answers
    private fun displayQuestion(question: TriviaQuestion) {
        binding.triviaQuestionTextView.text = question.questionText
        binding.answerOption1.text = question.possibleAnswers[0]
        binding.answerOption2.text = question.possibleAnswers[1]
        binding.answerOption3.text = question.possibleAnswers[2]
        binding.answerOption4.text = question.possibleAnswers[3]
    }

    // Retrieve selected answer
    private fun getSelectedAnswer(): String? {
        return when {
            binding.answerOption1.isChecked -> binding.answerOption1.text.toString()
            binding.answerOption2.isChecked -> binding.answerOption2.text.toString()
            binding.answerOption3.isChecked -> binding.answerOption3.text.toString()
            binding.answerOption4.isChecked -> binding.answerOption4.text.toString()
            else -> null
        }
    }

    // Handle feedback for correct/incorrect answers
    private fun handleAnswerFeedback(isCorrect: Boolean) {
        val feedbackMessage = if (isCorrect) {
            "Correct! Well done!"
        } else {
            "Incorrect. Better luck next time!"
        }
        Toast.makeText(requireContext(), feedbackMessage, Toast.LENGTH_SHORT).show()

        // Load the next question
        triviaViewModel.loadNextQuestion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
