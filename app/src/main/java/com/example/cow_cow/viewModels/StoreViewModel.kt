package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.StoreItem
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreViewModel(
    application: Application,
    private val storeRepository: StoreRepository,
    private val playerRepository: PlayerRepository  // Inject the PlayerRepository to access the global player
) : AndroidViewModel(application) {


    // LiveData for the list of store items
    private val _storeItems = MutableLiveData<List<StoreItem>>()
    val storeItems: LiveData<List<StoreItem>> get() = _storeItems

    // LiveData for purchase results (success or failure)
    private val _purchaseResult = MutableLiveData<PurchaseResult>()
    val purchaseResult: LiveData<PurchaseResult> get() = _purchaseResult

    // LiveData for errors (e.g., insufficient funds)
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Status and error messages for load operation
    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> get() = _statusMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        loadStoreItems()
    }

    /**
     * Load store items from the repository.
     */
    /**
     * Load store items for the current player from the repository.
     */
    fun loadStoreItems() {
        viewModelScope.launch {
            try {
                // Fetch the current player from the PlayerRepository
                val player = withContext(Dispatchers.IO) {
                    playerRepository.getCurrentPlayer()  // Get the global player
                }
                if (player != null) {
                    val items = withContext(Dispatchers.IO) {
                        storeRepository.getStoreItems(player)  // Pass the player to load their specific store items
                    }
                    _storeItems.value = items
                    _purchaseResult.value = null  // Clear previous results
                } else {
                    _error.value = "No player found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load store items: ${e.message}"
            }
        }
    }


    /**
     * Attempt to purchase an item from the store for the current player.
     */
    fun purchaseItem(item: StoreItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val player = playerRepository.getCurrentPlayer()
                if (player != null) {
                    val result = storeRepository.purchaseItem(item, player)
                    if (result) {
                        _purchaseResult.postValue(PurchaseResult.Success(item))
                    } else {
                        _purchaseResult.postValue(PurchaseResult.Failure("Purchase failed for ${item.name}"))
                    }
                } else {
                    _error.postValue("No player found to make a purchase")
                }
            } catch (e: Exception) {
                _purchaseResult.postValue(PurchaseResult.Failure("Error during purchase: ${e.message}"))
            }
        }
    }


    /**
     * Clear any purchase-related errors or results.
     */
    fun clearPurchaseResult() {
        _purchaseResult.value = null
        _error.value = null
    }
}

/**
 * A sealed class for representing the result of a purchase attempt.
 */
sealed class PurchaseResult {
    data class Success(val item: StoreItem) : PurchaseResult()
    data class Failure(val message: String) : PurchaseResult()
}
