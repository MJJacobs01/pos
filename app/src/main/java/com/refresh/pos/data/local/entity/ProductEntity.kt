package com.refresh.pos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_catalog")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val name: String,
    val barcode: String,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,
    val status: String = "ACTIVE"
)
