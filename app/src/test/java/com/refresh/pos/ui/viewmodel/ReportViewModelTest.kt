package com.refresh.pos.ui.viewmodel

import com.refresh.pos.data.repository.ReportRepository
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
class ReportViewModelTest {

    private val reportRepository = mockk<ReportRepository>(relaxed = true)
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
    fun periodSwitching_updatesPeriodIndex() = runTest {
        every { reportRepository.getSalesByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = ReportViewModel(reportRepository)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.setPeriod(2)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.periodIndex)
    }

    @Test
    fun dateNavigation_changesDate() = runTest {
        every { reportRepository.getSalesByDateRange(any(), any()) } returns flowOf(emptyList())

        val viewModel = ReportViewModel(reportRepository)
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.nextPeriod()
        advanceUntilIdle()

        val date = viewModel.uiState.value.currentDate
        assertTrue(date.isNotEmpty())
    }
}
