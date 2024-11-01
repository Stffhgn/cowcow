package com.example.cow_cow.utils

import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.enums.DifficultyLevel
import com.example.cow_cow.enums.LocationType

object ScavengerHuntItemGenerator {

    // Grouped items by difficulty based on best judgment
    private val easyItems = listOf(
        "Stop Sign", "Bus Stop", "Bicycle", "Bird", "Birdhouse", "MailBox", "Billboard",
        "Fence", "Fire Hydrant", "Park Bench", "Speed Limit Sign"
    )

    private val mediumItems = listOf(
        "Barn", "Beach", "Bridge", "Clock Tower", "Construction Zone", "Deer", "Dirt Road",
        "Dog", "Farm Animal", "Golf Course", "Greenhouse", "Horse", "Library", "Mountain",
        "Police Car", "School Zone", "Shopping Center", "Solar Panels", "Statue", "Tractor",
        "Traffic Light", "Train Station", "Zoo Sign"
    )

    private val hardItems = listOf(
        "Abandoned Building", "Airport Sign", "Antique Car", "Bald Eagle", "Carnival or Fair",
        "City Skyline", "Cemetery", "Flock of Birds", "Grain Silo", "Helicopter or Plane",
        "Mountain Biker", "Moose", "Nature Reserve Sign", "Orchard", "Scarecrow", "Statue of Historical Figure",
        "Sunflower Field", "Surfboard", "Tow Truck", "Treehouse", "Tunnel", "Vineyard", "Water Tower",
        "Wind Turbine", "Windmill", "Welcome Center"
    )

    /**
     * Generates a list of all scavenger hunt items with assigned difficulty levels.
     *
     * @return A list of ScavengerHuntItem instances, sorted by difficulty level.
     */
    fun generateAllItems(): List<ScavengerHuntItem> {
        val allItems = mutableListOf<ScavengerHuntItem>()

        allItems.addAll(easyItems.map { itemName ->
            ScavengerHuntItem(
                name = itemName,
                difficultyLevel = DifficultyLevel.EASY,
                isFound = false,
                isActive = false,
                tags = listOf("outdoors", "exploration")
            )
        })

        allItems.addAll(mediumItems.map { itemName ->
            ScavengerHuntItem(
                name = itemName,
                difficultyLevel = DifficultyLevel.MEDIUM,
                isFound = false,
                isActive = false,
                tags = listOf("outdoors", "exploration")
            )
        })

        allItems.addAll(hardItems.map { itemName ->
            ScavengerHuntItem(
                name = itemName,
                difficultyLevel = DifficultyLevel.HARD,
                isFound = false,
                isActive = false,
                tags = listOf("outdoors", "exploration", "rare")
            )
        })

        return allItems
    }
}
