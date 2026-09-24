package org.ivanivorontsov.healthbank.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.ui.components.EmptyState
import org.ivanivorontsov.healthbank.ui.components.TxRow
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: AppViewModel, onBack: () -> Unit, onReceipt: (String) -> Unit) {
    val query by vm.searchQuery.collectAsState()
    val txs by vm.filteredTx.collectAsState()
    var statusFilter by remember { mutableStateOf("ALL") }
    val filtered = when (statusFilter) {
        "ALL" -> txs
        else -> txs.filter { it.status == statusFilter }
    }

    Scaffold(topBar = { BankTopBar("История операций", onBack = onBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { vm.setSearch(it) },
                label = { Text("Поиск") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true
            )
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = statusFilter == "ALL", onClick = { statusFilter = "ALL" }, label = { Text("Все") })
                FilterChip(selected = statusFilter == "COMPLETED", onClick = { statusFilter = "COMPLETED" }, label = { Text("Исполнено") })
                FilterChip(selected = statusFilter == "PENDING", onClick = { statusFilter = "PENDING" }, label = { Text("Ожидание") })
                FilterChip(selected = statusFilter == "FAILED", onClick = { statusFilter = "FAILED" }, label = { Text("Ошибка") })
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { tx -> TxRow(tx) { onReceipt(tx.id) } }
                if (filtered.isEmpty()) item { EmptyState("Ничего не найдено") }
            }
        }
    }
}
