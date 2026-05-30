package com.refresh.pos.data.repository

import android.content.Context
import com.refresh.pos.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productRepository: ProductRepository
) {
    suspend fun seedDemoProducts() {
        val inputStream = context.resources.openRawResource(R.raw.products)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.use { it.readLines() }
        for (line in lines) {
            val parts = line.split(",")
            if (parts.size >= 3) {
                val barcode = parts[0]
                val name = parts[1]
                val price = parts[2].toDoubleOrNull() ?: 0.0
                productRepository.addProduct(name, barcode, price)
            }
        }
    }
}
