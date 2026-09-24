package org.ivanivorontsov.healthbank.ui.screens.accounts

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BalanceHero
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.ui.components.EmptyState
import org.ivanivorontsov.healthbank.ui.components.SectionTitle
import org.ivanivorontsov.healthbank.ui.components.TxRow
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(vm: AppViewModel, accountId: String, onBack: () -> Unit, onReceipt: (String) -> Unit) {
    val accounts by vm.accounts.collectAsState()
    val txs by vm.transactions.collectAsState()
    val acc = accounts.find { it.id == accountId }
    val list = txs.filter { it.accountId == accountId || it.counterAccountId == accountId }
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            BankTopBar(acc?.name ?: "Счёт", onBack = onBack, actions = {
                IconButton(onClick = {
                    vm.statementFor(accountId) { text ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                            putExtra(Intent.EXTRA_SUBJECT, "Выписка — ${acc?.name}")
                        }
                        ctx.startActivity(Intent.createChooser(intent, "Поделиться выпиской"))
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Выписка")
                }
            })
        }
    ) { padding ->
        if (acc == null) {
            Box(Modifier.fillMaxSize().padding(padding)) { EmptyState("Счёт не найден") }
            return@Scaffold
        }
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item { BalanceHero(acc.name, acc.balance, Modifier.padding(16.dp)) }
            item { SectionTitle("Операции по счёту") }
            items(list) { tx -> TxRow(tx) { onReceipt(tx.id) } }
            if (list.isEmpty()) item { EmptyState("Нет операций") }
        }
    }
}
