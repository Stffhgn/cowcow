package com.example.cow_cow.managers

import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp

object PowerUpManager {

    /**
     * Activates a power-up for a given player.
     *
     * @param player The player who is activating the power-up.
     * @param powerUpType The type of power-up being activated.
     * @param duration The duration for which the power-up will remain active (in milliseconds).
     * @param effectValue Optional: The value associated with the power-up effect (e.g., extra points).
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType, duration: Long, effectValue: Int = 0) {
        // Create the power-up and activate it
        val powerUp = PowerUp(
            type = powerUpType,
            isActive = true,
            duration = System.currentTimeMillis() + duration,
            effectValue = effectValue
        )

        // Add the power-up to the player's active power-ups
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
        // Find the power-up to deactivate
        val powerUp = player.activePowerUps.find { it.type == powerUpType && it.isActive }
        powerUp?.let {
            it.isActive = false
            removePowerUpEffect(player, it)
        }
    }

    /**
     * Checks for any expired power-ups and deactivates them.
     *
     * @param player The player whose power-ups are being checked.
     */
    fun checkForExpiredPowerUps(player: Player) {
        val currentTime = System.currentTimeMillis()
        val expiredPowerUps = player.activePowerUps.filter { it.isActive && it.duration <= currentTime }

        expiredPowerUps.forEach { powerUp ->
            deactivatePowerUp(player, powerUp.type)
        }
    }

    /**
     * Applies the effect of the given power-up to the player.
     *
     * @param player The player to apply the power-up effect to.
     * @param powerUp The power-up being applied.
     */
    private fun applyPowerUpEffect(player: Player, powerUp: PowerUp) {
        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                // Double the player's points for the duration of the power-up
                player.basePoints *= 2
            }
            PowerUpType.EXTRA_POINTS -> {
                // Add extra points immediately
                player.addBonusPoints(powerUp.effectValue)
            }
            PowerUpType.IMMUNITY -> {
                // Provide immunity from penalties for the duration
                player.isSilenced = false  // Example of cancelling a penalty effect
            }
            PowerUpType.SPEED_BOOST -> {
                // Any logic for increasing player's speed (could be used in other game modes)
            }
            PowerUpType.EXTRA_TIME -> {
                // Increase time-related gameplay (handled externally by time management systems)
            }
            PowerUpType.SHIELD -> {
                // Add shield logic for protecting against penalties or damage
            }
            PowerUpType.HEALTH_REGEN -> {
                // Gradually restore the player's health over time
            }
            // Additional power-up effects can be added here
            else -> {
                // Handle other power-ups (e.g., teleportation, invisibility, etc.)
            }
        }
    }

    /**
     * Removes the effect of the given power-up from the player.
     *
     * @param player The player to remove the power-up effect from.
     * @param powerUp The power-up being deactivated.
     */
    private fun removePowerUpEffect(player: Player, powerUp: PowerUp) {
        when (powerUp.type) {
            PowerUpType.DOUBLE_POINTS -> {
                // Restore the player's points to normal (if needed, depending on implementation)
            }
            PowerUpType.IMMUNITY -> {
                // Remove immunity and allow penalties to apply again
            }
            PowerUpType.SHIELD -> {
                // Remove shield effect
            }
            // Additional power-up removal logic as needed
        }
    }

    /**
     * Removes all active power-ups from a player (e.g., during game reset).
     *
     * @param player The player whose power-ups will be cleared.
     */
    fun clearActivePowerUps(player: Player) {
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
        activatePowerUp(player, randomPowerUpType, duration)
    }

    /**
     * Grants a power-up to all players in the game.
     *
     * @param players The list of players receiving the power-up.
     * @param powerUpType The type of power-up to grant.
     * @param duration The duration of the power-up.
     */
    fun grantPowerUpToAll(players: List<Player>, powerUpType: PowerUpType, duration: Long) {
        players.forEach { player ->
            activatePowerUp(player, powerUpType, duration)
        }
    }
}
