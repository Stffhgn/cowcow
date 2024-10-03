package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.models.calculatePenaltyPoints

class PenaltyViewModel : ViewModel() {

    // Instance of PenaltyManager to handle penalty logic
    private val penaltyManager = PenaltyManager

    // LiveData for penalties (observed by the UI)
    private val _penalties = MutableLiveData<List<Penalty>>()
    val penalties: LiveData<List<Penalty>> get() = _penalties

    // LiveData to track penalty status of a specific player
    private val _penaltyStatus = MutableLiveData<Boolean>()
    val penaltyStatus: LiveData<Boolean> get() = _penaltyStatus

    // Logging tag for debugging
    private val TAG = "PenaltyViewModel"

    // ---- Initialization ----

    init {
        _penalties.value = emptyList()  // Initialize with no penalties
        Log.d(TAG, "PenaltyViewModel initialized with no penalties.")
    }

    // ---- Penalty Management ----

    /**
     * Apply a penalty to a player and update LiveData.
     *
     * @param player The player to whom the penalty is applied.
     * @param type The type of penalty to apply (e.g., SILENCED, POINT_DEDUCTION).
     * @param duration The duration for which the penalty will last.
     */
    fun applyPenaltyToPlayer(player: Player, type: PenaltyType, duration: Long) {
        Log.d(TAG, "Applying penalty $type to player ${player.name} with duration $duration.")

        val penalty = Penalty(
            id = Penalty.generatePenaltyId(player.id, type),  // Generate a unique penalty ID
            name = type.name,                                 // Use the name of the PenaltyType
            pointsDeducted = calculatePenaltyPoints(type),    // Calculate points based on the actual penalty type
            penaltyType = type,                               // Assign the penalty type
            isActive = true,                                  // Mark the penalty as active
            duration = duration                               // Set the penalty duration
        )

        // Apply the penalty to the player
        penalty.applyPenalty(player)
        Log.d(TAG, "Penalty applied: ${penalty.name} with ${penalty.pointsDeducted} points deducted.")

        // Update the penalties and status
        _penalties.value = penaltyManager.getActivePenalties()
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
    }

    /**
     * Remove a penalty from a player and update LiveData.
     *
     * @param player The player whose penalty is removed.
     * @param penalty The penalty to remove.
     */
    fun removePenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Removing penalty ${penalty.name} from player ${player.name}.")

        penaltyManager.removePenalty(player, penalty)  // Ensure the logic in PenaltyManager works
        _penalties.value = penaltyManager.getActivePenalties()  // Update LiveData with remaining penalties
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)  // Update player's penalty status

        Log.d(TAG, "Penalty removed. Active penalties updated. Is player penalized: ${_penaltyStatus.value}")
    }

    /**
     * Check if a specific player has an active penalty.
     *
     * @param player The player to check.
     * @return LiveData<Boolean> indicating whether the player is penalized.
     */
    fun checkIfPlayerIsPenalized(player: Player) {
        Log.d(TAG, "Checking if player ${player.name} is penalized.")
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
        Log.d(TAG, "Player penalized: ${_penaltyStatus.value}")
    }

    /**
     * Clear all penalties for a specific player and update LiveData.
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearPenaltiesForPlayer(player: Player) {
        Log.d(TAG, "Clearing all penalties for player ${player.name}.")

        penaltyManager.clearPlayerPenalties(player)  // Ensure this method clears penalties correctly
        _penalties.value = penaltyManager.getActivePenalties()  // Update LiveData with the new state
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)  // Update player's penalty status

        Log.d(TAG, "All penalties cleared. Is player penalized: ${_penaltyStatus.value}")
    }

    // ---- Utility Functions ----

    /**
     * Get the list of active penalties.
     *
     * @return A list of currently active penalties.
     */
    fun getActivePenalties(): List<Penalty> {
        val activePenalties = penaltyManager.getActivePenalties()
        Log.d(TAG, "Retrieved active penalties: ${activePenalties.size} penalties.")
        return activePenalties
    }
}
