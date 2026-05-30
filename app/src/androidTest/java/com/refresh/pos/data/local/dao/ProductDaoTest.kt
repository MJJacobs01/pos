package com.refresh.pos.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.refresh.pos.data.local.PosDatabase
import com.refresh.pos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ProductDaoTest {

    private lateinit var database: PosDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PosDatabase::class.java
        ).build()
        productDao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieve_productIsFound() = runTest {
        val product = ProductEntity(name = "Pen", barcode = "123", unitPrice = 5.0)
        val id = productDao.insert(product)

        val retrieved = productDao.getByIdOnce(id)
        assertNotNull(retrieved)
        assertEquals("Pen", retrieved!!.name)
        assertEquals("123", retrieved.barcode)
        assertEquals(5.0, retrieved.unitPrice, 0.0)
    }

    @Test
    fun search_findsByName() = runTest {
        productDao.insert(ProductEntity(name = "Blue Pen", barcode = "111", unitPrice = 10.0))
        productDao.insert(ProductEntity(name = "Red Pencil", barcode = "222", unitPrice = 5.0))

        val results = productDao.search("Pen").first()
        assertEquals(1, results.size)
        assertEquals("Blue Pen", results[0].name)
    }

    @Test
    fun search_findsByBarcode() = runTest {
        productDao.insert(ProductEntity(name = "Pen", barcode = "12345", unitPrice = 10.0))

        val results = productDao.search("12345").first()
        assertEquals(1, results.size)
    }

    @Test
    fun suspend_changesStatus() = runTest {
        val id = productDao.insert(ProductEntity(name = "Pen", barcode = "123", unitPrice = 5.0))
        productDao.suspend(id)

        val retrieved = productDao.getByIdOnce(id)
        assertEquals("INACTIVE", retrieved?.status)
    }

    @Test
    fun getByBarcode_findsProduct() = runTest {
        productDao.insert(ProductEntity(name = "Pen", barcode = "ABC123", unitPrice = 5.0))

        val result = productDao.getByBarcode("ABC123").first()
        assertNotNull(result)
        assertEquals("Pen", result?.name)
    }
}
