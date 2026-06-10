package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.refresh.pos.R
import com.refresh.pos.ui.components.SheetContent
import com.refresh.pos.ui.components.SheetHeader
import com.refresh.pos.ui.components.SheetTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductLotSheet(
    onDismiss: () -> Unit,
    onAddLot: (quantity: Int, cost: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var quantity by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SheetContent {
            SheetHeader(
                title = stringResource(R.string.add_stock),
                subtitle = "Record a new stock lot",
                icon = Icons.Default.Inventory2
            )

            SheetTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = stringResource(R.string.quantity),
                leadingIcon = Icons.Default.Numbers,
                keyboardType = KeyboardType.Number
            )
            SheetTextField(
                value = cost,
                onValueChange = { cost = it },
                label = "Cost per unit",
                leadingIcon = Icons.Default.Payments,
                keyboardType = KeyboardType.Decimal
            )

            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: return@Button
                    val cst = cost.toDoubleOrNull() ?: return@Button
                    onAddLot(qty, cst)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = quantity.isNotBlank() && cost.isNotBlank()
            ) {
                Text(stringResource(R.string.add_stock))
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
