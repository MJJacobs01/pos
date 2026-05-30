package com.refresh.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.refresh.pos.data.local.dao.LanguageDao
import com.refresh.pos.data.local.dao.ProductDao
import com.refresh.pos.data.local.dao.ProductLotDao
import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.data.local.dao.StockSumDao
import com.refresh.pos.data.local.entity.LanguageEntity
import com.refresh.pos.data.local.entity.LineItemEntity
import com.refresh.pos.data.local.entity.ProductEntity
import com.refresh.pos.data.local.entity.ProductLotEntity
import com.refresh.pos.data.local.entity.SaleEntity
import com.refresh.pos.data.local.entity.StockSumEntity

@Database(
    entities = [
        ProductEntity::class,
        ProductLotEntity::class,
        SaleEntity::class,
        LineItemEntity::class,
        StockSumEntity::class,
        LanguageEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PosDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun productLotDao(): ProductLotDao
    abstract fun saleDao(): SaleDao
    abstract fun stockSumDao(): StockSumDao
    abstract fun languageDao(): LanguageDao
}
