package org.ivanivorontsov.healthbank.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.ivanivorontsov.healthbank.ui.components.BankTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onProfile: () -> Unit,
    onHistory: () -> Unit,
    onInsights: () -> Unit,
    onTransfer: () -> Unit
) {
    Scaffold(topBar = { BankTopBar("Ещё") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                ListItem(
                    headlineContent = { Text("Профиль и настройки") },
                    leadingContent = { Icon(Icons.Default.Person, null) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                    modifier = Modifier.clickable(onClick = onProfile)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("История операций") },
                    leadingContent = { Icon(Icons.Default.History, null) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                    modifier = Modifier.clickable(onClick = onHistory)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Аналитика") },
                    leadingContent = { Icon(Icons.Default.Insights, null) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                    modifier = Modifier.clickable(onClick = onInsights)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("Перевод между счетами") },
                    leadingContent = { Icon(Icons.Default.SwapHoriz, null) },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                    modifier = Modifier.clickable(onClick = onTransfer)
                )
                HorizontalDivider()
            }
            item {
                ListItem(
                    headlineContent = { Text("О приложении") },
                    supportingContent = { Text("Банк здоровья · ЗДР — вымышленные единицы здоровья · v1.0.0") },
                    leadingContent = { Icon(Icons.Default.Info, null) }
                )
            }
        }
    }
}
