package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cow_cow.managers.PenaltyManager
import com.example.cow_cow.models.Penalty
import com.example.cow_cow.models.Player
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.models.calculatePenaltyPoints
import com.example.cow_cow.repositories.PlayerRepository

class PenaltyViewModel(application: Application) : AndroidViewModel(application) {


    // Instance of PenaltyManager to handle penalty logic
    private val context = getApplication<Application>().applicationContext
    private val playerRepository = PlayerRepository(context)
    private val playerManager = PlayerManager(playerRepository)
    private val penaltyManager = PenaltyManager(playerManager)

    // LiveData for penalties (observed by the UI)
    private val _penalties = MutableLiveData<List<Penalty>>()
    val penalties: LiveData<List<Penalty>> get() = _penalties

    // LiveData to track penalty status of a specific player
    private val _penaltyStatus = MutableLiveData<Boolean>()
    val penaltyStatus: LiveData<Boolean> get() = _penaltyStatus

    // Logging tag for debugging
    private val TAG = "PenaltyViewModel"

    init {
        // Initialize with no penalties
        _penalties.value = emptyList()
        Log.d(TAG, "PenaltyViewModel initialized with no penalties.")
    }

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
            id = Penalty.generatePenaltyId(player.id, type).toString(),
            name = type.name,
            pointsDeducted = calculatePenaltyPoints(type),
            penaltyType = type,
            isActive = true,
            duration = duration
        )

        // Apply the penalty to the player
        penaltyManager.applyPenalty(player, penalty)
        updatePenaltiesLiveData()
        Log.d(TAG, "Penalty applied: ${penalty.name} with ${penalty.pointsDeducted} points deducted.")
    }

    /**
     * Remove a penalty from a player and update LiveData.
     *
     * @param player The player whose penalty is removed.
     * @param penalty The penalty to remove.
     */
    fun removePenalty(player: Player, penalty: Penalty) {
        Log.d(TAG, "Removing penalty ${penalty.name} from player ${player.name}.")
        penaltyManager.removePenalty(player, penalty)
        updatePenaltiesLiveData()
    }

    /**
     * Check if a specific player has an active penalty.
     *
     * @param player The player to check.
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
        penaltyManager.clearAllPenalties(player)
        updatePenaltiesLiveData()
    }

    /**
     * Update LiveData for penalties and penalty status.
     */
    private fun updatePenaltiesLiveData() {
        _penalties.value = penaltyManager.getActivePenalties()
        Log.d(TAG, "Updated penalties list with ${_penalties.value?.size ?: 0} active penalties.")
    }

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
