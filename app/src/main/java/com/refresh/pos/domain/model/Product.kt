package com.refresh.pos.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val barcode: String,
    val unitPrice: Double,
    val status: String = "ACTIVE"
) {
    companion object {
        const val UNDEFINED_ID = -1L
    }
}
