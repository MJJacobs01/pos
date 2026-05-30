package com.refresh.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refresh.pos.data.local.entity.StockSumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockSumDao {

    @Upsert(entity = StockSumEntity::class)
    suspend fun insertOrUpdate(productId: Long, quantity: Int)

    @Query("SELECT * FROM stock_sum WHERE _id = :productId")
    fun getByProductId(productId: Long): Flow<StockSumEntity?>

    @Query("SELECT * FROM stock_sum WHERE _id = :productId")
    suspend fun getByProductIdOnce(productId: Long): StockSumEntity?

    @Query("DELETE FROM stock_sum")
    suspend fun clear()
}
