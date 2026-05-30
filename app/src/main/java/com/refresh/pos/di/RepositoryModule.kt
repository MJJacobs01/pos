package com.refresh.pos.di

import com.refresh.pos.data.repository.DemoDataProvider
import com.refresh.pos.data.repository.LanguageRepository
import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.data.repository.ReportRepository
import com.refresh.pos.data.repository.SaleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: com.refresh.pos.data.local.dao.ProductDao,
        productLotDao: com.refresh.pos.data.local.dao.ProductLotDao,
        stockSumDao: com.refresh.pos.data.local.dao.StockSumDao
    ): ProductRepository = ProductRepository(productDao, productLotDao, stockSumDao)

    @Provides
    @Singleton
    fun provideSaleRepository(
        saleDao: com.refresh.pos.data.local.dao.SaleDao,
        productDao: com.refresh.pos.data.local.dao.ProductDao,
        stockSumDao: com.refresh.pos.data.local.dao.StockSumDao
    ): SaleRepository = SaleRepository(saleDao, productDao, stockSumDao)

    @Provides
    @Singleton
    fun provideReportRepository(
        saleDao: com.refresh.pos.data.local.dao.SaleDao
    ): ReportRepository = ReportRepository(saleDao)

    @Provides
    @Singleton
    fun provideLanguageRepository(
        languageDao: com.refresh.pos.data.local.dao.LanguageDao
    ): LanguageRepository = LanguageRepository(languageDao)

    @Provides
    @Singleton
    fun provideDemoDataProvider(
        @ApplicationContext context: android.content.Context,
        productRepository: ProductRepository
    ): DemoDataProvider = DemoDataProvider(context, productRepository)
}
