package com.example.cow_cow.utils

import android.content.Context
import com.example.cow_cow.models.ScavengerHuntItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScavengerHuntUtils {

    private const val SCAVENGER_HUNT_PREFS = "scavenger_hunt_prefs"
    private const val SCAVENGER_HUNT_KEY = "scavenger_hunt_key"

    private val gson = Gson()

    /**
     * Saves the scavenger hunt data to SharedPreferences.
     */
    fun saveScavengerHunt(context: Context, scavengerHuntList: List<ScavengerHuntItem>) {
        val sharedPreferences = context.getSharedPreferences(SCAVENGER_HUNT_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val scavengerHuntJson = gson.toJson(scavengerHuntList)
        editor.putString(SCAVENGER_HUNT_KEY, scavengerHuntJson)
        editor.apply()
    }

    /**
     * Loads the scavenger hunt data from SharedPreferences.
     */
    fun loadScavengerHunt(context: Context): List<ScavengerHuntItem> {
        val sharedPreferences = context.getSharedPreferences(SCAVENGER_HUNT_PREFS, Context.MODE_PRIVATE)
        val scavengerHuntJson = sharedPreferences.getString(SCAVENGER_HUNT_KEY, null)
        return if (scavengerHuntJson != null) {
            val type = object : TypeToken<List<ScavengerHuntItem>>() {}.type
            gson.fromJson(scavengerHuntJson, type)
        } else {
            listOf()
        }
    }

    /**
     * Clears the scavenger hunt data.
     */
    fun clearScavengerHunt(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SCAVENGER_HUNT_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(SCAVENGER_HUNT_KEY)
        editor.apply()
    }
}
