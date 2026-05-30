package com.refresh.pos.domain.model

data class LineItem(
    val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String,
    val productBarcode: String,
    val quantity: Int,
    val priceAtSale: Double
) {
    val totalPrice: Double
        get() = priceAtSale * quantity

    companion object {
        const val UNDEFINED = -1L
    }
}
