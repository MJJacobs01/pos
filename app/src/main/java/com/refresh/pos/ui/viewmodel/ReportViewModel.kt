package com.refresh.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.pos.data.repository.ReportRepository
import com.refresh.pos.domain.model.SaleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

data class ReportUiState(
    val sales: List<SaleItem> = emptyList(),
    val currentDate: String = "",
    val periodIndex: Int = 0,
    val isLoading: Boolean = false
) {
    val total: Double get() = sales.sumOf { it.computedTotal }

    companion object {
        val periods = listOf("Daily", "Weekly", "Monthly", "Yearly")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _periodIndex = MutableStateFlow(0)
    private val _currentCalendar = MutableStateFlow(Calendar.getInstance())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<ReportUiState> = combine(
        _periodIndex, _currentCalendar
    ) { periodIndex, calendar -> Pair(periodIndex, calendar) }
        .flatMapLatest { (periodIndex, calendar) ->
            val (start, end) = getDateRange(periodIndex, calendar)
            combine(
                reportRepository.getSalesByDateRange(start, end),
                _isLoading
            ) { sales, loading ->
                ReportUiState(
                    sales = sales,
                    currentDate = formatDate(periodIndex, calendar),
                    periodIndex = periodIndex,
                    isLoading = loading
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportUiState())

    fun setPeriod(index: Int) {
        _periodIndex.value = index
    }

    fun previousPeriod() {
        _currentCalendar.update { addTime(it, -1) }
    }

    fun nextPeriod() {
        _currentCalendar.update { addTime(it, 1) }
    }

    private fun addTime(calendar: Calendar, amount: Int): Calendar {
        val clone = calendar.clone() as Calendar
        when (_periodIndex.value) {
            0 -> clone.add(Calendar.DAY_OF_MONTH, amount)
            1 -> clone.add(Calendar.WEEK_OF_YEAR, amount)
            2 -> clone.add(Calendar.MONTH, amount)
            3 -> clone.add(Calendar.YEAR, amount)
        }
        return clone
    }

    private fun getDateRange(periodIndex: Int, calendar: Calendar): Pair<Calendar, Calendar> {
        val start = calendar.clone() as Calendar
        val end = calendar.clone() as Calendar
        when (periodIndex) {
            0 -> {
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59)
            }
            1 -> {
                start.set(Calendar.DAY_OF_WEEK, start.firstDayOfWeek)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.time = start.time
                end.add(Calendar.WEEK_OF_YEAR, 1)
                end.add(Calendar.SECOND, -1)
            }
            2 -> {
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.time = start.time
                end.add(Calendar.MONTH, 1)
                end.add(Calendar.SECOND, -1)
            }
            3 -> {
                start.set(Calendar.DAY_OF_YEAR, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.time = start.time
                end.add(Calendar.YEAR, 1)
                end.add(Calendar.SECOND, -1)
            }
        }
        return Pair(start, end)
    }

    private fun formatDate(periodIndex: Int, calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        return when (periodIndex) {
            0 -> String.format("%04d-%02d-%02d", year, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
            1 -> String.format("%04d Week %02d", year, calendar.get(Calendar.WEEK_OF_YEAR))
            2 -> String.format("%04d-%02d", year, calendar.get(Calendar.MONTH) + 1)
            3 -> year.toString()
            else -> ""
        }
    }
}
