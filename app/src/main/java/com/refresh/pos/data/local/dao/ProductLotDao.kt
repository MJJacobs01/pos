package com.refresh.pos.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import com.refresh.pos.data.local.entity.ProductLotEntity
import kotlinx.coroutines.flow.Flow

data class ProductLotWithProductName(
    @Embedded val lot: ProductLotEntity,
    @ColumnInfo(name = "product_name") val productName: String
)

@Dao
interface ProductLotDao {

    @Insert
    suspend fun insert(lot: ProductLotEntity): Long

    @Query("SELECT * FROM stock WHERE product_id = :productId ORDER BY date_added")
    fun getByProductId(productId: Long): Flow<List<ProductLotEntity>>

    @Query("SELECT s.*, p.name AS product_name FROM stock s INNER JOIN product_catalog p ON s.product_id = p._id WHERE s.product_id = :productId ORDER BY s.date_added")
    fun getLotsWithProductName(productId: Long): Flow<List<ProductLotWithProductName>>

    @Query("DELETE FROM stock")
    suspend fun clear()
}
