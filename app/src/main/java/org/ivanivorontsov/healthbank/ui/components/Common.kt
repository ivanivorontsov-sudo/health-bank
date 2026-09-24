package org.ivanivorontsov.healthbank.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ivanivorontsov.healthbank.data.entity.TransactionEntity
import org.ivanivorontsov.healthbank.ui.theme.CreditGreen
import org.ivanivorontsov.healthbank.ui.theme.DebitRed
import org.ivanivorontsov.healthbank.util.formatDateTime
import org.ivanivorontsov.healthbank.util.formatZdr
import org.ivanivorontsov.healthbank.util.statusRu

@Composable
fun BalanceHero(label: String, amount: Long, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                formatZdr(amount),
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text("единицы здоровья", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .width(72.dp)
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 2)
    }
}

@Composable
fun TxRow(tx: TransactionEntity, onClick: (() -> Unit)? = null) {
    val amountColor = when (tx.type) {
        "CREDIT" -> CreditGreen
        "DEBIT" -> DebitRed
        else -> MaterialTheme.colorScheme.onSurface
    }
    val prefix = when (tx.type) {
        "CREDIT" -> "+"
        "DEBIT" -> "−"
        "TRANSFER" -> "↔ "
        else -> ""
    }
    ListItem(
        headlineContent = { Text(tx.title, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text("${tx.category} · ${formatDateTime(tx.createdAt)} · ${statusRu(tx.status)}")
        },
        leadingContent = {
            Icon(iconForName(tx.icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Text(
                "$prefix${formatZdr(tx.amount)}",
                color = amountColor,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    )
}

fun iconForName(name: String): ImageVector = when (name) {
    "favorite" -> Icons.Default.Favorite
    "fitness", "fitness_center" -> Icons.Default.FitnessCenter
    "medical", "health_and_safety" -> Icons.Default.HealthAndSafety
    "bedtime" -> Icons.Default.Bedtime
    "directions_run" -> Icons.Default.DirectionsRun
    "self_improvement" -> Icons.Default.SelfImprovement
    "pool" -> Icons.Default.Pool
    "directions_walk" -> Icons.Default.DirectionsWalk
    "pedal_bike" -> Icons.Default.PedalBike
    "assignment" -> Icons.Default.Assignment
    "dentistry" -> Icons.Default.MedicalServices
    "visibility" -> Icons.Default.Visibility
    "bloodtype" -> Icons.Default.Bloodtype
    "vaccines" -> Icons.Default.Vaccines
    "coronavirus" -> Icons.Default.Coronavirus
    "water_drop" -> Icons.Default.WaterDrop
    "spa" -> Icons.Default.Spa
    "accessibility" -> Icons.Default.Accessibility
    "smoke_free" -> Icons.Default.SmokeFree
    "swap_horiz" -> Icons.Default.SwapHoriz
    else -> Icons.Default.AccountBalanceWallet
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun EmptyState(text: String) {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
