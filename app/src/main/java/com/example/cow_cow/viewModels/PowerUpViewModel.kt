package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.example.cow_cow.controllers.PowerUpController
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

    // Logging tag for debugging
    private val TAG = "PowerUpViewModel"

    init {
        Log.d(TAG, "PowerUpViewModel initialized.")
    }

    /**
     * Initialize the power-ups for the specified player.
     *
     * @param player The player whose power-ups are being initialized.
     */
    fun initializePowerUps(player: Player) {
        Log.d(TAG, "Initializing power-ups for player: ${player.name}")
        updatePowerUpState(player)
    }

    /**
     * Activate a power-up for a player.
     *
     * @param player The player activating the power-up.
     * @param powerUpType The type of power-up being activated.
     * @param duration The duration for which the power-up is active.
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType, duration: Long = 60000L) {
        Log.d(TAG, "Activating power-up ${powerUpType.name} for player: ${player.name} with duration: ${duration}ms")
        PowerUpController.activatePowerUp(player, powerUpType, duration)
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
        PowerUpController.deactivatePowerUpForPlayer(player, powerUpType)
        updatePowerUpState(player)
    }

    /**
     * Update the power-up state for the specified player in the ViewModel.
     *
     * @param player The player whose power-up state is being updated.
     */
    private fun updatePowerUpState(player: Player) {
        // Update LiveData values for active and available power-ups
        _activePowerUps.value = PowerUpController.getActivePowerUpsForPlayer(player)
        _availablePowerUps.value = player.heldPowerUps.filter { !it.isActive }

        // Check if a specific power-up is active (e.g., DOUBLE_POINTS)
        _isPowerUpActive.value = _activePowerUps.value?.any { it.type == PowerUpType.DOUBLE_POINTS } == true

        // Log the updated state
        Log.d(TAG, "Updated power-up state for player: ${player.name}. Active: ${_activePowerUps.value?.size}, Available: ${_availablePowerUps.value?.size}")
    }

    /**
     * Check if a specific power-up is active for the player.
     *
     * @param player The player whose power-up status is being checked.
     * @param powerUpType The type of power-up to check.
     * @return True if the power-up is active, false otherwise.
     */
    fun isSpecificPowerUpActive(player: Player, powerUpType: PowerUpType): Boolean {
        val isActive = player.activePowerUps.any { it.type == powerUpType && it.isActive && !it.hasExpired(System.currentTimeMillis()) }
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
        deactivatePowerUp(player, powerUpType)
    }

    /**
     * Clear all active power-ups for a player.
     *
     * @param player The player whose active power-ups will be cleared.
     */
    fun clearAllActivePowerUps(player: Player) {
        Log.d(TAG, "Clearing all active power-ups for player: ${player.name}")
        PowerUpController.clearAllActivePowerUpsForPlayer(player)
        updatePowerUpState(player)
    }
}
