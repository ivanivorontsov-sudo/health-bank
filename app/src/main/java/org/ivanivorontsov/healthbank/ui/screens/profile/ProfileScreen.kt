package org.ivanivorontsov.healthbank.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: AppViewModel, onBack: () -> Unit) {
    val prefs by vm.prefs.collectAsState()
    val message by vm.message.collectAsState()
    var name by remember(prefs) { mutableStateOf(prefs?.displayName ?: "") }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var notifyOps by remember(prefs) { mutableStateOf(prefs?.notifyOps ?: true) }
    var notifyInsights by remember(prefs) { mutableStateOf(prefs?.notifyInsights ?: true) }

    Scaffold(topBar = { BankTopBar("Профиль и настройки", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Отображаемое имя") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.updateName(name) }) { Text("Сохранить имя") }

            Spacer(Modifier.height(24.dp))
            Text("Смена PIN", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = oldPin,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) oldPin = it },
                label = { Text("Текущий PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newPin,
                onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) newPin = it },
                label = { Text("Новый PIN (4–6 цифр)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (newPin.length in 4..6) vm.changePin(oldPin, newPin) {
                    if (it) { oldPin = ""; newPin = "" }
                }
            }) { Text("Изменить PIN") }

            Spacer(Modifier.height(24.dp))
            Text("Уведомления (локальные предпочтения)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Операции")
                Switch(checked = notifyOps, onCheckedChange = {
                    notifyOps = it
                    vm.updateNotify(it, notifyInsights)
                })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Аналитика")
                Switch(checked = notifyInsights, onCheckedChange = {
                    notifyInsights = it
                    vm.updateNotify(notifyOps, it)
                })
            }

            message?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.primary)
                LaunchedEffect(it) {
                    kotlinx.coroutines.delay(2500)
                    vm.clearMessage()
                }
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "Банк здоровья — учёт капитала здоровья в номинальных единицах ЗДР. Не является финансовым продуктом. Деньги не используются.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
