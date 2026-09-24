package org.ivanivorontsov.healthbank.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import org.ivanivorontsov.healthbank.ui.screens.accounts.AccountDetailScreen
import org.ivanivorontsov.healthbank.ui.screens.accounts.AccountsScreen
import org.ivanivorontsov.healthbank.ui.screens.cards.CardsScreen
import org.ivanivorontsov.healthbank.ui.screens.history.HistoryScreen
import org.ivanivorontsov.healthbank.ui.screens.home.HomeScreen
import org.ivanivorontsov.healthbank.ui.screens.insights.InsightsScreen
import org.ivanivorontsov.healthbank.ui.screens.onboarding.OnboardingScreen
import org.ivanivorontsov.healthbank.ui.screens.payments.PaymentsScreen
import org.ivanivorontsov.healthbank.ui.screens.profile.MoreScreen
import org.ivanivorontsov.healthbank.ui.screens.profile.ProfileScreen
import org.ivanivorontsov.healthbank.ui.screens.receipt.ReceiptScreen
import org.ivanivorontsov.healthbank.ui.screens.transfers.TransferScreen
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

sealed class Dest(val route: String, val label: String, val icon: ImageVector, val selected: ImageVector) {
    data object Home : Dest("home", "Главная", Icons.Outlined.Home, Icons.Filled.Home)
    data object Accounts : Dest("accounts", "Счета", Icons.Outlined.AccountBalanceWallet, Icons.Filled.AccountBalanceWallet)
    data object Payments : Dest("payments", "Платежи", Icons.Outlined.Payments, Icons.Filled.Payments)
    data object Cards : Dest("cards", "Карты", Icons.Outlined.CreditCard, Icons.Filled.CreditCard)
    data object More : Dest("more", "Ещё", Icons.Outlined.MoreHoriz, Icons.Filled.MoreHoriz)
}

val bottomTabs = listOf(Dest.Home, Dest.Accounts, Dest.Payments, Dest.Cards, Dest.More)

@Composable
fun HealthBankNav(vm: AppViewModel, onboardingDone: Boolean) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    val showBottom = bottomTabs.any { it.route == current }

    Scaffold(
        bottomBar = {
            if (showBottom) {
                NavigationBar {
                    bottomTabs.forEach { dest ->
                        val selected = current == dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(dest.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (selected) dest.selected else dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = if (onboardingDone) Dest.Home.route else "onboarding",
            modifier = Modifier.padding(padding)
        ) {
            composable("onboarding") {
                OnboardingScreen(vm) {
                    nav.navigate(Dest.Home.route) { popUpTo("onboarding") { inclusive = true } }
                }
            }
            composable(Dest.Home.route) {
                HomeScreen(
                    vm = vm,
                    onTransfer = { nav.navigate("transfer") },
                    onPayments = { nav.navigate(Dest.Payments.route) },
                    onHistory = { nav.navigate("history") },
                    onInsights = { nav.navigate("insights") },
                    onAccount = { nav.navigate("account/$it") },
                    onReceipt = { nav.navigate("receipt/$it") }
                )
            }
            composable(Dest.Accounts.route) {
                AccountsScreen(vm, onAccount = { nav.navigate("account/$it") }, onTransfer = { nav.navigate("transfer") })
            }
            composable(
                "account/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: return@composable
                AccountDetailScreen(vm, id, onBack = { nav.popBackStack() }, onReceipt = { nav.navigate("receipt/$it") })
            }
            composable(Dest.Payments.route) {
                PaymentsScreen(vm, onReceipt = { nav.navigate("receipt/$it") })
            }
            composable(Dest.Cards.route) { CardsScreen(vm) }
            composable(Dest.More.route) {
                MoreScreen(
                    onProfile = { nav.navigate("profile") },
                    onHistory = { nav.navigate("history") },
                    onInsights = { nav.navigate("insights") },
                    onTransfer = { nav.navigate("transfer") }
                )
            }
            composable("transfer") {
                TransferScreen(vm, onBack = { nav.popBackStack() }, onReceipt = { id ->
                    nav.navigate("receipt/$id") { popUpTo(Dest.Home.route) }
                })
            }
            composable("history") {
                HistoryScreen(vm, onBack = { nav.popBackStack() }, onReceipt = { nav.navigate("receipt/$it") })
            }
            composable("insights") {
                InsightsScreen(vm, onBack = { nav.popBackStack() })
            }
            composable("profile") {
                ProfileScreen(vm, onBack = { nav.popBackStack() })
            }
            composable(
                "receipt/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: return@composable
                ReceiptScreen(vm, id, onBack = { nav.popBackStack() })
            }
        }
    }
}
