package com.example.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.local.AppSettings
import com.example.data.remote.FrankfurterService
import com.example.receiver.NotificationReceiver
import java.util.Locale
import com.example.ui.viewmodel.PantryViewModel
import com.example.utils.BackupHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsState()

    var currencySymbol by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf("SYSTEM") }
    var notificationEnabled by remember { mutableStateOf(true) }
    var notificationHour by remember { mutableStateOf(9) }
    var notificationMinute by remember { mutableStateOf(0) }
    var geminiApiKey by remember { mutableStateOf("") }
    var exchangeRates by remember { mutableStateOf<Map<String, Double>?>(null) }
    var convertingCurrency by remember { mutableStateOf(false) }
    var importPreview by remember { mutableStateOf<BackupHelper.ImportPreview?>(null) }
    var importError by remember { mutableStateOf<String?>(null) }

    // Initialize state from DB values
    LaunchedEffect(settings) {
        settings?.let {
            currencySymbol = it.currencySymbol
            selectedTheme = it.theme
            notificationEnabled = it.notificationEnabled
            notificationHour = it.notificationHour
            notificationMinute = it.notificationMinute
            geminiApiKey = it.geminiApiKey
        }
    }

    var showTimeDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            notificationEnabled = isGranted
            saveChanges(viewModel, settings, currencySymbol, selectedTheme, isGranted, notificationHour, notificationMinute, context)
        }
    )
    
    val scope = rememberCoroutineScope()
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    try {
                        val json = context.contentResolver.openInputStream(uri)
                            ?.bufferedReader()
                            ?.use { it.readText() }
                            .orEmpty()
                        importPreview = BackupHelper.parseProductsJson(json)
                        importError = null
                    } catch (e: Exception) {
                        importPreview = null
                        importError = e.message ?: context.getString(R.string.import_error)
                    }
                }
            }
        }
    )

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientGradientBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 1. Notification Schedule Section
            SettingsSection(title = stringResource(R.string.section_notifications)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.notification_title),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.notification_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!hasPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    notificationEnabled = true
                                    saveChanges(viewModel, settings, currencySymbol, selectedTheme, true, notificationHour, notificationMinute, context)
                                }
                            } else {
                                notificationEnabled = isChecked
                                saveChanges(viewModel, settings, currencySymbol, selectedTheme, isChecked, notificationHour, notificationMinute, context)
                            }
                        },
                        modifier = Modifier.testTag("settings_notifications_switch")
                    )
                }

                if (notificationEnabled) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.notification_time)) },
                        supportingContent = { 
                            Text(String.format(null, "%02d:%02d", notificationHour, notificationMinute)) 
                        },
                        leadingContent = { 
                            Icon(Icons.Filled.AccessTime, contentDescription = null) 
                        },
                        trailingContent = {
                            TextButton(onClick = { showTimeDialog = true }) {
                                Text(stringResource(R.string.modify))
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                }

                // Push alert instant simulation button (Extremely interactive)
                Button(
                    onClick = {
                        simulateInstantNotification(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_test_notification_button")
                ) {
                    Icon(imageVector = Icons.Filled.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.test_notification))
                }
            }

            // 3. Application Visual Theme Section
            SettingsSection(title = stringResource(R.string.section_appearance)) {
                Text(
                    text = stringResource(R.string.theme_label),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                val themes = listOf(
                    Triple("SYSTEM", stringResource(R.string.theme_system), Icons.Filled.SettingsSystemDaydream),
                    Triple("LIGHT", stringResource(R.string.theme_light), Icons.Filled.LightMode),
                    Triple("DARK", stringResource(R.string.theme_dark), Icons.Filled.DarkMode),
                    Triple("ASTRAL", stringResource(R.string.theme_fruit_pop), Icons.Filled.VolunteerActivism)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    themes.chunked(2).forEach { rowThemes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowThemes.forEach { (themeCode, themeLabel, themeIcon) ->
                                val isSelected = selectedTheme == themeCode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(58.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f)
                                        )
                                        .clickable {
                                            selectedTheme = themeCode
                                            saveChanges(viewModel, settings, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = themeIcon,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = themeLabel,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            if (rowThemes.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            // 4. Data Backup Section
            SettingsSection(title = stringResource(R.string.section_data)) {
                Text(
                    text = stringResource(R.string.data_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val products = viewModel.getAllProductsDirect()
                                BackupHelper.exportToJson(context, products)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.export_json))
                    }

                    OutlinedButton(
                        onClick = { 
                            scope.launch {
                                val products = viewModel.getAllProductsDirect()
                                BackupHelper.exportToCsv(context, products)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.TableView, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.export_csv))
                    }
                }

                Button(
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.import_json))
                }

                Text(
                    text = stringResource(R.string.import_json_warning),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 5. Currency Converter Section
            SettingsSection(title = "Moneda") {
                OutlinedTextField(
                    value = currencySymbol,
                    onValueChange = { 
                        currencySymbol = it
                        saveChanges(viewModel, settings, it, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                    },
                    label = { Text("Símbolo de moneda principal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tasas de cambio actuales (fuente: BCE)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                convertingCurrency = true
                                val rates = FrankfurterService.getAllRates("USD")
                                if (rates == null) {
                                    android.widget.Toast.makeText(context, "Error al obtener tasas de cambio. Verifica tu conexión.", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                exchangeRates = rates
                                convertingCurrency = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !convertingCurrency
                    ) {
                        Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (convertingCurrency) "Cargando..." else "Ver tasas USD")
                    }
                }
                if (exchangeRates != null) {
                    val rates = exchangeRates!!
                    val majors = listOf("EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "BRL", "ARS", "MXN")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        majors.filter { it in rates }.forEach { code ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(code, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(
                                    "${rates[code]?.let { String.format("%.4f", it) } ?: "-"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Actualizado: ${java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date())}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 6. AI Assistant Section
            SettingsSection(title = "Asistente IA") {
                Text(
                    text = "El asistente funciona sin configuración. Si deseas respuestas más avanzadas, puedes añadir una API Key de Gemini (gratis en aistudio.google.com) — opcional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = geminiApiKey,
                    onValueChange = { geminiApiKey = it },
                    label = { Text("Gemini API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("AIza...") }
                )
                Button(
                    onClick = {
                        saveChangesWithGemini(
                            viewModel, settings, currencySymbol, selectedTheme,
                            notificationEnabled, notificationHour, notificationMinute,
                            geminiApiKey, context
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = geminiApiKey.isNotBlank()
                ) {
                    Text("Guardar API Key")
                }
            }

            // 7. Privacy & Info Section
            SettingsSection(title = stringResource(R.string.section_about)) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.privacy_title)) },
                    supportingContent = { Text(stringResource(R.string.privacy_description)) },
                    leadingContent = { Icon(Icons.Filled.PrivacyTip, contentDescription = null) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showPrivacyDialog = true }
                )


            }
            }
        }
    }
    
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacy_title)) },
            text = {
                Text(stringResource(R.string.privacy_detail))
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text(stringResource(R.string.understood)) }
            }
        )
    }

    importError?.let { message ->
        AlertDialog(
            onDismissRequest = { importError = null },
            title = { Text(stringResource(R.string.import_error_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { importError = null }) { Text(stringResource(R.string.ok)) }
            }
        )
    }

    importPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = { importPreview = null },
            title = { Text(stringResource(R.string.import_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.import_confirm_message,
                        preview.products.size,
                        preview.skipped
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.replaceAllProducts(preview.products)
                        importPreview = null
                        android.widget.Toast.makeText(
                            context,
                            context.getString(R.string.import_success, preview.products.size),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    },
                    enabled = preview.products.isNotEmpty()
                ) { Text(stringResource(R.string.import_replace)) }
            },
            dismissButton = {
                TextButton(onClick = { importPreview = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    // Time Selection Dropdown Dialog (uniquely robust and independent of experimental Compose APIs)
    if (showTimeDialog) {
        var tempHour by remember { mutableStateOf(notificationHour) }
        var tempMinute by remember { mutableStateOf(notificationMinute) }

        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text(stringResource(R.string.notification_time), fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = {
                        notificationHour = tempHour
                        notificationMinute = tempMinute
                        saveChanges(viewModel, settings, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                        showTimeDialog = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    NumberSelector(
                        label = "Hour",
                        value = tempHour,
                        range = 0..23,
                        onValueChange = { tempHour = it }
                    )

                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold)

                    // Minute picker
                    NumberSelector(
                        label = "Min",
                        value = tempMinute,
                        range = 0..59,
                        onValueChange = { tempMinute = it }
                    )
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
fun NumberSelector(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(null, "%02d", value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(200.dp) // Limit height so it scrolls nicely
            ) {
                range.forEach { valNum ->
                    DropdownMenuItem(
                        text = { Text(String.format(null, "%02d", valNum)) },
                        onClick = {
                            onValueChange(valNum)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun saveChanges(
    viewModel: PantryViewModel,
    existing: AppSettings?,
    currencySymbol: String,
    theme: String,
    enabled: Boolean,
    hour: Int,
    minute: Int,
    context: Context
) {
    val current = existing ?: AppSettings()
    viewModel.saveFullSettings(
        current.copy(
            currencySymbol = currencySymbol,
            theme = theme,
            notificationEnabled = enabled,
            notificationHour = hour,
            notificationMinute = minute
        )
    )

    // Schedule or Cancel Local Notification
    if (enabled) {
        NotificationReceiver.scheduleNotification(context, hour, minute)
    } else {
        NotificationReceiver.cancelNotification(context)
    }
}

private fun saveChangesWithGemini(
    viewModel: PantryViewModel,
    existing: AppSettings?,
    currencySymbol: String,
    theme: String,
    enabled: Boolean,
    hour: Int,
    minute: Int,
    geminiKey: String,
    context: Context
) {
    val current = existing ?: AppSettings()
    viewModel.saveFullSettings(
        current.copy(
            currencySymbol = currencySymbol,
            theme = theme,
            notificationEnabled = enabled,
            notificationHour = hour,
            notificationMinute = minute,
            geminiApiKey = geminiKey
        )
    )
    if (enabled) {
        NotificationReceiver.scheduleNotification(context, hour, minute)
    } else {
        NotificationReceiver.cancelNotification(context)
    }
}

private fun simulateInstantNotification(context: Context) {
    val channelId = "pantry_expiry_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Expiry Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Test Alert")
        .setContentText("You have products about to expire tomorrow!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(102, notification)
}
