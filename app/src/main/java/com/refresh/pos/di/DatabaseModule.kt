package com.refresh.pos.di

import android.content.Context
import androidx.room.Room
import com.refresh.pos.data.local.PosDatabase
import com.refresh.pos.data.local.dao.LanguageDao
import com.refresh.pos.data.local.dao.ProductDao
import com.refresh.pos.data.local.dao.ProductLotDao
import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.data.local.dao.StockSumDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PosDatabase {
        return Room.databaseBuilder(context, PosDatabase::class.java, "com.refresh.db1")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideProductDao(db: PosDatabase): ProductDao = db.productDao()

    @Provides
    fun provideProductLotDao(db: PosDatabase): ProductLotDao = db.productLotDao()

    @Provides
    fun provideSaleDao(db: PosDatabase): SaleDao = db.saleDao()

    @Provides
    fun provideStockSumDao(db: PosDatabase): StockSumDao = db.stockSumDao()

    @Provides
    fun provideLanguageDao(db: PosDatabase): LanguageDao = db.languageDao()
}
