package com.refresh.pos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_sum")
data class StockSumEntity(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val productId: Long,
    val quantity: Int = 0
)
