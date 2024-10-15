package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.databinding.FragmentGameStatsBinding
import com.example.cow_cow.models.Game
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.viewModels.GameStatsViewModel
import com.example.cow_cow.viewModels.GameStatsViewModelFactory

class GameStatsFragment : Fragment() {

    private var _binding: FragmentGameStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameStatsViewModel: GameStatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameStatsFragment", "onCreateView called, inflating layout")
        _binding = FragmentGameStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GameStatsFragment", "onViewCreated called")

        // Initialize GameRepository and GameStatsViewModelFactory
        val gameRepository = GameRepository(requireContext())
        val factory = GameStatsViewModelFactory(requireActivity().application, gameRepository)

        // Initialize GameStatsViewModel using ViewModelProvider and factory
        gameStatsViewModel = ViewModelProvider(this, factory).get(GameStatsViewModel::class.java)
        Log.d("GameStatsFragment", "GameStatsViewModel initialized")

        // Show loading indicator while data is being fetched
        binding.progressBar.visibility = View.VISIBLE
        Log.d("GameStatsFragment", "Loading indicator shown")

        // Observe LiveData to update UI with game data
        gameStatsViewModel.game.observe(viewLifecycleOwner) { game ->
            binding.progressBar.visibility = View.GONE // Hide loading indicator
            if (game != null) {
                bindGameData(game)
            } else {
                displayError("Game data not found")
            }
        }

        // Handle errors from ViewModel
        gameStatsViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("GameStatsFragment", "Error: $it")
            }
        }
    }

    private fun bindGameData(game: Game) {
        Log.d("GameStatsFragment", "Binding game data to UI")

        // Reset error view
        binding.errorTextView.visibility = View.GONE

        // Update the UI with game data
        binding.totalPlayersTextView.text = "Total Players: ${game.players.size}"
        binding.totalRoundsTextView.text = "Total Rounds: ${game.round}/${game.totalRounds}"
        binding.elapsedTimeTextView.text = "Elapsed Time: ${game.elapsedTime} ms"
        binding.gameModeTextView.text = "Game Mode: ${game.gameMode.name}"
        binding.gameStatusTextView.text = "Game Status: ${game.status.name}"
    }

    private fun displayError(message: String) {
        Log.d("GameStatsFragment", "Displaying error message: $message")
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("GameStatsFragment", "onDestroyView called, cleaning up binding")
        _binding = null
    }
}
