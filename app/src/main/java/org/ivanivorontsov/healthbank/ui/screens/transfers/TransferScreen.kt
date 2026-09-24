package org.ivanivorontsov.healthbank.ui.screens.transfers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(vm: AppViewModel, onBack: () -> Unit, onReceipt: (String) -> Unit) {
    val accounts by vm.accounts.collectAsState()
    val preview by vm.transferPreview.collectAsState()
    val message by vm.message.collectAsState()
    var fromId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var toId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: "") }
    var amountText by remember { mutableStateOf("") }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(accounts) {
        if (fromId.isEmpty() && accounts.isNotEmpty()) fromId = accounts[0].id
        if (toId.isEmpty() && accounts.size > 1) toId = accounts[1].id
    }

    Scaffold(topBar = { BankTopBar("Перевод между счетами", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text("Откуда", style = MaterialTheme.typography.labelLarge)
            ExposedDropdownMenuBox(expanded = fromExpanded, onExpandedChange = { fromExpanded = it }) {
                OutlinedTextField(
                    value = accounts.find { it.id == fromId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(fromExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(fromExpanded, onDismissRequest = { fromExpanded = false }) {
                    accounts.forEach {
                        DropdownMenuItem(text = { Text("${it.name} (${formatZdr(it.balance)})") }, onClick = {
                            fromId = it.id; fromExpanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Куда", style = MaterialTheme.typography.labelLarge)
            ExposedDropdownMenuBox(expanded = toExpanded, onExpandedChange = { toExpanded = it }) {
                OutlinedTextField(
                    value = accounts.find { it.id == toId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(toExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(toExpanded, onDismissRequest = { toExpanded = false }) {
                    accounts.forEach {
                        DropdownMenuItem(text = { Text("${it.name} (${formatZdr(it.balance)})") }, onClick = {
                            toId = it.id; toExpanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter(Char::isDigit) },
                label = { Text("Сумма, ЗДР") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    vm.prepareTransfer(fromId, toId, amt)
                },
                Modifier.fillMaxWidth()
            ) { Text("Проверить перевод") }
        }
    }

    preview?.let { p ->
        AlertDialog(
            onDismissRequest = { vm.cancelTransferPreview() },
            title = { Text("Подтверждение перевода") },
            text = {
                Column {
                    Text("С ${p.from.name} → ${p.to.name}")
                    Text("Сумма: ${formatZdr(p.amount)}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Баланс после:")
                    Text("• ${p.from.name}: ${formatZdr(p.fromAfter)}")
                    Text("• ${p.to.name}: ${formatZdr(p.toAfter)}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.confirmTransfer { tx -> onReceipt(tx.id) }
                }) { Text("Подтвердить") }
            },
            dismissButton = {
                TextButton(onClick = { vm.cancelTransferPreview() }) { Text("Отмена") }
            }
        )
    }
}
