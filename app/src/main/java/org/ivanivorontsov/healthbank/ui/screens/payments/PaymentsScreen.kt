package org.ivanivorontsov.healthbank.ui.screens.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.data.entity.OperationCatalogEntity
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.ui.components.iconForName
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(vm: AppViewModel, onReceipt: (String) -> Unit) {
    val catalog by vm.catalog.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val receipt by vm.lastReceipt.collectAsState()
    var selected by remember { mutableStateOf<OperationCatalogEntity?>(null) }
    var accountId by remember { mutableStateOf<String?>(null) }
    val grouped = catalog.groupBy { it.category }

    LaunchedEffect(receipt) {
        receipt?.let {
            onReceipt(it.id)
            vm.clearReceipt()
        }
    }

    Scaffold(topBar = { BankTopBar("Платежи и операции") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Text(
                    "Зафиксируйте активность — на счёт зачислится номинал в ЗДР",
                    Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            grouped.forEach { (cat, ops) ->
                item {
                    Text(
                        cat,
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(ops) { op ->
                    ListItem(
                        headlineContent = { Text(op.title) },
                        supportingContent = { Text(op.description.ifBlank { op.category }) },
                        leadingContent = { Icon(iconForName(op.icon), null, tint = MaterialTheme.colorScheme.primary) },
                        trailingContent = {
                            Text("+${formatZdr(op.nominalValue)}", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        },
                        modifier = Modifier.clickable {
                            selected = op
                            accountId = op.defaultAccountId
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    selected?.let { op ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("Провести операцию?") },
            text = {
                Column {
                    Text("${op.title}\n+${formatZdr(op.nominalValue)}")
                    Spacer(Modifier.height(12.dp))
                    Text("Счёт зачисления:", style = MaterialTheme.typography.labelMedium)
                    accounts.forEach { acc ->
                        Row(Modifier.clickable { accountId = acc.id }.padding(vertical = 4.dp)) {
                            RadioButton(selected = accountId == acc.id, onClick = { accountId = acc.id })
                            Text(acc.name, Modifier.padding(start = 8.dp, top = 12.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.performOperation(op.id, accountId)
                    selected = null
                }) { Text("Провести") }
            },
            dismissButton = { TextButton(onClick = { selected = null }) { Text("Отмена") } }
        )
    }
}
