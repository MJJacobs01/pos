package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.refresh.pos.R
import com.refresh.pos.ui.components.SheetContent
import com.refresh.pos.ui.components.SheetHeader
import com.refresh.pos.ui.components.SheetTextField
import com.refresh.pos.ui.components.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSheet(
    total: Double,
    onDismiss: () -> Unit,
    onConfirm: (cash: Double) -> Boolean
) {
    val sheetState = rememberModalBottomSheetState()
    var cashReceived by remember { mutableStateOf("") }
    var changeAmount by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SheetContent {
            SheetHeader(
                title = stringResource(R.string.total),
                subtitle = "Take payment and give change",
                icon = Icons.Default.Payments
            )

            // Prominent total
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatMoney(total),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            SheetTextField(
                value = cashReceived,
                onValueChange = { cashReceived = it; changeAmount = null; validationError = false },
                label = stringResource(R.string.cash),
                leadingIcon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Decimal,
                isError = validationError,
                supportingText = if (validationError) "Cash must be ≥ total" else null
            )

            changeAmount?.let { change ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.change),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = change,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val cash = cashReceived.toDoubleOrNull()
                    if (cash != null && cash >= total) {
                        changeAmount = formatMoney(cash - total)
                        validationError = false
                        onConfirm(cash)
                    } else {
                        validationError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = cashReceived.isNotBlank()
            ) {
                Text(stringResource(R.string.confirm))
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}
