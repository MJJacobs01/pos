package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refresh.pos.R
import com.refresh.pos.ui.components.EmptyState
import com.refresh.pos.ui.components.SaleLineItemCard
import com.refresh.pos.ui.components.TotalBar
import com.refresh.pos.ui.viewmodel.SaleViewModel

@Composable
fun SaleScreen(
    onNavigateToSaleDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SaleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.isEmpty) {
            EmptyState(
                icon = Icons.Default.ShoppingCart,
                message = stringResource(R.string.hint_empty_sale),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.lineItems, key = { it.id }) { item ->
                    SaleLineItemCard(
                        name = item.productName,
                        quantity = item.quantity,
                        priceAtSale = item.priceAtSale,
                        totalPrice = item.totalPrice,
                        onClick = { viewModel.showEditSheet(item) }
                    )
                }
            }
        }

        TotalBar(
            label = stringResource(R.string.total),
            total = uiState.total
        ) {
            OutlinedButton(
                onClick = { viewModel.showClearDialog() },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isEmpty
            ) {
                Text(stringResource(R.string.clear))
            }
            Button(
                onClick = { viewModel.showPaymentSheet() },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isEmpty
            ) {
                Text("End Sale")
            }
        }
    }

    if (uiState.showClearDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideClearDialog() },
            title = { Text(stringResource(R.string.clear)) },
            text = { Text(stringResource(R.string.dialog_clear_sale)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelSale()
                    viewModel.hideClearDialog()
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideClearDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (uiState.showPaymentSheet) {
        PaymentSheet(
            total = uiState.total,
            onDismiss = { viewModel.hidePaymentSheet() },
            onConfirm = { cash -> viewModel.endSale(cash) }
        )
    }

    uiState.editingLineItem?.let { item ->
        EditLineItemSheet(
            item = item,
            onDismiss = { viewModel.hideEditSheet() },
            onSave = { qty, price -> viewModel.updateLineItem(item, qty, price) },
            onRemove = { viewModel.removeLineItem(item) }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SaleScreenPreview() {
    com.refresh.pos.ui.theme.PosTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SaleLineItemCard(name = "Coffee", quantity = 1, priceAtSale = 85.0, totalPrice = 85.0)
                }
                item {
                    SaleLineItemCard(name = "Milk", quantity = 2, priceAtSale = 45.0, totalPrice = 90.0)
                }
            }
            TotalBar(label = "Total", total = 175.0) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) { Text("Clear") }
                Button(onClick = { }, modifier = Modifier.weight(1f)) { Text("End Sale") }
            }
        }
    }
}
