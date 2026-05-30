package com.refresh.pos.ui.screen

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refresh.pos.R
import com.refresh.pos.domain.model.Product
import com.refresh.pos.ui.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    onNavigateToProductDetail: (Long) -> Unit,
    onProductTapped: (productId: Long, unitPrice: Double) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var longPressProduct by remember { mutableStateOf<Product?>(null) }
    var showSuspendDialog by remember { mutableStateOf<Product?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.products.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.products, key = { it.id }) { product ->
                    Column(
                        modifier = Modifier.combinedClickable(
                            onClick = { onProductTapped(product.id, product.unitPrice) },
                            onLongClick = { longPressProduct = product }
                        )
                    ) {
                        ListItem(
                            headlineContent = { Text(product.name) },
                            supportingContent = {
                                Row {
                                    Text(product.barcode)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(stringResource(R.string.unit_price))
                                    Text(product.unitPrice.toString())
                                }
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_new_product))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.clear)) },
            text = { Text(stringResource(R.string.dialog_remove_product)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showClearDialog = false
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    if (showAddSheet) {
        AddProductSheet(
            onDismiss = { showAddSheet = false },
            onAddProduct = { name, barcode, price ->
                viewModel.addProduct(name, barcode, price)
                showAddSheet = false
            }
        )
    }

    longPressProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { longPressProduct = null },
            title = { Text(product.name) },
            text = { Text("${product.barcode}\n${product.unitPrice}") },
            confirmButton = {
                TextButton(onClick = {
                    onNavigateToProductDetail(product.id)
                    longPressProduct = null
                }) { Text(stringResource(R.string.product_detail)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuspendDialog = product
                    longPressProduct = null
                }) { Text("Suspend") }
            }
        )
    }

    showSuspendDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showSuspendDialog = null },
            title = { Text(product.name) },
            text = { Text("Suspend this product?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.suspendProduct(product)
                    showSuspendDialog = null
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendDialog = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InventoryScreenPreview() {
    val fakeProducts = listOf(
        com.refresh.pos.domain.model.Product(1, "Pen", "123", 5.0),
        com.refresh.pos.domain.model.Product(2, "Pencil", "456", 3.0),
        com.refresh.pos.domain.model.Product(3, "Eraser", "789", 2.5)
    )
    com.refresh.pos.ui.theme.PosTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(fakeProducts) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = {
                            Row {
                                Text(product.barcode)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text("Unit Price")
                                Text(product.unitPrice.toString())
                            }
                        }
                    )
                }
            }
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
