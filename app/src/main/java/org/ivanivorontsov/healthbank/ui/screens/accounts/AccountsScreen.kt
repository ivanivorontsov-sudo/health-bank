package org.ivanivorontsov.healthbank.ui.screens.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.ui.components.iconForName
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(vm: AppViewModel, onAccount: (String) -> Unit, onTransfer: () -> Unit) {
    val accounts by vm.accounts.collectAsState()
    Scaffold(
        topBar = {
            BankTopBar("Счета", actions = {
                IconButton(onClick = onTransfer) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Перевод")
                }
            })
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(accounts) { acc ->
                ListItem(
                    headlineContent = { Text(acc.name, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(acc.type) },
                    leadingContent = { Icon(iconForName(acc.icon), null, tint = MaterialTheme.colorScheme.primary) },
                    trailingContent = { Text(formatZdr(acc.balance), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.clickable { onAccount(acc.id) }
                )
                HorizontalDivider()
            }
        }
    }
}
