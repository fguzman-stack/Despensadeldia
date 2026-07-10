package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.AppDatabase
import com.example.data.repository.PantryRepository
import com.example.ui.ads.AdManager
import com.example.ui.ads.ConsentManager
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UseFirstScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PantryViewModel
import com.example.widget.PantryWidgetProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local SQLite Database and Repository Initialization
        val database = AppDatabase.getDatabase(this)
        val repository = PantryRepository(database.pantryDao())
        val viewModel = PantryViewModel(repository)

        setContent {
            val settings by viewModel.settingsState.collectAsState()

            // UMP consent and AdMob initialization
            LaunchedEffect(Unit) {
                ConsentManager.requestConsent(this@MainActivity)
                AdManager.initialize(this@MainActivity)
            }

            // Request Notification Permission on Android 13+ (API 33)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val context = LocalContext.current
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (isGranted) {
                            PantryWidgetProvider.triggerUpdate(context)
                        }
                    }
                )
                LaunchedEffect(Unit) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Handle Dynamic Dark / Light theme selection
            val darkTheme = when (settings.theme) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (settings.countryName.isEmpty()) {
                        // Mandatory setup first
                        SetupScreen(
                            viewModel = viewModel,
                            onSetupComplete = {
                                // Trigger widget update when preferences change
                                PantryWidgetProvider.triggerUpdate(this@MainActivity)
                            }
                        )
                    } else if (!settings.onboardingCompleted) {
                        // Onboarding screens
                        OnboardingScreen(
                            onFinished = {
                                viewModel.setOnboardingCompleted()
                            }
                        )
                    } else {
                        // Main App Flow
                        PantryAppContainer(viewModel = viewModel)
                    }
                }
            }

            // Trigger widget update on app open
            LaunchedEffect(Unit) {
                PantryWidgetProvider.triggerUpdate(this@MainActivity)
            }
        }
    }
}

@Composable
fun PantryAppContainer(viewModel: PantryViewModel) {
    var selectedTab by remember { mutableStateOf("use_first") }
    val context = LocalContext.current

    // Automatically trigger home widget updates whenever database items change
    val activeProducts by viewModel.activeProductsState.collectAsState()
    LaunchedEffect(activeProducts) {
        PantryWidgetProvider.triggerUpdate(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.testTag("pantry_bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == "use_first",
                    onClick = { selectedTab = "use_first" },
                    label = { Text("Usa Primero") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "use_first") Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Usa Primero"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.testTag("nav_item_use_first")
                )
                NavigationBarItem(
                    selected = selectedTab == "inventory",
                    onClick = { selectedTab = "inventory" },
                    label = { Text("Inventario") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "inventory") Icons.Filled.Kitchen else Icons.Outlined.Kitchen,
                            contentDescription = "Inventario"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.testTag("nav_item_inventory")
                )
                NavigationBarItem(
                    selected = selectedTab == "stats",
                    onClick = { selectedTab = "stats" },
                    label = { Text("Estadísticas") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "stats") Icons.Filled.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Estadísticas"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.testTag("nav_item_stats")
                )
                NavigationBarItem(
                    selected = selectedTab == "settings",
                    onClick = { selectedTab = "settings" },
                    label = { Text("Ajustes") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "settings") Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Ajustes"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = selectedTab, animationSpec = tween(300)) { tab ->
            when (tab) {
                "use_first" -> UseFirstScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                "inventory" -> DashboardScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                "stats" -> StatsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
                "settings" -> SettingsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
