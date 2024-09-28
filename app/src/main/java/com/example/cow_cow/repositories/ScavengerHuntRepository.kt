package com.example.cow_cow.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.cow_cow.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScavengerHuntRepository {

    private val PREFS_NAME = "com.example.cow_cow.PREFERENCES"
    private val SCAVENGER_HUNT_KEY = "SCAVENGER_HUNT_KEY"
    private val gson = Gson()

    // LiveData for scavenger hunt items
    private val _scavengerHuntItems = MutableLiveData<List<ScavengerHuntItem>>()
    val scavengerHuntItems: LiveData<List<ScavengerHuntItem>> get() = _scavengerHuntItems

    // Retrieve scavenger hunt items and set LiveData
    fun loadScavengerHuntItems(context: Context) {
        val items = getScavengerHuntItems(context)
        _scavengerHuntItems.value = items
    }

    // Get SharedPreferences instance
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Retrieve scavenger hunt items from local storage (SharedPreferences)
    fun getScavengerHuntItems(context: Context): List<ScavengerHuntItem> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(SCAVENGER_HUNT_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<ScavengerHuntItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            // If no data is found, return the default hardcoded items
            getDefaultScavengerHuntItems()
        }
    }

    // Modify the save function to update LiveData as well
    fun saveScavengerHuntItems(items: List<ScavengerHuntItem>, context: Context) {
        // Save to SharedPreferences as before
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = gson.toJson(items)
        editor.putString(SCAVENGER_HUNT_KEY, json)
        editor.apply()

        // Update LiveData so observers are notified
        _scavengerHuntItems.value = items
    }

    fun getFilteredScavengerHuntItems(tags: List<String>, context: Context): List<ScavengerHuntItem> {
        return getScavengerHuntItems(context).filter { item ->
            tags.any { tag -> item.tags.contains(tag) }
        }
    }

    // Retrieve the default scavenger hunt items (hardcoded list)
    private fun getDefaultScavengerHuntItems(): List<ScavengerHuntItem> {
        return listOf(
            // CITY - General
            ScavengerHuntItem(
                name = "Find a red car",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "See a yellow taxi",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.ADULTS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Spot a street performer",
                difficultyLevel = DifficultyLevel.HARD,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.SUMMER
            ),

            // PARK
            ScavengerHuntItem(
                name = "Spot a squirrel",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.PARK,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Find a bird's nest",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.PARK,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.SPRING
            ),
            ScavengerHuntItem(
                name = "See a group of joggers",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.PARK,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.ADULTS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),

            // ROADTRIP
            ScavengerHuntItem(
                name = "Spot a white fence",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.ROADTRIP,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Find a barn",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.ROADTRIP,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Spot a tractor",
                difficultyLevel = DifficultyLevel.HARD,
                locationType = LocationType.ROADTRIP,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.SUMMER
            ),

            // RESTAURANT
            ScavengerHuntItem(
                name = "Find a restaurant with outdoor seating",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.RESTAURANT,
                timeOfDay = TimeOfDay.EVENING,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Spot a food truck",
                difficultyLevel = DifficultyLevel.HARD,
                locationType = LocationType.RESTAURANT,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.ADULTS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.SUMMER
            ),
            ScavengerHuntItem(
                name = "Find a restaurant with a kids' play area",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.RESTAURANT,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),

            // BEACH
            ScavengerHuntItem(
                name = "Find a seashell",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.BEACH,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.SUMMER
            ),
            ScavengerHuntItem(
                name = "Spot a kite",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.BEACH,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.WINDY,
                specialOccasion = null,
                season = Season.SUMMER
            ),
            ScavengerHuntItem(
                name = "See a lifeguard tower",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.BEACH,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),

            // ZOO
            ScavengerHuntItem(
                name = "Spot a person wearing a hat",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.ZOO,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "Find an animal sleeping",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.ZOO,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),
            ScavengerHuntItem(
                name = "See a monkey swinging",
                difficultyLevel = DifficultyLevel.HARD,
                locationType = LocationType.ZOO,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = null,
                season = Season.ALL
            ),

            // WEATHER-BASED
            ScavengerHuntItem(
                name = "Find a puddle",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.PARK,
                timeOfDay = TimeOfDay.MORNING,
                ageGroup = AgeGroup.KIDS,
                weather = WeatherCondition.RAINY,
                specialOccasion = null,
                season = Season.SPRING
            ),
            ScavengerHuntItem(
                name = "Spot someone with an umbrella",
                difficultyLevel = DifficultyLevel.MEDIUM,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.AFTERNOON,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.RAINY,
                specialOccasion = null,
                season = Season.ALL
            ),

            // SPECIAL OCCASIONS
            ScavengerHuntItem(
                name = "Spot someone in a Halloween costume",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.EVENING,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = SpecialOccasion.HALLOWEEN,
                season = Season.FALL
            ),
            ScavengerHuntItem(
                name = "Find a Christmas decoration",
                difficultyLevel = DifficultyLevel.EASY,
                locationType = LocationType.CITY,
                timeOfDay = TimeOfDay.EVENING,
                ageGroup = AgeGroup.BOTH,
                weather = WeatherCondition.CLEAR,
                specialOccasion = SpecialOccasion.CHRISTMAS,
                season = Season.WINTER
            )
        )
    }

    // Method to update the list with OTA data (via API or push notification)
    fun updateScavengerHuntItemsFromServer(items: List<ScavengerHuntItem>, context: Context) {
        // Example of receiving new items from server and saving to local storage
        saveScavengerHuntItems(items, context)
    }
}
