package com.refresh.pos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_lineitem")
data class LineItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "sale_id")
    val saleId: Long,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    val quantity: Int,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double
)
