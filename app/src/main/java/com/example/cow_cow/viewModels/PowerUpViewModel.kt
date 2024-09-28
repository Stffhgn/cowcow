package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.managers.PowerUpManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.enums.PowerUpType

class PowerUpViewModel : ViewModel() {

    // LiveData to observe the available power-ups for the current player
    private val _availablePowerUps = MutableLiveData<List<PowerUp>>()
    val availablePowerUps: LiveData<List<PowerUp>> get() = _availablePowerUps

    // LiveData to track the currently active power-ups for the player
    private val _activePowerUps = MutableLiveData<List<PowerUp>>()
    val activePowerUps: LiveData<List<PowerUp>> get() = _activePowerUps

    // LiveData to track if a specific power-up is active (e.g., DOUBLE_POINTS)
    private val _isPowerUpActive = MutableLiveData<Boolean>()
    val isPowerUpActive: LiveData<Boolean> get() = _isPowerUpActive

    // Reference to the PowerUpManager to handle power-up logic
    private val powerUpManager = PowerUpManager()

    // Function to initialize the power-ups for a player
    fun initializePowerUps(player: Player) {
        val availablePowerUps = powerUpManager.getAvailablePowerUps(player)
        _availablePowerUps.value = availablePowerUps

        val activePowerUps = powerUpManager.getActivePowerUps(player)
        _activePowerUps.value = activePowerUps
    }

    // Function to activate a power-up for a player
    fun activatePowerUp(player: Player, powerUpType: PowerUpType) {
        powerUpManager.activatePowerUp(player, powerUpType)
        updatePowerUpState(player)
    }

    // Function to deactivate a power-up for a player
    fun deactivatePowerUp(player: Player, powerUpType: PowerUpType) {
        powerUpManager.deactivatePowerUp(player, powerUpType)
        updatePowerUpState(player)
    }

    // Helper function to update the power-up state in the ViewModel
    private fun updatePowerUpState(player: Player) {
        _activePowerUps.value = powerUpManager.getActivePowerUps(player)
        _availablePowerUps.value = powerUpManager.getAvailablePowerUps(player)
        _isPowerUpActive.value = powerUpManager.isPowerUpActive(player, PowerUpType.DOUBLE_POINTS)
    }

    // Function to check if a specific power-up is active for a player
    fun isSpecificPowerUpActive(player: Player, powerUpType: PowerUpType): Boolean {
        return powerUpManager.isPowerUpActive(player, powerUpType)
    }

    // Function to handle the expiration of a power-up (optional)
    fun handlePowerUpExpiration(player: Player, powerUpType: PowerUpType) {
        powerUpManager.deactivatePowerUp(player, powerUpType)
        updatePowerUpState(player)
    }
}
