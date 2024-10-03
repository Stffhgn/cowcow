package com.example.cow_cow.repositories

import android.content.Context
import android.util.Log
import com.example.cow_cow.models.StoreItem
import com.example.cow_cow.enums.CurrencyType
import com.example.cow_cow.enums.ItemRarity
import com.example.cow_cow.models.Player

class StoreRepository(private val context: Context) {

    private val TAG = "StoreRepository"

    // --- Currency Management ---
    private var playerCoins: Int = 1000   // Default coins
    private var playerGems: Int = 50      // Default gems
    private var eventTokens: Int = 10     // Event-specific tokens

    // --- Store Items with Different Rarities ---
    private val storeItems = mutableListOf(
        StoreItem(1, "Power-Up", "Grants extra power", 100, CurrencyType.COINS, "Power-Up", ItemRarity.COMMON),
        StoreItem(2, "Rare Power-Up", "Special power boost", 300, CurrencyType.GEMS, "Power-Up", ItemRarity.RARE),
        StoreItem(3, "Legendary Cow Skin", "Legendary skin for your cow", 500, CurrencyType.GEMS, "Skin", ItemRarity.LEGENDARY),
        StoreItem(4, "Seasonal Event Token", "Token for event usage", 50, CurrencyType.EVENT_TOKENS, "Event", ItemRarity.COMMON),
        StoreItem(5, "Mystery Loot Box", "Contains a random item", 250, CurrencyType.COINS, "Mystery", ItemRarity.RARE),
        StoreItem(6, "Extra Lives", "Gives additional lives", 150, CurrencyType.COINS, "Utility", ItemRarity.COMMON),
        StoreItem(7, "Upgrade: Fast Speed", "Increases speed", 200, CurrencyType.COINS, "Utility", ItemRarity.LEGENDARY)
    )

    /**
     * Retrieve the list of store items available to the player.
     * You can filter based on rarity, events, etc.
     */
    fun getStoreItems(player: Player): List<StoreItem> {
        Log.d(TAG, "Fetching store items for player: ${player.name}")

        // Example: If the player has completed certain achievements, unlock special items
        val unlockedItems = mutableListOf<StoreItem>().apply {
            addAll(storeItems)
            if (player.hasSpecialAchievement()) {
                Log.d(TAG, "Player ${player.name} has a special achievement, unlocking special item.")
                add(StoreItem(8, "Special Edition Cow Skin", "Exclusive skin", 800, CurrencyType.GEMS, "Skin", ItemRarity.LEGENDARY))
            }
        }

        // Apply dynamic pricing based on events or player status
        return applyDynamicPricing(unlockedItems, player)
    }

    /**
     * Purchase an item from the store.
     */
    fun purchaseItem(item: StoreItem, player: Player): Boolean {
        Log.d(TAG, "Attempting to purchase item: ${item.name} for player: ${player.name}")

        return when (item.currencyType) {
            CurrencyType.COINS -> {
                if (playerCoins >= item.getFinalPrice()) {
                    deductCurrency(item.currencyType, item.getFinalPrice())
                    Log.d(TAG, "Purchased ${item.name} using COINS. Remaining coins: $playerCoins")
                    true
                } else {
                    Log.d(TAG, "Insufficient COINS for player: ${player.name}")
                    false
                }
            }
            CurrencyType.GEMS -> {
                if (playerGems >= item.getFinalPrice()) {
                    deductCurrency(item.currencyType, item.getFinalPrice())
                    Log.d(TAG, "Purchased ${item.name} using GEMS. Remaining gems: $playerGems")
                    true
                } else {
                    Log.d(TAG, "Insufficient GEMS for player: ${player.name}")
                    false
                }
            }
            CurrencyType.EVENT_TOKENS -> {
                if (eventTokens >= item.getFinalPrice()) {
                    deductCurrency(item.currencyType, item.getFinalPrice())
                    Log.d(TAG, "Purchased ${item.name} using EVENT TOKENS. Remaining tokens: $eventTokens")
                    true
                } else {
                    Log.d(TAG, "Insufficient EVENT TOKENS for player: ${player.name}")
                    false
                }
            }
            else -> false
        }
    }

    /**
     * Deduct the corresponding currency based on the purchase.
     */
    private fun deductCurrency(currencyType: CurrencyType, amount: Int) {
        when (currencyType) {
            CurrencyType.COINS -> {
                updateCoins(-amount)
                Log.d(TAG, "Deducted $amount COINS. New balance: $playerCoins")
            }
            CurrencyType.GEMS -> {
                updateGems(-amount)
                Log.d(TAG, "Deducted $amount GEMS. New balance: $playerGems")
            }
            CurrencyType.EVENT_TOKENS -> {
                updateEventTokens(-amount)
                Log.d(TAG, "Deducted $amount EVENT TOKENS. New balance: $eventTokens")
            }
            CurrencyType.TICKETS -> {
                // Handle logic for tickets if necessary (e.g., reduce ticket count)
                Log.d(TAG, "Deducted $amount TICKETS. Ticket system to be implemented.")
            }
            CurrencyType.REAL_MONEY -> {
                // Handle real money transactions (e.g., log the purchase or redirect to the payment gateway)
                Log.d(TAG, "Real money purchase of $amount made. Further implementation required for processing payments.")
            }
            else -> {
                Log.e(TAG, "Unknown currency type: $currencyType. Unable to deduct $amount.")
            }
        }
    }


    /**
     * Apply dynamic pricing for seasonal events, player level, or other factors.
     */
    private fun applyDynamicPricing(items: List<StoreItem>, player: Player): List<StoreItem> {
        Log.d(TAG, "Applying dynamic pricing for player: ${player.name}")

        return items.map { item ->
            val priceAdjustment = when {
                isHolidaySeason() -> {
                    Log.d(TAG, "Holiday season discount applied.")
                    0.9 // 10% discount during holidays
                }
                player.isVIP() -> {
                    Log.d(TAG, "VIP player discount applied.")
                    0.85   // 15% discount for VIP players
                }
                else -> 1.0
            }
            item.copy(price = (item.price * priceAdjustment).toInt())
        }
    }

    // --- Helper Methods for Player Currency and Store Updates ---

    private fun updateCoins(amount: Int) {
        playerCoins += amount
        Log.d(TAG, "Updated player coins: $playerCoins")
    }

    private fun updateGems(amount: Int) {
        playerGems += amount
        Log.d(TAG, "Updated player gems: $playerGems")
    }

    private fun updateEventTokens(amount: Int) {
        eventTokens += amount
        Log.d(TAG, "Updated event tokens: $eventTokens")
    }

    private fun isHolidaySeason(): Boolean {
        val isHoliday = false // Placeholder logic
        Log.d(TAG, "Holiday season check: $isHoliday")
        return isHoliday
    }

    private fun Player.hasSpecialAchievement(): Boolean {
        val hasAchievement = false // Placeholder logic
        Log.d(TAG, "Player ${this.name} has special achievement: $hasAchievement")
        return hasAchievement
    }

    private fun Player.isVIP(): Boolean {
        val isVIP = false // Placeholder logic
        Log.d(TAG, "Player ${this.name} is VIP: $isVIP")
        return isVIP
    }
}
