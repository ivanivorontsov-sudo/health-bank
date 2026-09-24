package org.ivanivorontsov.healthbank.ui.screens.receipt

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.ui.components.EmptyState
import org.ivanivorontsov.healthbank.util.*
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(vm: AppViewModel, txId: String, onBack: () -> Unit) {
    val txs by vm.transactions.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val tx = txs.find { it.id == txId }

    Scaffold(topBar = { BankTopBar("Квитанция", onBack = onBack) }) { padding ->
        if (tx == null) {
            Box(Modifier.fillMaxSize().padding(padding)) { EmptyState("Операция не найдена") }
            return@Scaffold
        }
        val acc = accounts.find { it.id == tx.accountId }
        val counter = tx.counterAccountId?.let { id -> accounts.find { it.id == id } }
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CheckCircle, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.tertiary)
            Spacer(Modifier.height(12.dp))
            Text(statusRu(tx.status), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                when (tx.type) {
                    "CREDIT" -> "+${formatZdr(tx.amount)}"
                    "DEBIT" -> "−${formatZdr(tx.amount)}"
                    else -> formatZdr(tx.amount)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReceiptLine("Операция", tx.title)
                    ReceiptLine("Категория", tx.category)
                    ReceiptLine("Тип", typeRu(tx.type))
                    ReceiptLine("Счёт", acc?.name ?: tx.accountId)
                    if (counter != null) ReceiptLine("Корр. счёт", counter.name)
                    ReceiptLine("Дата", formatDateTime(tx.createdAt))
                    tx.note?.let { ReceiptLine("Комментарий", it) }
                    ReceiptLine("ID", tx.id.take(8) + "…")
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack, Modifier.fillMaxWidth()) { Text("Готово") }
        }
    }
}

@Composable
private fun ReceiptLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f, fill = false))
    }
}
