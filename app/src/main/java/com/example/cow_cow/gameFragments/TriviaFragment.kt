package com.example.cow_cow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentTriviaBinding
import com.example.cow_cow.models.TriviaQuestion
import com.example.cow_cow.models.Player
import com.example.cow_cow.managers.TriviaManager
import com.example.cow_cow.viewModels.TriviaViewModel
import com.example.cow_cow.viewModels.TriviaViewModelFactory

class TriviaFragment : Fragment() {

    private var _binding: FragmentTriviaBinding? = null
    private val binding get() = _binding!!

    private lateinit var triviaViewModel: TriviaViewModel
    private lateinit var player: Player
    private lateinit var triviaManager: TriviaManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTriviaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TriviaManager and Player (You need to pass appropriate instances)
        triviaManager = // TODO: Obtain TriviaManager instance, likely from a shared source
            player = // TODO: Get Player instance, from arguments or shared ViewModel

        // Create ViewModelFactory using TriviaManager and Player
        val factory = TriviaViewModelFactory(triviaManager, player)
        triviaViewModel = ViewModelProvider(this, factory).get(TriviaViewModel::class.java)

        // Observe the trivia question and update the UI
        triviaViewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            question?.let { displayQuestion(it) }
        }

        // Observe the score and update the player's score in the UI
        triviaViewModel.score.observe(viewLifecycleOwner) { score ->
            binding.playerScore.text = getString(R.string.score_text, score)
        }

        // Observe whether the answer is correct and provide feedback
        triviaViewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            isCorrect?.let { handleAnswerFeedback(it) }
        }

        // Submit answer button listener
        binding.submitAnswerButton.setOnClickListener {
            val selectedAnswer = getSelectedAnswer() // Function to retrieve the selected answer
            if (selectedAnswer != null) {
                triviaViewModel.submitAnswer(selectedAnswer)
            } else {
                Toast.makeText(requireContext(), "Please select an answer.", Toast.LENGTH_SHORT).show()
            }
        }

        // Load the first trivia question
        triviaViewModel.loadNextQuestion()
    }

    private fun displayQuestion(question: TriviaQuestion) {
        binding.triviaQuestionTextView.text = question.questionText
        // Populate the answer options in the UI (e.g., radio buttons or checkboxes)
        // Assuming there are 4 possible answers, this is an example:
        binding.answerOption1.text = question.possibleAnswers[0]
        binding.answerOption2.text = question.possibleAnswers[1]
        binding.answerOption3.text = question.possibleAnswers[2]
        binding.answerOption4.text = question.possibleAnswers[3]
    }

    private fun getSelectedAnswer(): String? {
        // Retrieve the selected answer from the UI
        return when {
            binding.answerOption1.isChecked -> binding.answerOption1.text.toString()
            binding.answerOption2.isChecked -> binding.answerOption2.text.toString()
            binding.answerOption3.isChecked -> binding.answerOption3.text.toString()
            binding.answerOption4.isChecked -> binding.answerOption4.text.toString()
            else -> null
        }
    }

    private fun handleAnswerFeedback(isCorrect: Boolean) {
        val feedbackMessage = if (isCorrect) {
            "Correct! Well done!"
        } else {
            "Incorrect. Better luck next time!"
        }
        Toast.makeText(requireContext(), feedbackMessage, Toast.LENGTH_SHORT).show()

        // Optionally, load the next question after a delay
        triviaViewModel.loadNextQuestion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
