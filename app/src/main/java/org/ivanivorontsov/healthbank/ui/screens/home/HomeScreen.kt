package org.ivanivorontsov.healthbank.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.*
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: AppViewModel,
    onTransfer: () -> Unit,
    onPayments: () -> Unit,
    onHistory: () -> Unit,
    onInsights: () -> Unit,
    onAccount: (String) -> Unit,
    onReceipt: (String) -> Unit
) {
    val balance by vm.totalBalance.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val txs by vm.transactions.collectAsState()
    val prefs by vm.prefs.collectAsState()
    val pending = txs.filter { it.status == "PENDING" }
    val recent = txs.filter { it.status == "COMPLETED" }.take(5)
    val greeting = prefs?.displayName?.takeIf { it.isNotBlank() }?.let { "Здравствуйте, $it" } ?: "Здравствуйте"

    Scaffold(topBar = { BankTopBar("Банк здоровья") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Text(greeting, Modifier.padding(horizontal = 16.dp, vertical = 4.dp), style = MaterialTheme.typography.titleMedium)
                BalanceHero("Общий капитал здоровья", balance, Modifier.padding(16.dp))
            }
            item {
                LazyRow(Modifier.padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    item { QuickAction(Icons.Default.SwapHoriz, "Перевод", onTransfer) }
                    item { QuickAction(Icons.Default.Payments, "Операции", onPayments) }
                    item { QuickAction(Icons.Default.History, "История", onHistory) }
                    item { QuickAction(Icons.Default.Insights, "Аналитика", onInsights) }
                }
            }
            if (pending.isNotEmpty()) {
                item { SectionTitle("Ожидают / запланированы") }
                items(pending) { tx -> TxRow(tx) { onReceipt(tx.id) } }
            }
            item { SectionTitle("Счета") }
            item {
                LazyRow(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(accounts) { acc ->
                        Card(
                            Modifier.width(160.dp).clickable { onAccount(acc.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Icon(iconForName(acc.icon), null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                Text(acc.name, style = MaterialTheme.typography.labelMedium, maxLines = 2)
                                Text(formatZdr(acc.balance), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Последние операции", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = onHistory) { Text("Все") }
                }
            }
            items(recent) { tx -> TxRow(tx) { onReceipt(tx.id) } }
            if (recent.isEmpty()) item { EmptyState("Пока нет операций") }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
