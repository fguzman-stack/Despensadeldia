package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.PantryViewModel

data class CountryPreset(
    val name: String,
    val currencyCode: String,
    val currencySymbol: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: PantryViewModel,
    onSetupComplete: () -> Unit
) {
    val useDeviceMode = stringResource(R.string.setup_use_device)
    val manualMode = stringResource(R.string.setup_manual)

    val presets = listOf(
        CountryPreset(manualMode, "USD", "$"),
        CountryPreset("🇪🇸 Spain", "EUR", "€"),
        CountryPreset("🇲🇽 Mexico", "MXN", "$"),
        CountryPreset("🇨🇴 Colombia", "COP", "$"),
        CountryPreset("🇦🇷 Argentina", "ARS", "$"),
        CountryPreset("🇨🇱 Chile", "CLP", "$"),
        CountryPreset("🇵🇪 Peru", "PEN", "S/."),
        CountryPreset("🇺🇸 United States", "USD", "$"),
        CountryPreset("🇻🇪 Venezuela", "VES", "Bs.D"),
        CountryPreset("🇺🇾 Uruguay", "UYU", "$"),
        CountryPreset("🇧🇷 Brazil", "BRL", "R$"),
        CountryPreset("🇪🇨 Ecuador", "USD", "$"),
        CountryPreset("🇵🇾 Paraguay", "PYG", "Gs."),
        CountryPreset("🇧🇴 Bolivia", "BOB", "Bs."),
        CountryPreset("🇬🇹 Guatemala", "GTQ", "Q"),
        CountryPreset("🇨🇷 Costa Rica", "CRC", "₡"),
        CountryPreset("🇨🇺 Cuba", "CUP", "$"),
        CountryPreset("🇩🇴 Dominican Republic", "DOP", "$"),
        CountryPreset("🇵🇦 Panama", "PAB", "B/."),
        CountryPreset("Other / Custom", "USD", "$")
    )

    var selectedPresetIndex by remember { mutableStateOf(0) }
    var customCountry by remember { mutableStateOf("") }
    var customCurrencyCode by remember { mutableStateOf("") }
    var customCurrencySymbol by remember { mutableStateOf("") }

    val currentPreset = presets[selectedPresetIndex]
    val isCustomMode = currentPreset.name == "Other / Custom"

    val finalCountry = if (isCustomMode) customCountry else {
        if (currentPreset.name == useDeviceMode) "" else currentPreset.name
    }
    val finalCode = if (isCustomMode) customCurrencyCode else currentPreset.currencyCode
    val finalSymbol = if (isCustomMode) customCurrencySymbol else currentPreset.currencySymbol

    val isFormValid = if (isCustomMode) {
        customCountry.isNotBlank() && customCurrencyCode.isNotBlank() && customCurrencySymbol.isNotBlank()
    } else {
        true
    }

    var dropdownExpanded by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.setup_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientGradientBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer),
                            start = Offset.Zero,
                            end = Offset(220f, 220f)
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = stringResource(R.string.setup_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.setup_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Mode selector Dropdown
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentPreset.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.setup_country)) },
                    colors = fieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .testTag("country_dropdown_input")
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    presets.forEachIndexed { index, preset ->
                        DropdownMenuItem(
                            text = { Text(preset.name) },
                            onClick = {
                                selectedPresetIndex = index
                                if (preset.name == "Other / Custom") {
                                    customCountry = ""
                                    customCurrencyCode = "USD"
                                    customCurrencySymbol = "$"
                                }
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Custom inputs if "Other / Custom" is chosen
            if (isCustomMode) {
                OutlinedTextField(
                    value = customCountry,
                    onValueChange = { customCountry = it },
                    label = { Text(stringResource(R.string.country_label)) },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth().testTag("custom_country_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = customCurrencyCode,
                        onValueChange = { customCurrencyCode = it.take(3).uppercase() },
                        label = { Text(stringResource(R.string.setup_currency_code)) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.weight(1f).testTag("custom_currency_code_input")
                    )

                    OutlinedTextField(
                        value = customCurrencySymbol,
                        onValueChange = { customCurrencySymbol = it.take(4) },
                        label = { Text(stringResource(R.string.setup_currency_symbol)) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.weight(1f).testTag("custom_currency_symbol_input")
                    )
                }
            } else if (currentPreset.name != useDeviceMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalAtm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.setup_currency_code),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentPreset.currencyCode} (${currentPreset.currencySymbol})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.setupInitialPreferences(
                            country = finalCountry,
                            currencyCode = finalCode,
                            currencySymbol = finalSymbol
                        )
                        viewModel.setOnboardingCompleted()
                        onSetupComplete()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_setup_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.setup_confirm), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            }
        }
    }
}
