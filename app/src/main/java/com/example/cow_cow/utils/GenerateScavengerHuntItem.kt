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
        val playerStreak = player.cowCount // Represents the number of cows called
        val recentActivity = player.waterTowerCount // Represents the number of water towers called

        // Choose an item name and description based on conditions
        val (itemName, itemDescription) = when {
            hour in 6..8 -> "Morning Dew Drop" to "Find something that sparkles like morning dew."
            hour in 9..11 && recentActivity > 10 -> "Fitness Frenzy" to "You’ve been active! Spot a place where you can stretch your legs."
            hour in 12..13 -> "Lunchtime Explorer" to "Find a spot where people might be enjoying a midday break."
            hour in 14..17 -> "Sunshine Seeker" to "It’s bright out! Find something that catches the sunlight."
            hour in 18..20 && playerStreak >= 5 -> "Streak Seeker" to "You’re on a roll! Spot something that rolls, like a tire or a ball."
            hour in 22..5 && Random.nextBoolean() -> "Night Owl Quest" to "A rare night quest! Find something mysterious that only appears in the dark."
            season == Season.SPRING && recentActivity < 5 -> "Spring Awakening" to "You’ve been quiet lately! Find a blooming flower or a sign of new life."
            season == Season.SUMMER -> "Heat Wave Challenge" to "Stay cool! Spot a shady area or a source of water."
            season == Season.FALL && playerStreak > 3 -> "Harvest Hunt" to "You’re great at spotting cows! Now find something related to a harvest."
            season == Season.WINTER && Random.nextInt(100) < 10 -> "Rare Frostbite" to "Rare winter challenge! Find something cold or frosty."
            dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY -> "Weekend Wanderer" to "Weekends are for exploring! Find a new trail or path."
            playerStreak >= 10 -> "Loyal Explorer" to "Thanks for being consistent in spotting cows! Find something unique in your surroundings."
            else -> "Explorer’s Choice" to "Find anything interesting around you and share your discovery!"
        }


        // Get a random location type based on the current season.
        val locationType = getRandomLocationType(season)

        // Define other parameters with mock values or defaults for now.
        val difficultyLevel = DifficultyLevel.MEDIUM
        val timeOfDay = TimeOfDay.getCurrentTimeOfDay(hour)
        val ageGroup = AgeGroup.ALL_AGES
        val weatherCondition: WeatherCondition? = null
        val specialOccasion: SpecialOccasion? = null
        val tags = listOf("exploration")

        // Create and return the scavenger hunt item.
        return ScavengerHuntItem(
            name = itemName,
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
     * Generates a unique identifier for scavenger hunt items.
     */
    private fun generateUniqueId(): String {
        return "SH-${Random.nextInt(1000, 9999)}"
    }

    /**
     * Get a random location type based on the current season.
     */
    private fun getRandomLocationType(season: Season): LocationType {
        val locationOptions = when (season) {
            Season.SPRING -> listOf(LocationType.FOREST, LocationType.PARK, LocationType.NATIONAL_PARK)
            Season.SUMMER -> listOf(LocationType.BEACH, LocationType.WATERPARK, LocationType.MOUNTAIN)
            Season.FALL -> listOf(LocationType.FARM, LocationType.PARK, LocationType.HISTORICAL_SITE)
            Season.WINTER -> listOf(LocationType.MUSEUM, LocationType.CITY, LocationType.SUBURBAN)
            Season.ALL -> LocationType.values().toList() // Use all available location types as a fallback.
        }
        return locationOptions.random()
    }
}
