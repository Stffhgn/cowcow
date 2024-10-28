package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.PowerUp
import com.example.cow_cow.enums.PowerUpType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PowerUpRepository {

    // Constants for SharedPreferences
    private val PREFS_NAME = "com.example.cow_cow.POWER_UP_PREFERENCES"
    private val POWER_UPS_KEY = "POWER_UPS_KEY"
    private val gson = Gson()

    // LiveData for observing power-up data
    private val _powerUpsLiveData = MutableLiveData<List<PowerUp>>()
    val powerUpsLiveData: LiveData<List<PowerUp>> get() = _powerUpsLiveData

    /**
     * Get the SharedPreferences instance.
     */
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Retrieve the list of power-ups from SharedPreferences.
     */
    fun getPowerUps(context: Context): List<PowerUp> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(POWER_UPS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<PowerUp>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    /**
     * Save the list of power-ups to SharedPreferences and update LiveData.
     */
    private fun savePowerUps(powerUps: List<PowerUp>, context: Context) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(powerUps)
        editor.putString(POWER_UPS_KEY, json)
        editor.apply()

        // Update LiveData
        _powerUpsLiveData.value = powerUps
    }

    /**
     * Add a new power-up to the list and save it.
     */
    fun addPowerUp(context: Context, powerUp: PowerUp) {
        val powerUps = getPowerUps(context).toMutableList()
        powerUps.add(powerUp)
        savePowerUps(powerUps, context)
    }

    /**
     * Remove a power-up from the list and save the updated list.
     */
    fun removePowerUp(context: Context, powerUp: PowerUp) {
        val powerUps = getPowerUps(context).toMutableList()
        powerUps.remove(powerUp)
        savePowerUps(powerUps, context)
    }

    /**
     * Activate a power-up by setting its isActive flag to true.
     */
    fun activatePowerUp(context: Context, type: PowerUpType, duration: Long) {
        val powerUps = getPowerUps(context).toMutableList()
        val index = powerUps.indexOfFirst { it.type == type }
        if (index != -1) {
            val updatedPowerUp = powerUps[index].copy(isActive = true, duration = System.currentTimeMillis() + duration)
            powerUps[index] = updatedPowerUp
            savePowerUps(powerUps, context)
        }
    }

    /**
     * Deactivate a specific power-up by setting its isActive flag to false.
     */
    fun deactivatePowerUp(context: Context, type: PowerUpType) {
        val powerUps = getPowerUps(context).toMutableList()
        val index = powerUps.indexOfFirst { it.type == type }
        if (index != -1) {
            val updatedPowerUp = powerUps[index].copy(isActive = false)
            powerUps[index] = updatedPowerUp
            savePowerUps(powerUps, context)
        }
    }

    /**
     * Deactivate all power-ups (set isActive to false for all).
     */
    fun deactivateAllPowerUps(context: Context) {
        val powerUps = getPowerUps(context).map { it.copy(isActive = false) }
        savePowerUps(powerUps, context)
    }

    /**
     * Clear all power-ups (useful for game reset).
     */
    fun clearPowerUps(context: Context) {
        savePowerUps(emptyList(), context)
    }

    /**
     * Initialize default power-ups for a new game or reset.
     */
    fun initializeDefaultPowerUps(context: Context) {
        val defaultPowerUps = listOf(
            PowerUp(type = PowerUpType.DOUBLE_POINTS, isActive = false, effectValue = 2),
            PowerUp(type = PowerUpType.IMMUNITY, isActive = false),
            PowerUp(type = PowerUpType.EXTRA_TIME, isActive = false, effectValue = 30)
        )
        savePowerUps(defaultPowerUps, context)
    }

    /**
     * Get a specific power-up by its type.
     */
    fun getPowerUpByType(context: Context, type: PowerUpType): PowerUp? {
        return getPowerUps(context).find { it.type == type }
    }

    /**
     * Retrieve all active power-ups.
     */
    fun getActivePowerUps(context: Context): List<PowerUp> {
        return getPowerUps(context).filter { it.isActive }
    }
}
