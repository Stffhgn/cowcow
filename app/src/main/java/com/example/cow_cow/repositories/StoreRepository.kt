package com.example.cow_cow.repositories

import android.content.Context
import com.example.cow_cow.models.StoreItem
import com.example.cow_cow.models.CurrencyType
import com.example.cow_cow.models.Player

class StoreRepository(private val context: Context) {

    // --- Currency Management ---
    private val playerCoins: Int = 1000   // Default coins
    private val playerGems: Int = 50      // Default gems
    private val eventTokens: Int = 10     // Event-specific tokens

    // --- Store Items with Different Rarities ---
    private val storeItems = mutableListOf(
        StoreItem("Power-Up", 100, CurrencyType.COINS, "Common"),
        StoreItem("Rare Power-Up", 300, CurrencyType.GEMS, "Rare"),
        StoreItem("Legendary Cow Skin", 500, CurrencyType.GEMS, "Legendary"),
        StoreItem("Seasonal Event Token", 50, CurrencyType.EVENT_TOKENS, "Event"),
        StoreItem("Mystery Loot Box", 250, CurrencyType.COINS, "Mystery"),
        StoreItem("Extra Lives", 150, CurrencyType.COINS, "Common"),
        StoreItem("Upgrade: Fast Speed", 200, CurrencyType.COINS, "Upgradable")
    )

    /**
     * Retrieve the list of store items available to the player.
     * You can filter based on rarity, events, etc.
     */
    fun getStoreItems(player: Player): List<StoreItem> {
        // Example: If the player has completed certain achievements, unlock special items
        val unlockedItems = mutableListOf<StoreItem>().apply {
            addAll(storeItems)
            if (player.hasSpecialAchievement()) {
                add(StoreItem("Special Edition Cow Skin", 800, CurrencyType.GEMS, "Legendary"))
            }
        }

        // Apply discounts based on events, player level, etc.
        return applyDynamicPricing(unlockedItems, player)
    }

    /**
     * Purchase an item from the store.
     */
    fun purchaseItem(item: StoreItem, player: Player): Boolean {
        return when (item.currencyType) {
            CurrencyType.COINS -> {
                if (playerCoins >= item.price) {
                    deductCurrency(item.currencyType, item.price)
                    true
                } else false
            }
            CurrencyType.GEMS -> {
                if (playerGems >= item.price) {
                    deductCurrency(item.currencyType, item.price)
                    true
                } else false
            }
            CurrencyType.EVENT_TOKENS -> {
                if (eventTokens >= item.price) {
                    deductCurrency(item.currencyType, item.price)
                    true
                } else false
            }
        }
    }

    /**
     * Deduct the corresponding currency based on the purchase.
     */
    private fun deductCurrency(currencyType: CurrencyType, amount: Int) {
        when (currencyType) {
            CurrencyType.COINS -> updateCoins(-amount)
            CurrencyType.GEMS -> updateGems(-amount)
            CurrencyType.EVENT_TOKENS -> updateEventTokens(-amount)
        }
    }

    /**
     * Apply dynamic pricing for seasonal events, player level, or other factors.
     */
    private fun applyDynamicPricing(items: List<StoreItem>, player: Player): List<StoreItem> {
        return items.map { item ->
            val priceAdjustment = when {
                isHolidaySeason() -> 0.9 // 10% discount during holidays
                player.isVIP() -> 0.85   // 15% discount for VIP players
                else -> 1.0
            }
            item.copy(price = (item.price * priceAdjustment).toInt())
        }
    }

    // --- Helper Methods for Player Currency and Store Updates ---

    private fun updateCoins(amount: Int) {
        // Logic to update player coins
    }

    private fun updateGems(amount: Int) {
        // Logic to update player gems
    }

    private fun updateEventTokens(amount: Int) {
        // Logic to update player event tokens
    }

    private fun isHolidaySeason(): Boolean {
        // Logic to check if it's a special event or holiday (e.g., Christmas, Halloween)
        return false
    }

    // This is just a placeholder to demonstrate
    private fun Player.hasSpecialAchievement(): Boolean {
        // Check player achievements for unlocking special items
        return false
    }

    private fun Player.isVIP(): Boolean {
        // Check if player is a VIP, maybe from a premium subscription or level
        return false
    }
}
