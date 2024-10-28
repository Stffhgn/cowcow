package com.example.cow_cow.utils

import com.example.cow_cow.enums.*
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.models.Player
import java.util.Calendar
import kotlin.random.Random

object ScavengerHuntItemGenerator {

    /**
     * Generates a dynamic scavenger hunt item based on the player's status, time of day, season, and more.
     *
     * @param player The player for whom the item is being generated.
     * @return A generated ScavengerHuntItem.
     */
    fun generateScavengerHuntItem(player: Player): ScavengerHuntItem {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)
        val season = getSeason(month)
        val playerStreak = player.cowCount // Number of cows called
        val recentActivity = player.waterTowerCount // Water towers called

        // Contextual item list based on common sightings and conditions
        val possibleItems = listOf(
            "Dog" to "Spot a dog nearby.",
            "Blue House" to "Find a house painted blue.",
            "Deer" to "See if you can spot a deer.",
            "Stop Sign" to "Locate a stop sign.",
            "Traffic Light" to "Find a traffic light.",
            "Construction Zone" to "Spot a construction zone or road work area.",
            "Playground" to "Find a playground area.",
            "Park Bench" to "Spot a park bench.",
            "Bird" to "See if you can spot a bird in the area.",
            "Restaurant" to "Locate a nearby restaurant.",
            "School Zone" to "Find a school zone sign or area.",
            "Bicycle" to "Look for a bicycle."
        )

        // Enhance contextual choices with conditions based on time, season, and player activity
        val (itemName, itemDescription) = when {
            hour in 6..8 -> "Dog" to "Morning dog walkers! Spot a dog on a morning walk."
            hour in 12..13 -> "Restaurant" to "Lunchtime! Find a place where people might be eating."
            hour in 15..17 && season == Season.SUMMER -> "Traffic Light" to "Busy streets this afternoon! Find a traffic light."
            hour in 18..20 && playerStreak >= 5 -> "Blue House" to "On a streak! Spot a blue house as you continue."
            dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY -> "Playground" to "Weekend fun! Find a playground area."
            playerStreak >= 10 -> "Park Bench" to "Taking a break? Spot a park bench to rest on."
            Random.nextInt(100) < 20 -> possibleItems.random() // Randomly select an item with a 20% chance
            else -> "Explorer’s Choice" to "Find anything interesting around you and share your discovery!"
        }

        // Randomly assign a difficulty level, skewed by player activity
        val difficultyLevel = if (playerStreak > 10) DifficultyLevel.HARD else DifficultyLevel.MEDIUM

        // Generate location type based on season
        val locationType = getRandomLocationType(season)

        // Define other parameters with mock values or defaults for now
        val timeOfDay = TimeOfDay.getCurrentTimeOfDay(hour)
        val ageGroup = AgeGroup.ALL_AGES
        val weatherCondition: WeatherCondition? = null
        val specialOccasion: SpecialOccasion? = null
        val tags = listOf("outdoors", "exploration", "nature")

        // Return the newly created scavenger hunt item
        return ScavengerHuntItem(
            name = itemName,
            description = itemDescription,
            difficultyLevel = difficultyLevel,
            locationType = locationType,
            timeOfDay = timeOfDay,
            ageGroup = ageGroup,
            weather = weatherCondition,
            specialOccasion = specialOccasion,
            season = season,
            tags = tags,
            isPremium = false,
            isFree = true,
            isPurchased = false
        )
    }

    /**
     * Get the current season based on the month.
     */
    private fun getSeason(month: Int): Season {
        return when (month) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> Season.WINTER
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Season.SPRING
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> Season.SUMMER
            Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER -> Season.FALL
            else -> Season.ALL // Default to ALL if the month is somehow out of range.
        }
    }

    /**
     * Get a random location type based on the current season.
     */
    private fun getRandomLocationType(season: Season): LocationType {
        val locationOptions = when (season) {
            Season.SPRING -> listOf(LocationType.FOREST, LocationType.PARK, LocationType.NATIONAL_PARK)
            Season.SUMMER -> listOf(LocationType.BEACH, LocationType.WATERPARK, LocationType.CITY)
            Season.FALL -> listOf(LocationType.FARM, LocationType.HISTORICAL_SITE, LocationType.PARK)
            Season.WINTER -> listOf(LocationType.MUSEUM, LocationType.CITY, LocationType.SUBURBAN)
            Season.ALL -> LocationType.values().toList() // Use all available location types as a fallback.
        }
        return locationOptions.random()
    }
}
