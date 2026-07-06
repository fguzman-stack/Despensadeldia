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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.local.AppDatabase
import com.example.data.repository.PantryRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.RecipeScreen
import com.example.data.recipe.RecipeRepository
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
        val recipeRepository = RecipeRepository(this)
        val viewModel = PantryViewModel(repository, recipeRepository)

        setContent {
            val settings by viewModel.settingsState.collectAsState()

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

            // Perform automatic daily checks (e.g. streaks) on app open
            LaunchedEffect(Unit) {
                viewModel.checkAndRefreshStreak()
                PantryWidgetProvider.triggerUpdate(this@MainActivity)
            }
        }
    }
}

@Composable
fun PantryAppContainer(viewModel: PantryViewModel) {
    var selectedTab by remember { mutableStateOf("inventory") }
    val context = LocalContext.current

    // Automatically trigger home widget updates whenever database items change
    val activeProducts by viewModel.activeProductsState.collectAsState()
    LaunchedEffect(activeProducts) {
        PantryWidgetProvider.triggerUpdate(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("pantry_bottom_nav_bar")
            ) {
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
                    modifier = Modifier.testTag("nav_item_settings")
                )
                NavigationBarItem(
                    selected = selectedTab == "chef",
                    onClick = { selectedTab = "chef" },
                    label = { Text("Chef") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "chef") Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                            contentDescription = "Chef"
                        )
                    },
                    modifier = Modifier.testTag("nav_item_chef")
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
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
            "chef" -> RecipeScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
