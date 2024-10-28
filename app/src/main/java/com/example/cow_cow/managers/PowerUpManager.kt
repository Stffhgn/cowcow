package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp
import kotlinx.coroutines.*

object PowerUpManager {
    private const val TAG = "PowerUpManager"

    // --- Public API ---


    /**
     * Retrieves all active power-ups for a given player.
     *
     * @param player The player whose active power-ups are being retrieved.
     * @return A list of active power-ups.
     */
    fun getActivePowerUps(player: Player): List<PowerUp> {
        val activePowerUps = player.activePowerUps.filter { it.isActive && !it.hasExpired(System.currentTimeMillis()) }
        Log.d(TAG, "Found ${activePowerUps.size} active power-ups for player ${player.name}.")
        return activePowerUps
    }

    /**
     * Check if a player has an active immunity power-up.
     *
     * @param player The player to check for immunity.
     * @return True if the player has immunity, false otherwise.
     */
    fun hasImmunity(player: Player): Boolean {
        val hasImmunity = player.activePowerUps.any {
            it.isActive && (it.type == PowerUpType.IMMUNITY || it.type == PowerUpType.NO_PENALTIES)
        }
        Log.d(TAG, "Player ${player.name} has immunity: $hasImmunity")
        return hasImmunity
    }

    /**
     * Uses a power-up from a player's inventory.
     */
    fun usePowerUpFromInventory(player: Player, powerUpType: PowerUpType) {
        player.heldPowerUps.find { it.type == powerUpType && !it.isActive }?.let { powerUp ->
            activatePowerUp(player, powerUp.type, powerUp.duration, powerUp.effectValue)
            player.heldPowerUps.remove(powerUp)
            Log.d(TAG, "Used power-up ${powerUp.type.name} from inventory for ${player.name}.")
        } ?: Log.w(TAG, "${player.name} does not have an unused ${powerUpType.name} power-up.")
    }

    /**
     * Activates a power-up for a player.
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType, duration: Long, effectValue: Int = 0) {
        Log.d(TAG, "Activating ${powerUpType.name} for ${player.name} (duration: ${duration}ms, effect: $effectValue).")
        val powerUp = PowerUp(type = powerUpType, isActive = true, duration = System.currentTimeMillis() + duration, effectValue = effectValue)
        player.activePowerUps.add(powerUp)
        applyPowerUpEffect(player, powerUp)
    }

    /**
     * Applies the effects of a list of power-ups to a player's points.
     *
     * @param powerUps The list of active power-ups to apply.
     * @param player The player whose points are being adjusted.
     * @param currentPoints The current points before applying power-ups.
     * @return The new points after applying all power-up effects.
     */
    fun applyEffectsToPoints(powerUps: List<PowerUp>, player: Player, currentPoints: Int): Int {
        var adjustedPoints = currentPoints

        powerUps.forEach { powerUp ->
            adjustedPoints = applyEffectToPoints(adjustedPoints, player, powerUp)
            Log.d(TAG, "${powerUp.type.name} applied. ${player.name}'s points adjusted to $adjustedPoints.")
        }

        return adjustedPoints
    }

    /**
     * Checks for expired power-ups and deactivates them.
     */
    fun checkForExpiredPowerUps(player: Player) {
        val currentTime = System.currentTimeMillis()
        player.activePowerUps.filter { it.isActive && it.hasExpired(currentTime) }.forEach { powerUp ->
            deactivatePowerUp(player, powerUp)
        }
        player.activePowerUps.removeAll { !it.isActive }
    }

    /**
     * Clears all active power-ups for a player.
     */
    fun clearActivePowerUps(player: Player) {
        Log.d(TAG, "Clearing all active power-ups for ${player.name}.")
        player.activePowerUps.forEach { deactivatePowerUp(player, it) }
        player.activePowerUps.clear()
    }

    // --- Internal Helper Methods ---

