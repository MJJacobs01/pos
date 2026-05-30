package com.refresh.pos.data.repository

import com.refresh.pos.data.local.dao.SaleDao
import com.refresh.pos.data.local.dao.SaleWithLineItems
import com.refresh.pos.data.local.dao.StockSumDao
import com.refresh.pos.data.local.entity.LineItemEntity
import com.refresh.pos.data.local.entity.SaleEntity
import com.refresh.pos.domain.model.LineItem
import com.refresh.pos.domain.model.SaleItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val productDao: com.refresh.pos.data.local.dao.ProductDao,
    private val stockSumDao: StockSumDao
) {
    suspend fun initiateSale(startTime: String): SaleItem {
        val entity = SaleEntity(startTime = startTime, endTime = startTime)
        val saleId = saleDao.insert(entity)
        return SaleItem(
            id = saleId,
            startTime = startTime,
            endTime = startTime,
            status = "ON PROCESS"
        )
    }

    suspend fun addLineItem(
        saleId: Long,
        productId: Long,
        quantity: Int,
        unitPrice: Double
    ): LineItem {
        val product = productDao.getByIdOnce(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")
        val entity = LineItemEntity(
            saleId = saleId,
            productId = productId,
            quantity = quantity,
            unitPrice = unitPrice
        )
        val lineItemId = saleDao.insertLineItem(entity)
        return LineItem(
            id = lineItemId,
            saleId = saleId,
            productId = productId,
            productName = product.name,
            productBarcode = product.barcode,
            quantity = quantity,
            priceAtSale = unitPrice
        )
    }

    suspend fun updateLineItem(item: LineItem) {
        saleDao.updateLineItem(item.toEntity())
    }

    suspend fun removeLineItem(itemId: Long) {
        saleDao.deleteLineItem(itemId)
    }

    suspend fun endSale(saleId: Long, endTime: String) {
        val saleWithItems = saleDao.getSaleById(saleId) ?: return
        val ended = saleWithItems.sale.copy(status = "ENDED", endTime = endTime)
        saleDao.update(ended)
        for (item in saleWithItems.lineItems) {
            val currentSum = stockSumDao.getByProductIdOnce(item.productId)?.quantity ?: 0
            stockSumDao.insertOrUpdate(item.productId, currentSum - item.quantity)
        }
    }

    suspend fun cancelSale(saleId: Long, endTime: String) {
        val saleWithItems = saleDao.getSaleById(saleId) ?: return
        val canceled = saleWithItems.sale.copy(status = "CANCELED", endTime = endTime)
        saleDao.update(canceled)
    }

    suspend fun getSaleById(id: Long): SaleItem? {
        val result = saleDao.getSaleById(id) ?: return null
        return result.toDomain()
    }

    fun getAllSales(start: String, end: String): Flow<List<SaleItem>> =
        saleDao.getAllSales(start, end).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun clearAll() {
        saleDao.clearLineItems()
        saleDao.clearSales()
    }

    private fun SaleEntity.toDomain() = SaleItem(
        id = id,
        startTime = startTime,
        endTime = endTime,
        status = status,
        total = total,
        orders = orders
    )

    private fun SaleWithLineItems.toDomain() = SaleItem(
        id = sale.id,
        startTime = sale.startTime,
        endTime = sale.endTime,
        status = sale.status,
        total = sale.total,
        orders = sale.orders,
        lineItems = lineItems.map {
            LineItem(
                id = it.id,
                saleId = it.saleId,
                productId = it.productId,
                productName = "",
                productBarcode = "",
                quantity = it.quantity,
                priceAtSale = it.unitPrice
            )
        }
    )

    private fun LineItem.toEntity() = LineItemEntity(
        id = id,
        saleId = saleId,
        productId = productId,
        quantity = quantity,
        unitPrice = priceAtSale
    )
}
