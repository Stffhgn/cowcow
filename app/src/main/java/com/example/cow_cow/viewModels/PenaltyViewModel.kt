package com.example.cow_cow.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.PenaltyType

class PenaltyViewModel : ViewModel() {

    // Instance of PenaltyManager to handle penalty logic
    private val penaltyManager = PenaltyManager()

    // LiveData for penalties (observed by the UI)
    private val _penalties = MutableLiveData<List<Penalty>>()
    val penalties: LiveData<List<Penalty>> get() = _penalties

    // LiveData to track penalty status of a specific player
    private val _penaltyStatus = MutableLiveData<Boolean>()
    val penaltyStatus: LiveData<Boolean> get() = _penaltyStatus

    // ---- Initialization ----

    init {
        _penalties.value = emptyList()  // Initialize with no penalties
    }

    // ---- Penalty Management ----

    /**
     * Apply a penalty to a player and update LiveData.
     *
     * @param player The player to whom the penalty is applied.
     * @param type The type of penalty to apply (e.g., SILENCED, POINT_DEDUCTION).
     * @param duration The duration for which the penalty will last.
     */
    fun applyPenalty(player: Player, type: PenaltyType, duration: Long) {
        penaltyManager.applyPenalty(player, type, duration)
        _penalties.value = penaltyManager.getActivePenalties()  // Update LiveData with active penalties
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
    }

    /**
     * Remove a penalty from a player and update LiveData.
     *
     * @param player The player whose penalty is removed.
     * @param penalty The penalty to remove.
     */
    fun removePenalty(player: Player, penalty: Penalty) {
        penaltyManager.removePenalty(player, penalty)
        _penalties.value = penaltyManager.getActivePenalties()  // Update LiveData with remaining penalties
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
    }

    /**
     * Check if a specific player has an active penalty.
     *
     * @param player The player to check.
     * @return LiveData<Boolean> indicating whether the player is penalized.
     */
    fun checkIfPlayerIsPenalized(player: Player) {
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
    }

    // ---- Utility Functions ----

    /**
     * Get the list of active penalties.
     *
     * @return A list of currently active penalties.
     */
    fun getActivePenalties(): List<Penalty> {
        return penaltyManager.getActivePenalties()
    }

    /**
     * Clear all penalties for a specific player.
     *
     * @param player The player whose penalties will be cleared.
     */
    fun clearPenaltiesForPlayer(player: Player) {
        penaltyManager.clearPlayerPenalties(player)
        _penalties.value = penaltyManager.getActivePenalties()  // Update LiveData with the new state
        _penaltyStatus.value = penaltyManager.isPlayerPenalized(player)
    }
}
