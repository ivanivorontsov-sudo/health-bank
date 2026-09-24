package org.ivanivorontsov.healthbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ivanivorontsov.healthbank.ui.navigation.HealthBankNav
import org.ivanivorontsov.healthbank.ui.theme.HealthBankTheme
import org.ivanivorontsov.healthbank.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as HealthBankApp
        setContent {
            HealthBankTheme {
                Surface(Modifier.fillMaxSize()) {
                    val vm: AppViewModel = viewModel(factory = AppViewModel.Factory(app.repository))
                    val prefs by vm.prefs.collectAsState()
                    val ready = prefs != null
                    if (ready) {
                        HealthBankNav(vm, onboardingDone = prefs!!.onboardingDone)
                    }
                }
            }
        }
    }
}
