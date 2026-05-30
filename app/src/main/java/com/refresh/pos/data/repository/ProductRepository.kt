package com.refresh.pos.data.repository

import com.refresh.pos.data.local.dao.ProductDao
import com.refresh.pos.data.local.dao.ProductLotDao
import com.refresh.pos.data.local.dao.StockSumDao
import com.refresh.pos.data.local.entity.ProductEntity
import com.refresh.pos.data.local.entity.ProductLotEntity
import com.refresh.pos.domain.model.Product
import com.refresh.pos.domain.model.ProductLot
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val productLotDao: ProductLotDao,
    private val stockSumDao: StockSumDao
) {
    fun getAllProducts(): Flow<List<Product>> =
        productDao.getAll().map { entities -> entities.map { it.toDomain() } }

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.search(query).map { entities -> entities.map { it.toDomain() } }

    fun getProductById(id: Long): Flow<Product?> =
        productDao.getById(id).map { it?.toDomain() }

    fun getProductByBarcode(barcode: String): Flow<Product?> =
        productDao.getByBarcode(barcode).map { it?.toDomain() }

    suspend fun addProduct(name: String, barcode: String, price: Double): Result<Long> = runCatching {
        val entity = ProductEntity(name = name, barcode = barcode, unitPrice = price)
        val productId = productDao.insert(entity)
        stockSumDao.insertOrUpdate(productId, 0)
        productId
    }

    suspend fun editProduct(product: Product): Result<Unit> = runCatching {
        productDao.update(product.toEntity())
    }

    suspend fun suspendProduct(product: Product): Result<Unit> = runCatching {
        productDao.suspend(product.id)
    }

    suspend fun addProductLot(
        productId: Long,
        date: String,
        quantity: Int,
        cost: Double
    ): Result<Long> = runCatching {
        val lot = ProductLotEntity(
            productId = productId,
            dateAdded = date,
            quantity = quantity,
            cost = cost
        )
        val lotId = productLotDao.insert(lot)
        val currentSum = stockSumDao.getByProductIdOnce(productId)?.quantity ?: 0
        stockSumDao.insertOrUpdate(productId, currentSum + quantity)
        lotId
    }

    fun getLotsByProductId(productId: Long): Flow<List<ProductLot>> =
        productLotDao.getLotsWithProductName(productId).map { list ->
            list.map { it.toDomain() }
        }

    fun getStockSum(productId: Long): Flow<Int> =
        stockSumDao.getByProductId(productId).map { it?.quantity ?: 0 }

    suspend fun clearAll() {
        productDao.clear()
        productLotDao.clear()
        stockSumDao.clear()
    }

    private fun ProductEntity.toDomain() = Product(
        id = id,
        name = name,
        barcode = barcode,
        unitPrice = unitPrice,
        status = status
    )

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        barcode = barcode,
        unitPrice = unitPrice,
        status = status
    )

    private fun com.refresh.pos.data.local.dao.ProductLotWithProductName.toDomain() = ProductLot(
        id = lot.id,
        dateAdded = lot.dateAdded,
        quantity = lot.quantity,
        productName = productName,
        unitCost = lot.cost
    )
}
