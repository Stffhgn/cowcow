package com.example.cow_cow.managers

import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp

object PowerUpManager {

    private const val TAG = "PowerUpManager"

    /**
     * Activates a power-up for a given player.
     *
     * @param player The player who is activating the power-up.
     * @param powerUpType The type of power-up being activated.
     * @param duration The duration for which the power-up will remain active (in milliseconds).
     * @param effectValue Optional: The value associated with the power-up effect (e.g., extra points).
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType, duration: Long, effectValue: Int = 0) {
        Log.d(TAG, "Activating power-up ${powerUpType.name} for player ${player.name}")

        // Create the power-up and add it to the player's active power-ups
        val powerUp = PowerUp(
            type = powerUpType,
            isActive = true,
            duration = System.currentTimeMillis() + duration,
            effectValue = effectValue
        )

        player.activePowerUps.add(powerUp)
        applyPowerUpEffect(player, powerUp)
    }

    /**
     * Deactivates a power-up for a given player after its duration expires.
     *
     * @param player The player whose power-up is deactivating.
     * @param powerUpType The type of power-up being deactivated.
     */
    fun deactivatePowerUp(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Deactivating power-up ${powerUpType.name} for player ${player.name}")

        // Find the power-up and deactivate it
        val powerUp = player.activePowerUps.find { it.type == powerUpType && it.isActive }
        powerUp?.let {
            it.isActive = false
            removePowerUpEffect(player, it)
        }
    }

    /**
     * Checks for any expired power-ups and deactivates them for a player.
     *
     * @param player The player whose power-ups are being checked.
     */
    fun checkForExpiredPowerUps(player: Player) {
        val currentTime = System.currentTimeMillis()
        val expiredPowerUps = player.activePowerUps.filter { it.isActive && it.duration <= currentTime }

        expiredPowerUps.forEach { powerUp ->
            Log.d(TAG, "Power-up ${powerUp.type.name} has expired for player ${player.name}")
            deactivatePowerUp(player, powerUp.type)
        }
    }

    /**
     * Applies the effect of the given power-up to the player.
     *
     * @param player The player to apply the power-up effect to.
     * @param powerUp The power-up being applied.
     */
    fun applyPowerUpEffect(player: Player, powerUp: PowerUp) {
        Log.d(TAG, "Applying power-up effect: ${powerUp.type.name} to player ${player.name}")

        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                player.basePoints *= 2
                Log.d(TAG, "Double Points applied: ${player.basePoints} points for ${player.name}.")
            }
            PowerUpType.BONUS_POINTS -> {
                player.addBonusPoints(powerUp.effectValue)
                Log.d(TAG, "Bonus Points applied: ${powerUp.effectValue} bonus points for ${player.name}.")
            }
            PowerUpType.IMMUNITY -> {
                player.isSilenced = false
                Log.d(TAG, "Immunity applied: ${player.name} is no longer silenced.")
            }
            else -> {
                Log.d(TAG, "No specific effect applied for power-up: ${powerUp.type.name}")
            }
        }
    }
    /**
     * Grants a specific power-up to a player.
     *
     * @param player The player receiving the power-up.
     * @param powerUpType The type of power-up being granted.
     * @param effectValue Optional: The value associated with the power-up effect (e.g., extra points).
     */
    fun grantPowerUp(player: Player, powerUpType: PowerUpType, effectValue: Int = 0) {
        Log.d(TAG, "Granting power-up ${powerUpType.name} to player ${player.name}")

        // Create the power-up and add it to the player's list of power-ups
        val powerUp = PowerUp(
            type = powerUpType,
            isActive = true,
            duration = 300000, // Assigning a default duration if needed
            effectValue = effectValue
        )

        // Add power-up to player's active power-ups
        player.activePowerUps.add(powerUp)

        // Log that the power-up was successfully granted
        Log.d(TAG, "Power-up ${powerUpType.name} with effect value $effectValue granted to player ${player.name}")

        // Apply the power-up effect immediately if applicable
        applyPowerUpEffect(player, powerUp)
    }

    /**
     * Removes the effect of the given power-up from the player.
     *
     * @param player The player to remove the power-up effect from.
     * @param powerUp The power-up being deactivated.
     */
    private fun removePowerUpEffect(player: Player, powerUp: PowerUp) {
        Log.d(TAG, "Removing power-up effect: ${powerUp.type.name} from player ${player.name}")

        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                player.basePoints /= 2
                Log.d(TAG, "Double points removed from player ${player.name}.")
            }
            PowerUpType.IMMUNITY -> {
                player.isSilenced = true
                Log.d(TAG, "Player ${player.name} is silenced again after immunity ends.")
            }
            else -> {
                Log.d(TAG, "No specific removal effect for power-up: ${powerUp.type.name}")
            }
        }
    }

    /**
     * Clears all active power-ups from the player's list.
     *
     * @param player The player whose active power-ups will be cleared.
     */
    fun clearActivePowerUps(player: Player) {
        Log.d(TAG, "Clearing all active power-ups for player ${player.name}")
        player.activePowerUps.clear()
    }

    /**
     * Grants a random power-up to the player.
     *
     * @param player The player who is receiving a random power-up.
     * @param duration The duration for the power-up effect.
     */
    fun grantRandomPowerUp(player: Player, duration: Long) {
        val allPowerUps = PowerUpType.values().toList()
        val randomPowerUpType = allPowerUps.random()
        Log.d(TAG, "Granting random power-up ${randomPowerUpType.name} to player ${player.name}")
        activatePowerUp(player, randomPowerUpType, duration)
    }

    /**
     * Grants a specific power-up to all players in the game.
     *
     * @param players The list of players receiving the power-up.
     * @param powerUpType The type of power-up to grant.
     * @param duration The duration of the power-up.
     */
    fun grantPowerUpToAll(players: List<Player>, powerUpType: PowerUpType, duration: Long) {
        Log.d(TAG, "Granting power-up ${powerUpType.name} to all players.")
        players.forEach { player ->
            activatePowerUp(player, powerUpType, duration)
        }
    }

    /**
     * Retrieves the list of active power-ups for a player.
     *
     * @param player The player whose active power-ups are being retrieved.
     * @return List of active power-ups.
     */
    fun getActivePowerUps(player: Player): List<PowerUp> {
        return player.activePowerUps.filter { it.isActive }
    }

    /**
     * Retrieves the list of available power-ups for a player (e.g., from a shop or power-up menu).
     *
     * @param player The player whose available power-ups are being retrieved.
     * @return List of available power-ups.
     */
    fun getAvailablePowerUps(player: Player): List<PowerUp> {
        // This could be based on game logic such as what power-ups are currently unlockable.
        return listOf(
            PowerUp(type = PowerUpType.DOUBLE_POINTS, isActive = false),
            PowerUp(type = PowerUpType.BONUS_POINTS, isActive = false),
            PowerUp(type = PowerUpType.IMMUNITY, isActive = false)
        ) // Example power-ups available for this player.
    }

    /**
     * Checks if a specific power-up is currently active for the player.
     *
     * @param player The player whose power-up status is being checked.
     * @param powerUpType The type of power-up to check.
     * @return True if the power-up is active, false otherwise.
     */
    fun isPowerUpActive(player: Player, powerUpType: PowerUpType): Boolean {
        return player.activePowerUps.any { it.type == powerUpType && it.isActive }
    }
}
