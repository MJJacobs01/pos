package com.refresh.pos.domain.model

data class SaleItem(
    val id: Long = 0,
    val startTime: String,
    val endTime: String,
    val status: String,
    val total: Double = 0.0,
    val orders: Int = 0,
    val lineItems: List<LineItem> = emptyList()
) {
    val computedTotal: Double
        get() = if (lineItems.isNotEmpty()) lineItems.sumOf { it.totalPrice } else total

    val computedOrders: Int
        get() = if (lineItems.isNotEmpty()) lineItems.sumOf { it.quantity } else orders
}
