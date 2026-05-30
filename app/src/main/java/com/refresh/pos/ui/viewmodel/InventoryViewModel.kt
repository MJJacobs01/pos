package com.refresh.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.pos.data.repository.DemoDataProvider
import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val demoDataProvider: DemoDataProvider
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    val products: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) productRepository.getAllProducts()
            else productRepository.searchProducts(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<InventoryUiState> = combine(
        products, _searchQuery, _isLoading, _message
    ) { products, query, loading, message ->
        InventoryUiState(
            products = products,
            searchQuery = query,
            isLoading = loading,
            message = message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryUiState())

    fun onSearchQueryChange(query: String) {
        when (query.trim()) {
            "/demo" -> seedDemo()
            "/clear" -> onClearRequested()
            else -> _searchQuery.value = query
        }
    }

    fun addProduct(name: String, barcode: String, price: Double) {
        viewModelScope.launch {
            productRepository.addProduct(name, barcode, price)
        }
    }

    fun suspendProduct(product: Product) {
        viewModelScope.launch {
            productRepository.suspendProduct(product)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            productRepository.clearAll()
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun onBarcodeScanned(barcode: String) {
        _searchQuery.value = barcode
    }

    private fun seedDemo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                demoDataProvider.seedDemoProducts()
                _message.value = "Demo data seeded"
            } catch (e: Exception) {
                _message.value = "Failed to seed demo data"
            }
            _isLoading.value = false
            _searchQuery.value = ""
        }
    }

    private fun onClearRequested() {
        _searchQuery.value = ""
        viewModelScope.launch {
            productRepository.clearAll()
            _message.value = "All data cleared"
        }
    }
}
