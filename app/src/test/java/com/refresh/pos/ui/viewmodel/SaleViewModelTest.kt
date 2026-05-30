package com.refresh.pos.ui.viewmodel

import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.data.repository.SaleRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaleViewModelTest {

    private val saleRepository = mockk<SaleRepository>(relaxed = true)
    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialSaleState_isEmpty() = runTest {
        val viewModel = SaleViewModel(saleRepository, productRepository)
        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
        assertEquals(0.0, state.total, 0.0)
    }

    @Test
    fun endSale_clearsState() = runTest {
        coEvery { saleRepository.endSale(any(), any()) } returns Unit

        val viewModel = SaleViewModel(saleRepository, productRepository)
        viewModel.endSale(0.0)

        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
    }

    @Test
    fun cancelSale_clearsState() = runTest {
        coEvery { saleRepository.cancelSale(any(), any()) } returns Unit

        val viewModel = SaleViewModel(saleRepository, productRepository)
        viewModel.cancelSale()

        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
    }
}
