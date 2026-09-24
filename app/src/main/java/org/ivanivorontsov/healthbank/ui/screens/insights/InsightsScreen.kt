package org.ivanivorontsov.healthbank.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.ui.components.BankTopBar
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(vm: AppViewModel, onBack: () -> Unit) {
    var period by remember { mutableStateOf("month") }
    var data by remember { mutableStateOf<AppViewModel.InsightData?>(null) }

    LaunchedEffect(period) {
        val cal = Calendar.getInstance()
        val to = cal.timeInMillis
        when (period) {
            "month" -> cal.add(Calendar.MONTH, -1)
            "year" -> cal.add(Calendar.YEAR, -1)
            else -> cal.add(Calendar.DAY_OF_YEAR, -7)
        }
        data = vm.insights(cal.timeInMillis, to)
    }

    Scaffold(topBar = { BankTopBar("Аналитика капитала", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = period == "week", onClick = { period = "week" }, label = { Text("Неделя") })
                FilterChip(selected = period == "month", onClick = { period = "month" }, label = { Text("Месяц") })
                FilterChip(selected = period == "year", onClick = { period = "year" }, label = { Text("Год") })
            }
            Spacer(Modifier.height(16.dp))
            data?.let { d ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Итого зачислено", style = MaterialTheme.typography.labelMedium)
                        Text(formatZdr(d.totalCredits), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Переводов: ${formatZdr(d.totalTransfers)}")
                        Text("Операций: ${d.count}")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("По категориям", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                val max = d.byCategory.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
                d.byCategory.forEach { (cat, sum) ->
                    Column(Modifier.padding(vertical = 6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cat)
                            Text(formatZdr(sum), fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .fillMaxWidth(sum.toFloat() / max)
                                .height(10.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                        )
                    }
                }
                if (d.byCategory.isEmpty()) {
                    Text("Нет данных за период", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } ?: Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
