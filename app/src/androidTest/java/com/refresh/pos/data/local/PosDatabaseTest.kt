package com.refresh.pos.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PosDatabaseTest {

    private lateinit var database: PosDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PosDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun version_isOne() {
        assertEquals(1, database.openHelper.readableDatabase.version)
    }

    @Test
    fun allDaos_areAccessible() {
        assertNotNull(database.productDao())
        assertNotNull(database.productLotDao())
        assertNotNull(database.saleDao())
        assertNotNull(database.stockSumDao())
        assertNotNull(database.languageDao())
    }
}
