package com.refresh.pos.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.domain.DateTimeUtils
import com.refresh.pos.domain.model.Product
import com.refresh.pos.domain.model.ProductLot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val stockSum: Int = 0,
    val lots: List<ProductLot> = emptyList(),
    val isEditing: Boolean = false,
    val editName: String = "",
    val editBarcode: String = "",
    val editPrice: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {

    val productId: Long = savedStateHandle.get<Long>("productId") ?: 0L

    private val _isEditing = MutableStateFlow(false)
    private val _editName = MutableStateFlow("")
    private val _editBarcode = MutableStateFlow("")
    private val _editPrice = MutableStateFlow("")

    val uiState: StateFlow<ProductDetailUiState> = combine(
        productRepository.getProductById(productId),
        productRepository.getStockSum(productId),
        productRepository.getLotsByProductId(productId)
    ) { product, stockSum, lots ->
        Triple(product, stockSum, lots)
    }.combine(combine(_isEditing, _editName, _editBarcode, _editPrice) { e, n, b, p ->
        listOf(e, n, b, p)
    }) { (product, stockSum, lots), edits ->
        ProductDetailUiState(
            product = product,
            stockSum = stockSum,
            lots = lots,
            isEditing = edits[0] as Boolean,
            editName = edits[1] as String,
            editBarcode = edits[2] as String,
            editPrice = edits[3] as String
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductDetailUiState())

    fun startEditing(product: Product) {
        _editName.value = product.name
        _editBarcode.value = product.barcode
        _editPrice.value = product.unitPrice.toString()
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
    }

    fun saveEditing() {
        viewModelScope.launch {
            val product = uiState.value.product ?: return@launch
            val price = _editPrice.value.toDoubleOrNull() ?: return@launch
            productRepository.editProduct(
                product.copy(
                    name = _editName.value,
                    barcode = _editBarcode.value,
                    unitPrice = price
                )
            )
            _isEditing.value = false
        }
    }

    fun onEditNameChange(name: String) { _editName.value = name }
    fun onEditBarcodeChange(barcode: String) { _editBarcode.value = barcode }
    fun onEditPriceChange(price: String) { _editPrice.value = price }

    fun addLot(quantity: Int, cost: Double) {
        viewModelScope.launch {
            productRepository.addProductLot(
                productId = productId,
                date = DateTimeUtils.getCurrentTime(),
                quantity = quantity,
                cost = cost
            )
        }
    }
}
