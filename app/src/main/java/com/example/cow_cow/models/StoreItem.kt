package com.example.cow_cow.models

import com.example.cow_cow.enums.CurrencyType
import com.example.cow_cow.enums.ItemRarity

data class StoreItem(
    val id: Int,                                 // Unique ID for the item
    val name: String,                            // Name of the store item
    val description: String,                     // Description of the item
    val price: Int,                              // Base price of the item
    val currencyType: CurrencyType,              // Currency used for purchase (Coins, Gems, etc.)
    val category: String,                        // Category (e.g., "Power-Up", "Skin", "Utility")
    val rarity: ItemRarity,                      // Rarity of the item (Common, Rare, etc.)
    val isLimitedTimeOffer: Boolean = false,     // Flag for limited-time offer items
    val discount: Int = 0,                       // Discount percentage (0 if no discount)
    val isPurchasable: Boolean = true            // Flag to disable purchase (e.g., out of stock, unavailable)
) {
    // Function to calculate the final price after applying any discount
    fun getFinalPrice(): Int {
        return if (discount > 0) {
            (price * (1 - (discount / 100.0))).toInt()
        } else {
            price
        }
    }
}
