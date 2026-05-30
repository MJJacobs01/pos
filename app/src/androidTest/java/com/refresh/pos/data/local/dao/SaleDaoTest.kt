package com.refresh.pos.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.refresh.pos.data.local.PosDatabase
import com.refresh.pos.data.local.entity.LineItemEntity
import com.refresh.pos.data.local.entity.SaleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SaleDaoTest {

    private lateinit var database: PosDatabase
    private lateinit var saleDao: SaleDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PosDatabase::class.java
        ).build()
        saleDao = database.saleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fullSaleFlow_insertAndRetrieveWithLineItems() = runTest {
        val sale = SaleEntity(
            startTime = "2026-05-30 10:00:00",
            endTime = "2026-05-30 10:00:00"
        )
        val saleId = saleDao.insert(sale)

        val lineItem = LineItemEntity(saleId = saleId, productId = 1L, quantity = 2, unitPrice = 5.0)
        saleDao.insertLineItem(lineItem)

        val retrieved = saleDao.getSaleById(saleId)
        assertNotNull(retrieved)
        assertEquals(1, retrieved!!.lineItems.size)
        assertEquals(2, retrieved.lineItems[0].quantity)
    }

    @Test
    fun endSale_statusChanges() = runTest {
        val sale = SaleEntity(
            startTime = "2026-05-30 10:00:00",
            endTime = "2026-05-30 10:00:00"
        )
        val saleId = saleDao.insert(sale)

        val updated = sale.copy(id = saleId, status = "ENDED", endTime = "2026-05-30 11:00:00")
        saleDao.update(updated)

        val retrieved = saleDao.getSaleById(saleId)
        assertNotNull(retrieved)
        assertEquals("ENDED", retrieved!!.sale.status)
    }

    @Test
    fun cancelSale_statusChanges() = runTest {
        val sale = SaleEntity(startTime = "t", endTime = "t")
        val saleId = saleDao.insert(sale)

        val updated = sale.copy(id = saleId, status = "CANCELED", endTime = "t2")
        saleDao.update(updated)

        val retrieved = saleDao.getSaleById(saleId)
        assertEquals("CANCELED", retrieved!!.sale.status)
    }

    @Test
    fun deleteLineItem_removesItem() = runTest {
        val sale = SaleEntity(startTime = "t", endTime = "t")
        val saleId = saleDao.insert(sale)

        val item = LineItemEntity(saleId = saleId, productId = 1L, quantity = 1, unitPrice = 10.0)
        val itemId = saleDao.insertLineItem(item)
        saleDao.deleteLineItem(itemId)

        val retrieved = saleDao.getSaleById(saleId)
        assertEquals(0, retrieved!!.lineItems.size)
    }

    @Test
    fun getAllSales_filtersByDateRange() = runTest {
        saleDao.insert(SaleEntity(startTime = "2026-05-01 00:00:00", endTime = "2026-05-01 01:00:00"))
        saleDao.insert(SaleEntity(startTime = "2026-06-01 00:00:00", endTime = "2026-06-01 01:00:00"))

        val results = saleDao.getAllSales("2026-05-01", "2026-05-31").first()
        assertEquals(1, results.size)
    }
}
