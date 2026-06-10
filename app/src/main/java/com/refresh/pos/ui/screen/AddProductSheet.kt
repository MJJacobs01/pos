package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.Label
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
fun AddProductSheet(
    onDismiss: () -> Unit,
    onAddProduct: (name: String, barcode: String, price: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var barcode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SheetContent {
            SheetHeader(
                title = stringResource(R.string.add_new_product),
                subtitle = "Create a product for your catalog",
                icon = Icons.Default.AddShoppingCart
            )

            SheetTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = stringResource(R.string.barcode),
                leadingIcon = Icons.Default.QrCode2,
                keyboardType = KeyboardType.Number
            )
            SheetTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.name),
                leadingIcon = Icons.Outlined.Label
            )
            SheetTextField(
                value = price,
                onValueChange = { price = it },
                label = stringResource(R.string.unit_price),
                leadingIcon = Icons.Default.Payments,
                keyboardType = KeyboardType.Decimal
            )

            Button(
                onClick = {
                    val priceVal = price.toDoubleOrNull() ?: return@Button
                    onAddProduct(name, barcode, priceVal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = barcode.isNotBlank() && name.isNotBlank() && price.isNotBlank()
            ) {
                Text(stringResource(R.string.add_new_product))
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
