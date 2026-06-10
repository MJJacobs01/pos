package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.refresh.pos.R
import com.refresh.pos.domain.model.LineItem
import com.refresh.pos.ui.components.SheetContent
import com.refresh.pos.ui.components.SheetHeader
import com.refresh.pos.ui.components.SheetTextField
import com.refresh.pos.ui.components.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLineItemSheet(
    item: LineItem,
    onDismiss: () -> Unit,
    onSave: (quantity: Int, priceAtSale: Double) -> Unit,
    onRemove: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var price by remember { mutableStateOf(item.priceAtSale.toString()) }

    val lineTotal = (quantity.toIntOrNull() ?: 0) * (price.toDoubleOrNull() ?: 0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SheetContent {
            SheetHeader(
                title = item.productName,
                subtitle = item.productBarcode,
                icon = Icons.Default.ShoppingCart
            )

            SheetTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = stringResource(R.string.quantity),
                leadingIcon = Icons.Default.Numbers,
                keyboardType = KeyboardType.Number
            )
            SheetTextField(
                value = price,
                onValueChange = { price = it },
                label = stringResource(R.string.unit_price),
                leadingIcon = Icons.Default.Payments,
                keyboardType = KeyboardType.Decimal
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMoney(lineTotal),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: return@Button
                    val prc = price.toDoubleOrNull() ?: return@Button
                    onSave(qty, prc)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save")
            }
            OutlinedButton(
                onClick = onRemove,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null)
                Text(
                    text = stringResource(R.string.remove),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
