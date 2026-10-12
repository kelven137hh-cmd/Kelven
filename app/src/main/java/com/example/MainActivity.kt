package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.ZapRepository
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.MerchantDashboardScreen
import com.example.ui.screens.PlanUpgradeScreen
import com.example.ui.screens.PublicCatalogScreen
import com.example.ui.screens.StoreWizardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ZapViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(applicationContext)
    val repository = ZapRepository(database)
    val factory = ZapViewModel.Factory(repository)
    val viewModel = ViewModelProvider(this, factory)[ZapViewModel::class.java]

    setContent {
      MyApplicationTheme {
        ZapApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun ZapApp(viewModel: ZapViewModel) {
  val context = LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()

  // Display user toasts
  LaunchedEffect(userMessage) {
    userMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearMessage()
    }
  }

  // Handle system back navigation
  BackHandler(enabled = currentScreen !is AppScreen.Landing) {
    when (currentScreen) {
      is AppScreen.PublicCatalog -> viewModel.navigateTo(AppScreen.Landing)
      is AppScreen.Dashboard -> viewModel.navigateTo(AppScreen.Landing)
      is AppScreen.Wizard -> viewModel.navigateTo(AppScreen.Landing)
      is AppScreen.Auth -> viewModel.navigateTo(AppScreen.Landing)
      is AppScreen.AdminPanel -> viewModel.navigateTo(AppScreen.Landing)
      is AppScreen.PlanUpgrade -> viewModel.navigateTo(AppScreen.Dashboard)
      else -> {}
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      when (val screen = currentScreen) {
        is AppScreen.Landing -> {
          LandingScreen(
            viewModel = viewModel,
            onNavigateToAuth = { viewModel.navigateTo(AppScreen.Auth) },
            onNavigateToWizard = { viewModel.navigateTo(AppScreen.Wizard) },
            onOpenStoreCatalog = { slug -> viewModel.navigateTo(AppScreen.PublicCatalog(slug)) }
          )
        }

        is AppScreen.Auth -> {
          AuthScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.Landing) }
          )
        }

        is AppScreen.Wizard -> {
          StoreWizardScreen(
            viewModel = viewModel,
            onCancel = { viewModel.navigateTo(AppScreen.Landing) }
          )
        }

        is AppScreen.Dashboard -> {
          MerchantDashboardScreen(
            viewModel = viewModel,
            onOpenStoreCatalog = { slug -> viewModel.navigateTo(AppScreen.PublicCatalog(slug)) },
            onCreateNewStore = { viewModel.navigateTo(AppScreen.Wizard) }
          )
        }

        is AppScreen.PublicCatalog -> {
          PublicCatalogScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.Landing) }
          )
        }

        is AppScreen.AdminPanel -> {
          AdminPanelScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.Landing) }
          )
        }

        is AppScreen.PlanUpgrade -> {
          PlanUpgradeScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(AppScreen.Dashboard) }
          )
        }
      }
    }
  }
}
