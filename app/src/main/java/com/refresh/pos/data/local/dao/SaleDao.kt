package com.refresh.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.refresh.pos.data.local.entity.LineItemEntity
import com.refresh.pos.data.local.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

data class SaleWithLineItems(
    @Embedded val sale: SaleEntity,
    @Relation(
        parentColumn = "_id",
        entityColumn = "sale_id"
    )
    val lineItems: List<LineItemEntity>
)

@Dao
interface SaleDao {

    @Insert
    suspend fun insert(sale: SaleEntity): Long

    @Update
    suspend fun update(sale: SaleEntity)

    @Insert
    suspend fun insertLineItem(item: LineItemEntity): Long

    @Update
    suspend fun updateLineItem(item: LineItemEntity)

    @Query("DELETE FROM sale_lineitem WHERE _id = :id")
    suspend fun deleteLineItem(id: Long)

    @Transaction
    @Query("SELECT * FROM sale WHERE _id = :id")
    suspend fun getSaleById(id: Long): SaleWithLineItems?

    @Query("SELECT * FROM sale WHERE start_time >= :start AND end_time <= :end ORDER BY start_time DESC")
    fun getAllSales(start: String, end: String): Flow<List<SaleEntity>>

    @Query("DELETE FROM sale_lineitem")
    suspend fun clearLineItems()

    @Query("DELETE FROM sale")
    suspend fun clearSales()
}
