package com.example.cow_cow.controllers

import android.util.Log
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.enums.PowerUpRarity
import com.example.cow_cow.managers.PowerUpManager
import com.example.cow_cow.managers.PlayerManager
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.PowerUp

object PowerUpController {

    private const val TAG = "PowerUpController"

    /**
     * Activates a power-up for a specific player.
     *
     * @param player The player who is activating the power-up.
     * @param powerUpType The type of power-up being activated.
     * @param duration Optional: The duration for which the power-up will remain active (in milliseconds).
     */
    fun activatePowerUp(player: Player, powerUpType: PowerUpType, duration: Long? = null) {
        Log.d(TAG, "Activating power-up ${powerUpType.name} for player ${player.name}")

        // Find the power-up in player's heldPowerUps
        val powerUp = player.heldPowerUps.find { it.type == powerUpType && it.isHeld && !it.isActive }
        if (powerUp != null) {
            // Set the duration if provided
            if (duration != null) {
                powerUp.duration = duration
            }
            powerUp.activate(player)
            // Move the power-up from heldPowerUps to activePowerUps
            player.activePowerUps.add(powerUp)
            player.heldPowerUps.remove(powerUp)
        } else {
            Log.w(TAG, "Player ${player.name} does not have the power-up ${powerUpType.name} in held power-ups.")
        }
    }

    /**
     * Deactivates a specific power-up for a player.
     *
     * @param player The player whose power-up is being deactivated.
     * @param powerUpType The type of power-up being deactivated.
     */
    fun deactivatePowerUpForPlayer(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Deactivating power-up $powerUpType for player ${player.name}")
        val powerUp = player.activePowerUps.find { it.type == powerUpType && it.isActive }
        if (powerUp != null) {
            powerUp.deactivate(player)
            // Remove the power-up from activePowerUps
            player.activePowerUps.remove(powerUp)
        } else {
            Log.w(TAG, "Active power-up of type $powerUpType not found for player ${player.name}")
        }
    }

    /**
     * Checks and deactivates expired power-ups for a player.
     *
     * @param player The player whose power-ups are being checked.
     */
    fun checkAndDeactivateExpiredPowerUps(player: Player) {
        Log.d(TAG, "Checking expired power-ups for player ${player.name}")
        PowerUpManager.checkForExpiredPowerUps(player)
    }

    /**
     * Grants a specific power-up to a player for future use.
     *
     * @param player The player receiving the power-up.
     * @param powerUpType The type of power-up to grant.
     * @param effectValue Optional: The value associated with the power-up effect (e.g., extra points).
     * @param duration Optional: The duration of the power-up (in milliseconds).
     * @param level Optional: The level of the power-up.
     * @param rarity Optional: The rarity of the power-up.
     */
    fun grantPowerUpToPlayer(
        player: Player,
        powerUpType: PowerUpType,
        effectValue: Int = 0,
        duration: Long = 0L,
        level: Int = 1,
        rarity: PowerUpRarity = PowerUpRarity.COMMON
    ) {
        Log.d(TAG, "Granting power-up $powerUpType to player ${player.name}")
        val powerUp = PowerUp(
            type = powerUpType,
            isHeld = true,
            isActive = false,
            duration = duration,
            effectValue = effectValue,
            level = level,
            rarity = rarity
        )
        player.heldPowerUps.add(powerUp)
    }

    /**
     * Allows a player to use a power-up from their inventory.
     *
     * @param player The player using the power-up.
     * @param powerUpType The type of power-up to activate.
     */
    fun usePowerUpFromInventory(player: Player, powerUpType: PowerUpType) {
        Log.d(TAG, "Player ${player.name} is using power-up $powerUpType from inventory")
        val powerUp = player.heldPowerUps.find { it.type == powerUpType && it.isHeld && !it.isActive }
        if (powerUp != null) {
            powerUp.activate(player)
            // Move the power-up from heldPowerUps to activePowerUps
            player.activePowerUps.add(powerUp)
            player.heldPowerUps.remove(powerUp)
        } else {
            Log.e(TAG, "Power-up $powerUpType not found in ${player.name}'s inventory.")
        }
    }

    /**
     * Retrieves the list of active power-ups for a specific player.
     *
     * @param player The player whose active power-ups are being retrieved.
     * @return List of active power-ups.
     */
    fun getActivePowerUpsForPlayer(player: Player): List<PowerUp> {
        return PowerUpManager.getActivePowerUps(player)
    }

    /**
     * Grants a random power-up to a player.
     *
     * @param player The player who is receiving a random power-up.
     */
    fun grantRandomPowerUpToPlayer(player: Player) {
        val randomPowerUp = PowerUpManager.generateRandomPowerUp()
        randomPowerUp.isHeld = true
        player.heldPowerUps.add(randomPowerUp)
        Log.d(TAG, "${player.name} received a random power-up: ${randomPowerUp.type.name}.")
    }

    /**
     * Grants a specific power-up to all players.
     *
     * @param powerUpType The type of power-up to grant.
     * @param effectValue Optional: The value associated with the power-up effect.
     * @param duration Optional: The duration of the power-up (in milliseconds).
     * @param level Optional: The level of the power-up.
     * @param rarity Optional: The rarity of the power-up.
     */
    fun grantPowerUpToAllPlayers(
        powerUpType: PowerUpType,
        effectValue: Int = 0,
        duration: Long = 0L,
        level: Int = 1,
        rarity: PowerUpRarity = PowerUpRarity.COMMON
    ) {
        Log.d(TAG, "Granting power-up $powerUpType to all players")
        val allPlayers = PlayerManager.getAllPlayers()
        allPlayers.forEach { player ->
            grantPowerUpToPlayer(player, powerUpType, effectValue, duration, level, rarity)
        }
    }

    /**
     * Clears all active power-ups for a specific player.
     *
     * @param player The player whose active power-ups will be cleared.
     */
    fun clearAllActivePowerUpsForPlayer(player: Player) {
        Log.d(TAG, "Clearing all active power-ups for player ${player.name}")
        PowerUpManager.clearActivePowerUps(player)
    }

    /**
     * Handles power-up rewards when a player finds a specific item.
     *
     * @param player The player who found the item.
     * @param powerUpType The power-up awarded for finding the item.
     * @param effectValue Optional: The value associated with the power-up effect.
     * @param duration Optional: The duration of the power-up (in milliseconds).
     * @param level Optional: The level of the power-up.
     * @param rarity Optional: The rarity of the power-up.
     */
    fun rewardPowerUpForFindingItem(
        player: Player,
        powerUpType: PowerUpType,
        effectValue: Int = 0,
        duration: Long = 0L,
        level: Int = 1,
        rarity: PowerUpRarity = PowerUpRarity.COMMON
    ) {
        grantPowerUpToPlayer(player, powerUpType, effectValue, duration, level, rarity)
        Log.d(TAG, "Rewarded power-up $powerUpType to player ${player.name} for finding an item.")
    }
}
