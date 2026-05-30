package com.refresh.pos.data.repository

import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.domain.model.SaleItem
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ReportRepository @Inject constructor(
    private val saleDao: SaleDao
) {
    fun getSalesByDateRange(start: Calendar, end: Calendar): Flow<List<SaleItem>> {
        val startStr = formatCalendar(start)
        val endStr = formatCalendar(end)
        return saleDao.getAllSales(startStr, endStr).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getAllSales(): Flow<List<SaleItem>> =
        saleDao.getAllSales("1970-01-01", "2099-12-31").map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun clearAll() {
        saleDao.clearLineItems()
        saleDao.clearSales()
    }

    private fun formatCalendar(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        return "$year-$month-$day 00:00:00"
    }

    private fun com.refresh.pos.data.local.entity.SaleEntity.toDomain() = SaleItem(
        id = id,
        startTime = startTime,
        endTime = endTime,
        status = status,
        total = total,
        orders = orders
    )
}
