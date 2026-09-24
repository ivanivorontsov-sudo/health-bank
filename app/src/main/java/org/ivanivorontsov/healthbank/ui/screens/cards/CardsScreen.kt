package org.ivanivorontsov.healthbank.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.data.entity.CardEntity
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(vm: AppViewModel) {
    val cards by vm.cards.collectAsState()
    val accounts by vm.accounts.collectAsState()
    var limitCard by remember { mutableStateOf<CardEntity?>(null) }
    var limitText by remember { mutableStateOf("") }

    Scaffold(topBar = { BankTopBar("Карты здоровья") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(cards) { card ->
                val color = try { Color(android.graphics.Color.parseColor(card.colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.7f))))
                            .padding(20.dp)
                    ) {
                        Text(card.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("•••• ${card.lastFour}", color = Color.White.copy(0.9f), style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        val linked = accounts.find { it.id == card.linkedAccountId }?.name ?: ""
                        Text("Счёт: $linked", color = Color.White.copy(0.85f), style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Лимит: ${formatZdr(card.monthlyLimit)} · потрачено ${formatZdr(card.spentThisMonth)}",
                            color = Color.White.copy(0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (card.isFrozen) {
                            Spacer(Modifier.height(4.dp))
                            Text("❄ ЗАМОРОЖЕНА", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { vm.setCardFrozen(card.id, !card.isFrozen) }, Modifier.weight(1f)) {
                        Text(if (card.isFrozen) "Разморозить" else "Заморозить")
                    }
                    OutlinedButton(onClick = {
                        limitCard = card
                        limitText = card.monthlyLimit.toString()
                    }, Modifier.weight(1f)) {
                        Text("Лимит")
                    }
                }
            }
        }
    }

    limitCard?.let { card ->
        AlertDialog(
            onDismissRequest = { limitCard = null },
            title = { Text("Месячный лимит") },
            text = {
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it.filter(Char::isDigit) },
                    label = { Text("ЗДР") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    limitText.toLongOrNull()?.let { vm.updateCardLimit(card.id, it) }
                    limitCard = null
                }) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { limitCard = null }) { Text("Отмена") } }
        )
    }
}
