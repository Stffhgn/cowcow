package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.cow_cow.models.PlayerSettings
import com.google.gson.Gson

class SettingsRepository(private val context: Context) {

    // Constants for SharedPreferences
    private val PREFS_NAME = "com.example.cow_cow.SETTINGS"
    private val VERSION_KEY = "SETTINGS_VERSION"
    private val PLAYER_SETTINGS_KEY_PREFIX = "PLAYER_SETTINGS_" // For player-specific settings
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    // --- Generic Save Methods (for flexibility) ---

    /**
     * Save a Boolean setting.
     */
    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    /**
     * Save an Integer setting.
     */
    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    /**
     * Save a String setting.
     */
    fun saveString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    /**
     * Save a Float setting.
     */
    fun saveFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    /**
     * Save a Long setting.
     */
    fun saveLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    /**
     * Save a list of strings (for settings that need multiple values).
     */
    fun saveStringList(key: String, values: List<String>) {
        editor.putString(key, values.joinToString(",")).apply()
    }

    // --- Generic Load Methods (for flexibility) ---

    /**
     * Get a Boolean setting with a default value.
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Get an Integer setting with a default value.
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Get a String setting with a default value.
     */
    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Get a Float setting with a default value.
     */
    fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    /**
     * Get a Long setting with a default value.
     */
    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * Get a list of strings by splitting a stored string.
     */
    fun getStringList(key: String, defaultValue: List<String>): List<String> {
        val storedValue = sharedPreferences.getString(key, null)
        return storedValue?.split(",") ?: defaultValue
    }

    // --- Player-Specific Settings ---

    /**
     * Retrieve settings for a specific player.
     */
    fun getPlayerSettings(playerId: Int): PlayerSettings {
        val json = sharedPreferences.getString(PLAYER_SETTINGS_KEY_PREFIX + playerId, null)
        return if (json != null) {
            gson.fromJson(json, PlayerSettings::class.java)
        } else {
            PlayerSettings() // Default settings
        }
    }

    /**
     * Save settings for a specific player.
     */
    fun savePlayerSettings(playerId: Int, settings: PlayerSettings) {
        val json = gson.toJson(settings)
        editor.putString(PLAYER_SETTINGS_KEY_PREFIX + playerId, json).apply()
    }

    /**
     * Reset all player settings to default.
     */
    fun resetPlayerSettings(playerId: Int) {
        savePlayerSettings(playerId, PlayerSettings()) // Reset to default
    }

    // --- Versioning for Future-Proofing ---

    /**
     * Save the current settings version.
     */
    fun setVersion(version: Int) {
        editor.putInt(VERSION_KEY, version).apply()
    }

    /**
     * Get the current settings version, default to 1.
     */
    fun getVersion(): Int {
        return sharedPreferences.getInt(VERSION_KEY, 1)
    }

    // --- Clearing or Resetting Settings ---

    /**
     * Clear a specific setting by key.
     */
    fun clearSetting(key: String) {
        editor.remove(key).apply()
    }

    /**
     * Clear all settings.
     */
    fun clearAllSettings() {
        editor.clear().apply()
    }

    // --- Default Settings for New Features or Resets ---

    /**
     * Apply default settings when initializing or resetting the app.
     */
    fun applyDefaultSettings() {
        if (getVersion() < 2) {
            saveBoolean("SOUND_ENABLED", true)
            saveString("DIFFICULTY_LEVEL", "MEDIUM")
            saveString("THEME", "LIGHT")
            saveInt("NEW_FEATURE_TOGGLE", 1)  // Placeholder for a new feature toggle
            setVersion(2)
        }
    }
}
