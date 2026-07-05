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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val presets = listOf(
        CountryPreset("España", "EUR", "€"),
        CountryPreset("México", "MXN", "$"),
        CountryPreset("Colombia", "COP", "$"),
        CountryPreset("Argentina", "ARS", "$"),
        CountryPreset("Chile", "CLP", "$"),
        CountryPreset("Perú", "PEN", "S/."),
        CountryPreset("Estados Unidos", "USD", "$"),
        CountryPreset("Venezuela", "VES", "Bs.D"),
        CountryPreset("Uruguay", "UYU", "$"),
        CountryPreset("Otro / Personalizado", "USD", "$")
    )

    var selectedPresetIndex by remember { mutableStateOf(0) }
    var customCountry by remember { mutableStateOf("") }
    var customCurrencyCode by remember { mutableStateOf("") }
    var customCurrencySymbol by remember { mutableStateOf("") }

    val currentPreset = presets[selectedPresetIndex]

    val finalCountry = if (currentPreset.name == "Otro / Personalizado") customCountry else currentPreset.name
    val finalCode = if (currentPreset.name == "Otro / Personalizado") customCurrencyCode else currentPreset.currencyCode
    val finalSymbol = if (currentPreset.name == "Otro / Personalizado") customCurrencySymbol else currentPreset.currencySymbol

    val isFormValid = if (currentPreset.name == "Otro / Personalizado") {
        customCountry.isNotBlank() && customCurrencyCode.isNotBlank() && customCurrencySymbol.isNotBlank()
    } else {
        true
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración Inicial", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
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
                text = "¡Personalicemos tu Despensa!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Selecciona tu país de residencia para configurar automáticamente la moneda con la que calcularás tu dinero ahorrado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Country selector Dropdown
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentPreset.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("País / Región") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("country_dropdown_input"),
                    colors = OutlinedTextFieldDefaults.colors()
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
                                if (preset.name == "Otro / Personalizado") {
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

            // Custom inputs if "Otro / Personalizado" is chosen
            if (currentPreset.name == "Otro / Personalizado") {
                OutlinedTextField(
                    value = customCountry,
                    onValueChange = { customCountry = it },
                    label = { Text("Nombre del País") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_country_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = customCurrencyCode,
                        onValueChange = { customCurrencyCode = it.take(3).uppercase() },
                        label = { Text("Código de Moneda (ej: EUR)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("custom_currency_code_input")
                    )

                    OutlinedTextField(
                        value = customCurrencySymbol,
                        onValueChange = { customCurrencySymbol = it.take(4) },
                        label = { Text("Símbolo (ej: €)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("custom_currency_symbol_input")
                    )
                }
            } else {
                // Read-only indicator of preselected currency
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                                text = "Moneda Configurará automáticamente:",
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar y Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
