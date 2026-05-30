package com.refresh.pos.data.repository

import com.refresh.pos.data.local.dao.ProductDao
import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.data.local.dao.SaleWithLineItems
import com.refresh.pos.data.local.dao.StockSumDao
import com.refresh.pos.data.local.entity.LineItemEntity
import com.refresh.pos.data.local.entity.ProductEntity
import com.refresh.pos.data.local.entity.SaleEntity
import com.refresh.pos.data.local.entity.StockSumEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SaleRepositoryTest {

    private val saleDao = mockk<SaleDao>()
    private val productDao = mockk<ProductDao>()
    private val stockSumDao = mockk<StockSumDao>()
    private val repository = SaleRepository(saleDao, productDao, stockSumDao)

    @Test
    fun initiateSale_createsNewSaleWithOnProcessStatus() = runTest {
        coEvery { saleDao.insert(any()) } returns 1L

        val sale = repository.initiateSale("2026-05-30 10:00:00")

        assertEquals(1L, sale.id)
        assertEquals("ON PROCESS", sale.status)
    }

    @Test
    fun addLineItem_addsItemToSale() = runTest {
        val product = ProductEntity(id = 1, name = "Test", barcode = "123", unitPrice = 9.99)
        coEvery { productDao.getByIdOnce(1L) } returns product
        coEvery { saleDao.insertLineItem(any()) } returns 10L

        val lineItem = repository.addLineItem(saleId = 1L, productId = 1L, quantity = 2, unitPrice = 9.99)

        assertEquals(10L, lineItem.id)
        assertEquals("Test", lineItem.productName)
        assertEquals(2, lineItem.quantity)
    }

    @Test
    fun endSale_updatesStatusAndAdjustsStockSums() = runTest {
        val saleEntity = SaleEntity(id = 1, startTime = "t", endTime = "t", status = "ON PROCESS")
        val lineItemEntity = LineItemEntity(id = 1, saleId = 1, productId = 1L, quantity = 3, unitPrice = 5.0)
        val saleWithItems = SaleWithLineItems(sale = saleEntity, lineItems = listOf(lineItemEntity))

        coEvery { saleDao.getSaleById(1L) } returns saleWithItems
        coEvery { saleDao.update(any()) } returns Unit
        coEvery { stockSumDao.getByProductIdOnce(1L) } returns StockSumEntity(productId = 1L, quantity = 10)
        coEvery { stockSumDao.insertOrUpdate(any(), any()) } returns Unit

        repository.endSale(1L, "2026-05-30 11:00:00")

        coVerify { saleDao.update(match { it.status == "ENDED" }) }
        coVerify { stockSumDao.insertOrUpdate(1L, 7) }
    }

    @Test
    fun cancelSale_setsCanceledStatus() = runTest {
        val saleEntity = SaleEntity(id = 1, startTime = "t", endTime = "t", status = "ON PROCESS")
        val saleWithItems = SaleWithLineItems(sale = saleEntity, lineItems = emptyList())

        coEvery { saleDao.getSaleById(1L) } returns saleWithItems
        coEvery { saleDao.update(any()) } returns Unit

        repository.cancelSale(1L, "2026-05-30 11:00:00")

        coVerify { saleDao.update(match { it.status == "CANCELED" }) }
    }

    @Test
    fun getSaleById_returnsNullWhenNotFound() = runTest {
        coEvery { saleDao.getSaleById(1L) } returns null

        val result = repository.getSaleById(1L)

        assertEquals(null, result)
    }
}