    /**
     * Applies a specific power-up effect.
     */
    fun applyPowerUpEffect(player: Player, powerUp: PowerUp) {
        Log.d(TAG, "Applying ${powerUp.type.name} for ${player.name} with effect value ${powerUp.effectValue}.")
        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> player.basePoints *= 2
            PowerUpType.SCORE_MULTIPLIER -> player.basePoints += player.basePoints * powerUp.effectValue / 100
            PowerUpType.BONUS_POINTS -> player.basePoints += powerUp.effectValue
            PowerUpType.IMMUNITY, PowerUpType.NO_PENALTIES -> Log.d(TAG, "${player.name} is immune to penalties.")
            PowerUpType.TELEPORTATION -> handleTeleportation(player)
            PowerUpType.SHIELD -> applyShield(player)
            PowerUpType.RANDOM_POWER_UP -> grantRandomPowerUp(player)
            PowerUpType.POINTS_TRANSFER -> transferPointsFromAnotherPlayer(player)
            PowerUpType.TEAM_BUFF -> applyTeamBuff(player, powerUp.duration)
            else -> Log.d(TAG, "Unhandled power-up type: ${powerUp.type.name} for ${player.name}.")
        }
    }

    /**
     * Adjusts points based on power-up effects.
     *
     * @param points The current points to be adjusted.
     * @param player The player whose points are being adjusted.
     * @param powerUp The active power-up to apply.
     * @return The adjusted points after applying the power-up effect.
     */
    fun applyEffectToPoints(points: Int, player: Player, powerUp: PowerUp): Int {
        return when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> points * 2
            PowerUpType.SCORE_MULTIPLIER -> points + (points * powerUp.effectValue / 100)
            PowerUpType.BONUS_POINTS -> points + powerUp.effectValue
            else -> points
        }.also { adjustedPoints ->
            Log.d(TAG, "${powerUp.type.name} applied. ${player.name}'s points adjusted to $adjustedPoints.")
        }
    }
    /**
     * Deactivates a power-up and removes its effects.
     */
    fun deactivatePowerUp(player: Player, powerUp: PowerUp) {
        powerUp.isActive = false
        removePowerUpEffect(player, powerUp)
        Log.d(TAG, "Deactivated ${powerUp.type.name} for ${player.name}.")
    }

    /**
     * Removes a power-up's effect.
     */
    private fun removePowerUpEffect(player: Player, powerUp: PowerUp) {
        when (powerUp.type) {
            PowerUpType.IMMUNITY -> player.isSilenced = false
            PowerUpType.SHIELD -> player.hasActiveShield = false
            else -> Log.d(TAG, "No specific removal action for ${powerUp.type.name}.")
        }
    }

    // --- Specialized Effects ---

    private fun handleTeleportation(player: Player) {
        Log.d(TAG, "Handling teleportation for ${player.name}.")
        // Implement teleportation logic.
    }

    private fun applyShield(player: Player) {
        player.hasActiveShield = true
        Log.d(TAG, "Shield applied to ${player.name}.")
    }

    fun grantRandomPowerUp(player: Player) {
        val randomPowerUp = generateRandomPowerUp()
        player.heldPowerUps.add(randomPowerUp)
        Log.d(TAG, "${player.name} received a random power-up: ${randomPowerUp.type.name}.")
    }

    fun transferPointsFromAnotherPlayer(player: Player) {
        val otherPlayer = findPlayerToTransferFrom()
        val pointsToTransfer = 10
        otherPlayer?.let {
            player.basePoints += pointsToTransfer
            it.basePoints -= pointsToTransfer
            Log.d(TAG, "$pointsToTransfer points transferred from ${it.name} to ${player.name}.")
        } ?: Log.d(TAG, "No player available for point transfer to ${player.name}.")
    }

    /**
     * Applies a team buff to a single player.
     *
     * @param player The player receiving the team buff.
     * @param duration The duration of the buff.
     */
    fun applyTeamBuff(player: Player, duration: Long) {
        Log.d(TAG, "Team Buff applied for ${player.name}.")

        // Placeholder for actual buff logic (e.g., increased points or other effects)
        player.bonusPoints += 10 // Example effect: Add 10 bonus points as a buff.

        // Use Coroutine to handle the expiration of the buff.
        CoroutineScope(Dispatchers.Main).launch {
            delay(duration)
            player.bonusPoints -= 10 // Revert the buff effect.
            Log.d(TAG, "Team Buff expired for ${player.name}. Bonus points reverted.")
        }
    }

    fun generateRandomPowerUp(): PowerUp {
        val randomType = PowerUpType.values().random()
        val randomEffectValue = (5..15).random()
        return PowerUp(type = randomType, effectValue = randomEffectValue)
    }

    private fun findPlayerToTransferFrom(): Player? {
        // Implement logic for selecting a player for point transfer.
        return null
    }
}
