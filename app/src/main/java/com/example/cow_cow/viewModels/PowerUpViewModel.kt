package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.managers.PowerUpManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.enums.PowerUpType
import android.util.Log

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
    private val powerUpManager = PowerUpManager

    // Logging tag for debugging
    private val TAG = "PowerUpViewModel"

    /**
     * Initialize the power-ups for the specified player.
     *
     * @param player The player whose power-ups are being initialized.
     */
    fun initializePowerUps(player: Player) {
        Log.d(TAG, "Initializing power-ups for player: ${player.name}")
        val availablePowerUps = powerUpManager.getAvailablePowerUps(player)
        _availablePowerUps.value = availablePowerUps

        val activePowerUps = powerUpManager.getActivePowerUps(player)
        _activePowerUps.value = activePowerUps
        Log.d(TAG, "Power-ups initialized. Available: ${availablePowerUps.size}, Active: ${activePowerUps.size}")
    }

    /**
     * Activate a power-up for a player.
     *
     * @param player The player activating the power-up.
     * @param powerUpType The type of power-up being activated.
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Activating power-up ${powerUpType.name} for player: ${player.name}")
        powerUpManager.activatePowerUp(player, powerUpType, duration = 60000L) // Example duration of 60 seconds
        updatePowerUpState(player)
    }

    /**
     * Deactivate a power-up for a player.
     *
     * @param player The player deactivating the power-up.
     * @param powerUpType The type of power-up being deactivated.
     */
    fun deactivatePowerUp(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Deactivating power-up ${powerUpType.name} for player: ${player.name}")
        powerUpManager.deactivatePowerUp(player, powerUpType)
        updatePowerUpState(player)
    }

    /**
     * Update the power-up state for the specified player in the ViewModel.
     *
     * @param player The player whose power-up state is being updated.
     */
    private fun updatePowerUpState(player: Player) {
        _activePowerUps.value = powerUpManager.getActivePowerUps(player)
        _availablePowerUps.value = powerUpManager.getAvailablePowerUps(player)
        _isPowerUpActive.value = powerUpManager.isPowerUpActive(player, PowerUpType.DOUBLE_POINTS)
        Log.d(TAG, "Updated power-up state for player: ${player.name}")
    }

    /**
     * Check if a specific power-up is active for the player.
     *
     * @param player The player whose power-up status is being checked.
     * @param powerUpType The type of power-up to check.
     * @return True if the power-up is active, false otherwise.
     */
    fun isSpecificPowerUpActive(player: Player, powerUpType: PowerUpType): Boolean {
        val isActive = powerUpManager.isPowerUpActive(player, powerUpType)
        Log.d(TAG, "Power-up ${powerUpType.name} active for player: ${player.name} -> $isActive")
        return isActive
    }

    /**
     * Handle the expiration of a power-up for the player.
     *
     * @param player The player whose power-up expired.
     * @param powerUpType The type of power-up that expired.
     */
    fun handlePowerUpExpiration(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Handling power-up expiration for ${powerUpType.name} for player: ${player.name}")
        powerUpManager.deactivatePowerUp(player, powerUpType)
        updatePowerUpState(player)
    }
}
