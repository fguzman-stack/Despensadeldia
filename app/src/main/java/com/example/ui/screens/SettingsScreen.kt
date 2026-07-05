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
import com.example.MainActivity
import com.example.data.local.AppSettings
import com.example.receiver.NotificationReceiver
import com.example.ui.viewmodel.PantryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsState()

    var countryName by remember { mutableStateOf("") }
    var currencyCode by remember { mutableStateOf("") }
    var currencySymbol by remember { mutableStateOf("") }
    var selectedTheme by remember { mutableStateOf("SYSTEM") }
    var notificationEnabled by remember { mutableStateOf(true) }
    var notificationHour by remember { mutableStateOf(9) }
    var notificationMinute by remember { mutableStateOf(0) }

    // Initialize state from DB values
    LaunchedEffect(settings) {
        settings?.let {
            countryName = it.countryName
            currencyCode = it.currencyCode
            currencySymbol = it.currencySymbol
            selectedTheme = it.theme
            notificationEnabled = it.notificationEnabled
            notificationHour = it.notificationHour
            notificationMinute = it.notificationMinute
        }
    }

    var showTimeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 1. Regional Configuration Section
            SettingsSection(title = "Localización y Moneda") {
                OutlinedTextField(
                    value = countryName,
                    onValueChange = {
                        countryName = it
                        saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                    },
                    label = { Text("País / Región") },
                    leadingIcon = { Icon(Icons.Filled.Public, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_country_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = currencyCode,
                        onValueChange = {
                            currencyCode = it.uppercase()
                            saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                        },
                        label = { Text("Moneda") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("settings_currency_code_input")
                    )

                    OutlinedTextField(
                        value = currencySymbol,
                        onValueChange = {
                            currencySymbol = it
                            saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                        },
                        label = { Text("Símbolo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("settings_currency_symbol_input")
                    )
                }
            }

            // 2. Notification Schedule Section
            SettingsSection(title = "Notificaciones Diarias") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Recordatorios de vencimiento",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Avisar diariamente de alimentos próximos a vencer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = {
                            notificationEnabled = it
                            saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                        },
                        modifier = Modifier.testTag("settings_notifications_switch")
                    )
                }

                if (notificationEnabled) {
                    ListItem(
                        headlineContent = { Text("Hora de los recordatorios") },
                        supportingContent = { 
                            Text(String.format(null, "%02d:%02d", notificationHour, notificationMinute)) 
                        },
                        leadingContent = { 
                            Icon(Icons.Filled.AccessTime, contentDescription = null) 
                        },
                        trailingContent = {
                            TextButton(onClick = { showTimeDialog = true }) {
                                Text("Modificar")
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
                    Text("Probar Notificación de Alerta")
                }
            }

            // 3. Application Visual Theme Section
            SettingsSection(title = "Apariencia") {
                Text(
                    text = "Tema de la Aplicación",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themes = listOf(
                        Triple("SYSTEM", "Sistema", Icons.Filled.SettingsSystemDaydream),
                        Triple("LIGHT", "Claro", Icons.Filled.LightMode),
                        Triple("DARK", "Oscuro", Icons.Filled.DarkMode)
                    )

                    themes.forEach { (themeCode, themeLabel, themeIcon) ->
                        val isSelected = selectedTheme == themeCode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable {
                                    selectedTheme = themeCode
                                    saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = themeIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = themeLabel,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Time Selection Dropdown Dialog (uniquely robust and independent of experimental Compose APIs)
    if (showTimeDialog) {
        var tempHour by remember { mutableStateOf(notificationHour) }
        var tempMinute by remember { mutableStateOf(notificationMinute) }

        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Configurar Hora", fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = {
                        notificationHour = tempHour
                        notificationMinute = tempMinute
                        saveChanges(viewModel, settings, countryName, currencyCode, currencySymbol, selectedTheme, notificationEnabled, notificationHour, notificationMinute, context)
                        showTimeDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) {
                    Text("Cancelar")
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
                        label = "Hora",
                        value = tempHour,
                        range = 0..23,
                        onValueChange = { tempHour = it }
                    )

                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold)

                    // Minute picker
                    NumberSelector(
                        label = "Minuto",
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
    country: String,
    currencyCode: String,
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
            countryName = country,
            currencyCode = currencyCode,
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

private fun simulateInstantNotification(context: Context) {
    val channelId = "pantry_expiry_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Avisos de Vencimiento de Alimentos",
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
        .setContentTitle("Prueba de Alerta de Despensa 🍏")
        .setContentText("¡Tienes 2 plátanos maduros próximos a vencer mañana! Prepárate un delicioso pan de plátano.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(102, notification)
}
