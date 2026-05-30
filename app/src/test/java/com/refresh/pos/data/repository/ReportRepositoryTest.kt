package com.refresh.pos.data.repository

import app.cash.turbine.test
import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.data.local.entity.SaleEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class ReportRepositoryTest {

    private val saleDao = mockk<SaleDao>()
    private val repository = ReportRepository(saleDao)

    @Test
    fun getSalesByDateRange_queriesWithFormattedDates() = runTest {
        val entity = SaleEntity(id = 1, startTime = "t", endTime = "t")
        every { saleDao.getAllSales(any(), any()) } returns flowOf(listOf(entity))

        val start = Calendar.getInstance().apply { set(2026, 4, 1, 0, 0, 0) }
        val end = Calendar.getInstance().apply { set(2026, 4, 31, 23, 59, 59) }

        repository.getSalesByDateRange(start, end).test {
            val sales = awaitItem()
            assertEquals(1, sales.size)
            assertEquals(1L, sales[0].id)
            awaitComplete()
        }
    }

    @Test
    fun getAllSales_usesFullDateRange() = runTest {
        val entity = SaleEntity(id = 1, startTime = "t", endTime = "t")
        every { saleDao.getAllSales(any(), any()) } returns flowOf(listOf(entity))

        repository.getAllSales().test {
            val sales = awaitItem()
            assertEquals(1, sales.size)
            awaitComplete()
        }
    }
}
