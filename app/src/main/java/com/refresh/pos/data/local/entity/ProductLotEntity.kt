package com.refresh.pos.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock")
data class ProductLotEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "product_id")
    val productId: Long,
    val quantity: Int,
    val cost: Double,
    @ColumnInfo(name = "date_added")
    val dateAdded: String
)
