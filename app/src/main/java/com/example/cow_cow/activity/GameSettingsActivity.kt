package com.example.cow_cow.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cow_cow.R
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.viewmodel.GameViewModel
import com.example.cow_cow.viewmodel.GameViewModelFactory

class GameSettingsActivity : AppCompatActivity() {

    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        // Initialize the repository
        val gameRepository = GameRepository()

        // Initialize the ViewModel with the repository using ViewModelFactory
        val factory = GameViewModelFactory(application, gameRepository)
        gameViewModel = ViewModelProvider(this, factory).get(GameViewModel::class.java)

        // Reference switches from the layout
        val quietGameSwitch: Switch = findViewById(R.id.quietGameSwitch)
        val falseCallPenaltySwitch: Switch = findViewById(R.id.falseCallPenaltySwitch)
        val noRepeatRuleSwitch: Switch = findViewById(R.id.noRepeatRuleSwitch)

        // Save the settings when the button is clicked
        findViewById<Button>(R.id.saveSettingsButton).setOnClickListener {
            gameViewModel.quietGameEnabled = quietGameSwitch.isChecked
            gameViewModel.falseCallPenaltyEnabled = falseCallPenaltySwitch.isChecked
            gameViewModel.noRepeatRuleEnabled = noRepeatRuleSwitch.isChecked  // Save the no repeat rule

            Toast.makeText(this, "Game rules saved!", Toast.LENGTH_SHORT).show()
            finish()  // Close the activity
        }
    }
}
