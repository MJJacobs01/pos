package com.refresh.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.data.repository.SaleRepository
import com.refresh.pos.domain.DateTimeUtils
import com.refresh.pos.domain.model.LineItem
import com.refresh.pos.domain.model.SaleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaleUiState(
    val currentSaleId: Long? = null,
    val lineItems: List<LineItem> = emptyList(),
    val showPaymentSheet: Boolean = false,
    val showClearDialog: Boolean = false,
    val editingLineItem: LineItem? = null
) {
    val total: Double get() = lineItems.sumOf { it.totalPrice }
    val isEmpty: Boolean get() = lineItems.isEmpty()
}

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaleUiState())
    val uiState: StateFlow<SaleUiState> = _uiState.asStateFlow()

    fun addItem(productId: Long, quantity: Int) {
        viewModelScope.launch {
            val currentProduct = productRepository.getProductById(productId).first()
                ?: return@launch

            var currentSaleId = _uiState.value.currentSaleId
            if (currentSaleId == null) {
                val sale = saleRepository.initiateSale(DateTimeUtils.getCurrentTime())
                currentSaleId = sale.id
            }

            val lineItem = saleRepository.addLineItem(
                saleId = currentSaleId,
                productId = productId,
                quantity = quantity,
                unitPrice = currentProduct.unitPrice
            )

            _uiState.update { state ->
                state.copy(
                    currentSaleId = currentSaleId,
                    lineItems = state.lineItems + lineItem
                )
            }
        }
    }

    fun updateLineItem(item: LineItem, quantity: Int, priceAtSale: Double) {
        viewModelScope.launch {
            val updated = item.copy(quantity = quantity, priceAtSale = priceAtSale)
            saleRepository.updateLineItem(updated)
            _uiState.update { state ->
                state.copy(
                    lineItems = state.lineItems.map { if (it.id == item.id) updated else it },
                    editingLineItem = null
                )
            }
        }
    }

    fun removeLineItem(item: LineItem) {
        viewModelScope.launch {
            saleRepository.removeLineItem(item.id)
            _uiState.update { state ->
                val newItems = state.lineItems.filter { it.id != item.id }
                if (newItems.isEmpty()) {
                    if (state.currentSaleId != null) {
                        saleRepository.cancelSale(state.currentSaleId, DateTimeUtils.getCurrentTime())
                    }
                    state.copy(lineItems = emptyList(), currentSaleId = null, editingLineItem = null)
                } else {
                    state.copy(lineItems = newItems, editingLineItem = null)
                }
            }
        }
    }

    fun cancelSale() {
        viewModelScope.launch {
            val saleId = _uiState.value.currentSaleId ?: return@launch
            saleRepository.cancelSale(saleId, DateTimeUtils.getCurrentTime())
            _uiState.update { SaleUiState() }
        }
    }

    fun endSale(cashReceived: Double): Boolean {
        val state = _uiState.value
        if (cashReceived < state.total) return false
        viewModelScope.launch {
            val saleId = state.currentSaleId ?: return@launch
            saleRepository.endSale(saleId, DateTimeUtils.getCurrentTime())
            _uiState.update { SaleUiState() }
        }
        return true
    }

    fun showPaymentSheet() {
        _uiState.update { it.copy(showPaymentSheet = true) }
    }

    fun hidePaymentSheet() {
        _uiState.update { it.copy(showPaymentSheet = false) }
    }

    fun showClearDialog() {
        _uiState.update { it.copy(showClearDialog = true) }
    }

    fun hideClearDialog() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    fun showEditSheet(item: LineItem) {
        _uiState.update { it.copy(editingLineItem = item) }
    }

    fun hideEditSheet() {
        _uiState.update { it.copy(editingLineItem = null) }
    }
}
