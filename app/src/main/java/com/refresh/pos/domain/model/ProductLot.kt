package com.refresh.pos.domain.model

data class ProductLot(
    val id: Long = 0,
    val dateAdded: String,
    val quantity: Int,
    val productName: String,
    val unitCost: Double
) {
    companion object {
        const val UNDEFINED_ID = -1L
    }
}
