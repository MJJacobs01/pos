package com.refresh.pos.data.repository

import app.cash.turbine.test
import com.refresh.pos.data.local.dao.ProductDao
import com.refresh.pos.data.local.dao.ProductLotDao
import com.refresh.pos.data.local.dao.ProductLotWithProductName
import com.refresh.pos.data.local.dao.StockSumDao
import com.refresh.pos.data.local.entity.ProductEntity
import com.refresh.pos.data.local.entity.ProductLotEntity
import com.refresh.pos.data.local.entity.StockSumEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductRepositoryTest {

    private val productDao = mockk<ProductDao>()
    private val productLotDao = mockk<ProductLotDao>()
    private val stockSumDao = mockk<StockSumDao>()
    private val repository = ProductRepository(productDao, productLotDao, stockSumDao)

    @Test
    fun addProduct_createsStockSumEntry() = runTest {
        val entity = ProductEntity(name = "Test", barcode = "123", unitPrice = 9.99)
        coEvery { productDao.insert(any()) } returns 1L
        coEvery { stockSumDao.insertOrUpdate(1L, 0) } returns Unit

        val result = repository.addProduct("Test", "123", 9.99)

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { stockSumDao.insertOrUpdate(1L, 0) }
    }

    @Test
    fun suspendProduct_setsInactiveStatus() = runTest {
        coEvery { productDao.suspend(1L) } returns Unit

        val product = com.refresh.pos.domain.model.Product(id = 1L, name = "Test", barcode = "123", unitPrice = 9.99)
        val result = repository.suspendProduct(product)

        assertTrue(result.isSuccess)
        coVerify { productDao.suspend(1L) }
    }

    @Test
    fun searchProducts_filtersByNameAndBarcode() = runTest {
        val entities = listOf(
            ProductEntity(id = 1, name = "Pen", barcode = "123", unitPrice = 5.0),
            ProductEntity(id = 2, name = "Pencil", barcode = "456", unitPrice = 3.0)
        )
        every { productDao.search("Pen") } returns flowOf(entities)

        repository.searchProducts("Pen").test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals("Pen", items[0].name)
            awaitComplete()
        }
    }

    @Test
    fun getLotsByProductId_returnsMappedProductLots() = runTest {
        val lotEntity = ProductLotEntity(id = 1, productId = 1L, quantity = 10, cost = 2.0, dateAdded = "2026-01-01")
        val withName = ProductLotWithProductName(lot = lotEntity, productName = "Test Product")
        every { productLotDao.getLotsWithProductName(1L) } returns flowOf(listOf(withName))

        repository.getLotsByProductId(1L).test {
            val lots = awaitItem()
            assertEquals(1, lots.size)
            assertEquals("Test Product", lots[0].productName)
            assertEquals(10, lots[0].quantity)
            awaitComplete()
        }
    }
}
