package com.example.cow_cow.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.models.StoreItem
import com.example.cow_cow.repositories.StoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreViewModel(application: Application, private val repository: StoreRepository) :
    AndroidViewModel(application) {

    // LiveData for the list of store items
    private val _storeItems = MutableLiveData<List<StoreItem>>()
    val storeItems: LiveData<List<StoreItem>> get() = _storeItems

    // LiveData for purchase results (success or failure)
    private val _purchaseResult = MutableLiveData<PurchaseResult>()
    val purchaseResult: LiveData<PurchaseResult> get() = _purchaseResult

    // LiveData for errors (e.g., insufficient funds)
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadStoreItems()
    }

    /**
     * Load store items from the repository.
     */
    private fun loadStoreItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repository.getStoreItems()
                _storeItems.postValue(items)
            } catch (e: Exception) {
                // Handle any loading errors (e.g., network issues, file loading issues)
                _error.postValue("Error loading store items: ${e.message}")
            }
        }
    }

    /**
     * Attempt to purchase an item from the store.
     */
    fun purchaseItem(item: StoreItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.purchaseItem(item)
                if (result) {
                    _purchaseResult.postValue(PurchaseResult.Success(item))
                } else {
                    _purchaseResult.postValue(PurchaseResult.Failure("Purchase failed for ${item.name}"))
                }
            } catch (e: Exception) {
                // Handle purchase exceptions (e.g., insufficient funds, item unavailable)
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
