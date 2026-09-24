package org.ivanivorontsov.healthbank.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BankBlue = Color(0xFF0D47A1)
val BankBlueLight = Color(0xFF5472D3)
val BankTeal = Color(0xFF00838F)
val BankGreen = Color(0xFF2E7D32)
val BankPurple = Color(0xFF6A1B9A)
val BankBg = Color(0xFFF5F7FA)
val BankCard = Color(0xFFFFFFFF)
val CreditGreen = Color(0xFF1B5E20)
val DebitRed = Color(0xFFB71C1C)

private val LightColors = lightColorScheme(
    primary = BankBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    secondary = BankTeal,
    tertiary = BankGreen,
    background = BankBg,
    surface = BankCard,
    surfaceVariant = Color(0xFFE8EEF5),
    error = DebitRed
)

@Composable
fun HealthBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
