package com.refresh.pos.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refresh.pos.R

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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${stringResource(R.string.total)}: ${"%.2f".format(total)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cashReceived,
                onValueChange = { cashReceived = it; changeAmount = null; validationError = false },
                label = { Text(stringResource(R.string.cash)) },
                isError = validationError,
                supportingText = if (validationError) {
                    { Text("Cash must be >= total") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            changeAmount?.let { change ->
                Text(
                    text = "${stringResource(R.string.change)}: $change",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val cash = cashReceived.toDoubleOrNull()
                    if (cash != null && cash >= total) {
                        changeAmount = "%.2f".format(cash - total)
                        validationError = false
                        onConfirm(cash)
                    } else {
                        validationError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = cashReceived.isNotBlank()
            ) {
                Text(stringResource(R.string.confirm))
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
