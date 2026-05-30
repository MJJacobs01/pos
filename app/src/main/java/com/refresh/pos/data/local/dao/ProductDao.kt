package com.refresh.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.refresh.pos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE product_catalog SET status = 'INACTIVE' WHERE _id = :id")
    suspend fun suspend(id: Long)

    @Query("SELECT * FROM product_catalog WHERE _id = :id")
    fun getById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM product_catalog WHERE _id = :id")
    suspend fun getByIdOnce(id: Long): ProductEntity?

    @Query("SELECT * FROM product_catalog WHERE barcode = :barcode")
    fun getByBarcode(barcode: String): Flow<ProductEntity?>

    @Query("SELECT * FROM product_catalog ORDER BY name")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_catalog WHERE name = :name")
    fun getByName(name: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_catalog WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY name")
    fun search(query: String): Flow<List<ProductEntity>>

    @Query("DELETE FROM product_catalog")
    suspend fun clear()
}
