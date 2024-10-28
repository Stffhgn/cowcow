package com.example.cow_cow.gameFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cow_cow.R
import com.example.cow_cow.activity.GameActivity
import com.example.cow_cow.adapters.GamesAdapter
import com.example.cow_cow.controllers.RainbowCarController
import com.example.cow_cow.controllers.ScavengerHuntController
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.uiStuff.FlexboxContainer

class TeamGamesFragmentDialog : DialogFragment() {

    companion object {
        const val TAG_DIALOG = "TeamGamesFragmentDialog"

        fun newInstance(): TeamGamesFragmentDialog {
            return TeamGamesFragmentDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_team_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for the games list
        val gamesRecyclerView = view.findViewById<RecyclerView>(R.id.gamesRecyclerView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Define the list of games
        val gamesList = listOf("Trivia", "Scavenger Hunt", "Rainbow Car")

        // Set the adapter for the games list
        val gamesAdapter = GamesAdapter(gamesList) { selectedGame ->
            onGameSelected(selectedGame)
        }
        gamesRecyclerView.adapter = gamesAdapter
    }

    // Handle game selection
    private fun onGameSelected(game: String) {
        val gameActivity = activity as? GameActivity
        if (gameActivity == null) {
            Log.e(TAG_DIALOG, "GameActivity not found!")
            Toast.makeText(context, "GameActivity not found", Toast.LENGTH_SHORT).show()
            return
        }

        when (game) {
            "Trivia" -> {
                replaceFragment(TriviaFragment())
            }
            "Scavenger Hunt" -> {
                gameActivity.loadScavengerHuntButtons()
                    ?: Log.e(TAG_DIALOG, "Failed to load Scavenger Hunt UI.")
            }
            "Rainbow Car" -> {
                gameActivity.loadRainbowCarButton()
                    ?: Log.e(TAG_DIALOG, "Failed to load Rainbow Car UI.")
            }
            else -> {
                Toast.makeText(context, "Unknown game selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Dismiss the dialog after game selection
        dismiss()
    }

    // Replace the current fragment with the selected game fragment
    private fun replaceFragment(fragment: Fragment) {
        (activity as? GameActivity)?.let {
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment) // Replace the current fragment
                .addToBackStack(null) // Optionally add to back stack for navigation
                .commit()
        } ?: Log.e(TAG_DIALOG, "GameActivity not found!")
    }
}
