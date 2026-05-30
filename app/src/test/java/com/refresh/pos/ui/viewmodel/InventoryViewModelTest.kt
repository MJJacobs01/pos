package com.refresh.pos.ui.viewmodel

import com.refresh.pos.data.repository.DemoDataProvider
import com.refresh.pos.data.repository.ProductRepository
import com.refresh.pos.domain.model.Product
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private val demoDataProvider = mockk<DemoDataProvider>(relaxed = true)
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
    fun initialLoad_fetchesAllProducts() = runTest {
        val products = listOf(Product(id = 1, name = "Pen", barcode = "123", unitPrice = 5.0))
        every { productRepository.getAllProducts() } returns flowOf(products)
        every { productRepository.searchProducts(any()) } returns flowOf(emptyList())

        val viewModel = InventoryViewModel(productRepository, demoDataProvider)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.products.size)
        assertEquals("Pen", state.products[0].name)
    }

    @Test
    fun search_filtersProducts() = runTest {
        every { productRepository.getAllProducts() } returns flowOf(emptyList())
        every { productRepository.searchProducts("Pen") } returns flowOf(
            listOf(Product(id = 1, name = "Pen", barcode = "123", unitPrice = 5.0))
        )

        val viewModel = InventoryViewModel(productRepository, demoDataProvider)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Pen")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.products.size)
        assertEquals("Pen", state.products[0].name)
    }

    @Test
    fun secretDemoCommand_seedsData() = runTest {
        every { productRepository.getAllProducts() } returns flowOf(emptyList())
        coEvery { demoDataProvider.seedDemoProducts() } returns Unit

        val viewModel = InventoryViewModel(productRepository, demoDataProvider)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onSearchQueryChange("/demo")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertEquals("Demo data seeded", state.message)
    }

    @Test
    fun secretClearCommand_clearsData() = runTest {
        every { productRepository.getAllProducts() } returns flowOf(emptyList())
        coEvery { productRepository.clearAll() } returns Unit

        val viewModel = InventoryViewModel(productRepository, demoDataProvider)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onSearchQueryChange("/clear")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertEquals("All data cleared", state.message)
    }
}
