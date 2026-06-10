package com.refresh.pos.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refresh.pos.R
import com.refresh.pos.ui.components.EmptyState
import com.refresh.pos.ui.components.PeriodSelector
import com.refresh.pos.ui.components.TotalBar
import com.refresh.pos.ui.components.formatMoney
import com.refresh.pos.ui.viewmodel.ReportUiState
import com.refresh.pos.ui.viewmodel.ReportViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReportScreen(
    onNavigateToSaleDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        PeriodSelector(
            periods = ReportUiState.periods,
            selectedIndex = uiState.periodIndex,
            onSelect = { viewModel.setPeriod(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.previousPeriod() }) {
                Text(stringResource(R.string.previous))
            }
            Text(
                text = uiState.currentDate,
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { viewModel.nextPeriod() }) {
                Text(stringResource(R.string.next))
            }
        }

        if (uiState.sales.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                message = "No sales in this period",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.sales) { sale ->
                    SaleSummaryCard(
                        id = sale.id,
                        date = sale.startTime.substring(0, minOf(10, sale.startTime.length)),
                        itemCount = sale.computedOrders,
                        total = sale.computedTotal,
                        onClick = { onNavigateToSaleDetail(sale.id) }
                    )
                }
            }
        }

        TotalBar(
            label = stringResource(R.string.total),
            total = uiState.total
        )
    }
}

@Composable
private fun SaleSummaryCard(
    id: Long,
    date: String,
    itemCount: Int,
    total: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Sale #$id", style = MaterialTheme.typography.titleMedium)
                Text(
                    date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Items: $itemCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatMoney(total),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ReportScreenPreview() {
    com.refresh.pos.ui.theme.PosTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            PeriodSelector(
                periods = listOf("Daily", "Weekly", "Monthly", "Yearly"),
                selectedIndex = 0,
                onSelect = { },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { }) { Text("Prev") }
                Text("2026-05-30", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { }) { Text("Next") }
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SaleSummaryCard(id = 1, date = "2026-05-30", itemCount = 3, total = 175.0, onClick = { })
                }
                item {
                    SaleSummaryCard(id = 2, date = "2026-05-30", itemCount = 1, total = 42.5, onClick = { })
                }
            }
            TotalBar(label = "Total", total = 217.5)
        }
    }
}
