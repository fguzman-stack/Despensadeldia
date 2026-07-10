package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import com.example.ui.theme.Emerald
import com.example.ui.theme.Amber
import com.example.ui.theme.Coral
import com.example.ui.theme.Sky
import com.example.ui.viewmodel.PantryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * "Usa Primero" — the main screen of the app.
 * Shows products prioritized by urgency with immediate action buttons.
 *
 * Priority order:
 * 1. Expired products without resolution (ask what happened)
 * 2. Products expiring today
 * 3. Products expiring tomorrow
 * 4. Products expiring within 7 days
 * 5. Products with best-before date approaching
 * 6. Estimated expiry approaching
 *
 * Products with ExpiryType.NONE never appear here unless manually marked.
 * Snoozed products are hidden until snoozeUntil passes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseFirstScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val activeProducts by viewModel.activeProductsState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val now = System.currentTimeMillis()
    val oneDayMs = 24L * 60 * 60 * 1000
    val todayEnd = now + oneDayMs
    val tomorrowEnd = now + 2 * oneDayMs
    val weekEnd = now + 7 * oneDayMs

    // Filter: only products with expiration dates, excluding snoozed
    val urgentProducts = activeProducts.filter { product ->
        product.expirationDate != null &&
        product.expiryType != ExpiryType.NONE &&
        (product.snoozeUntil == null || product.snoozeUntil <= now)
    }.sortedBy { it.expirationDate }

    // Group by urgency
    val expired = urgentProducts.filter { it.expirationDate!! < now }
    val expiringToday = urgentProducts.filter { it.expirationDate!! in now until todayEnd }
    val expiringTomorrow = urgentProducts.filter { it.expirationDate!! in todayEnd until tomorrowEnd }
    val expiringThisWeek = urgentProducts.filter { it.expirationDate!! in tomorrowEnd until weekEnd }

    val hasUrgentItems = expired.isNotEmpty() || expiringToday.isNotEmpty() ||
            expiringTomorrow.isNotEmpty() || expiringThisWeek.isNotEmpty()

    // State for resolution dialog
    var productToResolve by remember { mutableStateOf<Product?>(null) }
    // State for snooze dialog
    var productToSnooze by remember { mutableStateOf<Product?>(null) }

    val currencySymbol = settings.currencySymbol.ifEmpty { "$" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (!hasUrgentItems) {
            // ─── All clear! ────────────────────────────────────────
            AllClearView(
                totalActive = activeProducts.size,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header
                item {
Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.use_first_title),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.use_first_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                }

                // ─── Expired (requires resolution) ─────────────────
                if (expired.isNotEmpty()) {
                    item {
                        UrgencySectionHeader(
                            icon = Icons.Filled.ErrorOutline,
                            title = stringResource(R.string.section_expired),
                            subtitle = stringResource(R.string.section_expired_subtitle),
                            color = Coral,
                            count = expired.size
                        )
                    }
                    items(expired, key = { it.id }) { product ->
                        UseFirstProductCard(
                            product = product,
                            urgencyColor = Coral,
                            urgencyLabel = stringResource(R.string.expired),
                            currencySymbol = currencySymbol,
                            showExpiryTypeHint = false,
                            onResolve = { productToResolve = product },
                            onSnooze = { productToSnooze = product }
                        )
                    }
                }

                // ─── Expiring today ────────────────────────────────
                if (expiringToday.isNotEmpty()) {
                    item {
                        UrgencySectionHeader(
                            icon = Icons.Filled.Warning,
                            title = stringResource(R.string.section_today),
                            subtitle = stringResource(R.string.section_today_subtitle),
                            color = Coral,
                            count = expiringToday.size
                        )
                    }
                    items(expiringToday, key = { it.id }) { product ->
                        UseFirstProductCard(
                            product = product,
                            urgencyColor = Coral,
                            urgencyLabel = stringResource(R.string.today),
                            currencySymbol = currencySymbol,
                            showExpiryTypeHint = product.expiryType == ExpiryType.BEST_BEFORE,
                            onResolve = { productToResolve = product },
                            onSnooze = { productToSnooze = product }
                        )
                    }
                }

                // ─── Expiring tomorrow ─────────────────────────────
                if (expiringTomorrow.isNotEmpty()) {
                    item {
                        UrgencySectionHeader(
                            icon = Icons.Filled.Schedule,
                            title = stringResource(R.string.section_tomorrow),
                            subtitle = stringResource(R.string.section_tomorrow_subtitle),
                            color = Amber,
                            count = expiringTomorrow.size
                        )
                    }
                    items(expiringTomorrow, key = { it.id }) { product ->
                        UseFirstProductCard(
                            product = product,
                            urgencyColor = Amber,
                            urgencyLabel = stringResource(R.string.tomorrow),
                            currencySymbol = currencySymbol,
                            showExpiryTypeHint = product.expiryType == ExpiryType.BEST_BEFORE,
                            onResolve = { productToResolve = product },
                            onSnooze = { productToSnooze = product }
                        )
                    }
                }

                // ─── This week ─────────────────────────────────────
                if (expiringThisWeek.isNotEmpty()) {
                    item {
                        UrgencySectionHeader(
                            icon = Icons.Filled.DateRange,
                            title = stringResource(R.string.section_this_week),
                            subtitle = stringResource(R.string.section_this_week_subtitle),
                            color = Amber,
                            count = expiringThisWeek.size
                        )
                    }
                    items(expiringThisWeek, key = { it.id }) { product ->
                        val daysLeft = ((product.expirationDate!! - now) / oneDayMs).toInt()
                        val label = when {
                            product.expiryType == ExpiryType.ESTIMATED -> "~$daysLeft ${stringResource(R.string.days)} (${stringResource(R.string.expiry_estimated).lowercase()})"
                            product.expiryType == ExpiryType.BEST_BEFORE -> "$daysLeft ${stringResource(R.string.days)} (${stringResource(R.string.expiry_best_before).lowercase()})"
                            else -> "$daysLeft ${stringResource(R.string.days)}"
                        }
                        UseFirstProductCard(
                            product = product,
                            urgencyColor = Amber,
                            urgencyLabel = label,
                            currencySymbol = currencySymbol,
                            showExpiryTypeHint = product.expiryType != ExpiryType.FIXED,
                            onResolve = { productToResolve = product },
                            onSnooze = { productToSnooze = product }
                        )
                    }
                }

                // Bottom spacer
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    // ─── Resolution dialog ─────────────────────────────────────────
    if (productToResolve != null) {
        val product = productToResolve!!
        val consumedMsg = stringResource(R.string.snackbar_consumed, product.name)
        val undoLabel = stringResource(R.string.snackbar_undone)
        val wastedMsg = stringResource(R.string.snackbar_wasted, product.name)
        val donatedMsg = stringResource(R.string.snackbar_donated, product.name)
        ResolveProductDialog(
            productName = product.name,
            onConsume = {
                viewModel.markAsConsumed(product)
                productToResolve = null
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = consumedMsg,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoProductResolution(product)
                    }
                }
            },
            onWaste = {
                viewModel.markAsWasted(product)
                productToResolve = null
                scope.launch {
                    snackbarHostState.showSnackbar(wastedMsg)
                }
            },
            onDonate = {
                viewModel.markAsDonated(product)
                productToResolve = null
                scope.launch {
                    snackbarHostState.showSnackbar(donatedMsg)
                }
            },
            onKeep = {
                // Keep in inventory — no action needed
                productToResolve = null
            },
            onDismiss = { productToResolve = null }
        )
    }

    // ─── Snooze dialog ─────────────────────────────────────────────
    if (productToSnooze != null) {
        val product = productToSnooze!!
        val snoozeTemplate = stringResource(R.string.snackbar_snoozed)
        SnoozeDialog(
            productName = product.name,
            onSnooze = { daysToSnooze ->
                val snoozeUntil = System.currentTimeMillis() + (daysToSnooze * 24L * 60 * 60 * 1000)
                viewModel.snoozeProduct(product, snoozeUntil)
                productToSnooze = null
                val snoozeMessage = snoozeTemplate.format(product.name, daysToSnooze)
                scope.launch {
                    snackbarHostState.showSnackbar(snoozeMessage)
                }
            },
            onDismiss = { productToSnooze = null }
        )
    }
}

// ─── Section Header ────────────────────────────────────────────────

@Composable
fun UrgencySectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.12f)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

// ─── Product Card for UseFirst ─────────────────────────────────────

@Composable
fun UseFirstProductCard(
    product: Product,
    urgencyColor: Color,
    urgencyLabel: String,
    currencySymbol: String,
    showExpiryTypeHint: Boolean,
    onResolve: () -> Unit,
    onSnooze: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Location icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(urgencyColor.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = locationIcon(product.location),
                        contentDescription = null,
                        tint = urgencyColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${product.quantity.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() }} ${product.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = locationDisplayName(product.location),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Urgency badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = urgencyColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = urgencyLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = urgencyColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Expiry type hint for non-fixed dates
            if (showExpiryTypeHint) {
                val hintText = when (product.expiryType) {
                    ExpiryType.BEST_BEFORE -> stringResource(R.string.hint_best_before)
                    ExpiryType.ESTIMATED -> stringResource(R.string.hint_estimated)
                    else -> null
                }
                if (hintText != null) {
                    Text(
                        text = hintText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 6.dp, start = 56.dp)
                    )
                }
            }

            // Price info (only if registered)
            if (product.totalPrice > 0) {
                Text(
                    text = stringResource(R.string.value_label, currencySymbol, String.format(Locale.getDefault(), "%.0f", product.totalPrice)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp, start = 56.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Consume button
                FilledTonalButton(
                    onClick = onResolve,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Emerald.copy(alpha = 0.12f),
                        contentColor = Emerald
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.action_resolve), style = MaterialTheme.typography.labelLarge)
                }

                // Snooze button
                OutlinedButton(
                    onClick = onSnooze,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Snooze, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.action_snooze), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ─── All Clear View ────────────────────────────────────────────────

@Composable
fun AllClearView(totalActive: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Emerald.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Emerald,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.use_first_all_clear),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (totalActive > 0) "Tienes $totalActive productos en tu inventario.\n${stringResource(R.string.use_first_no_urgent)}"
                   else stringResource(R.string.inventory_empty) + ".\n" + stringResource(R.string.inventory_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Resolve Product Dialog ────────────────────────────────────────

@Composable
fun ResolveProductDialog(
    productName: String,
    onConsume: () -> Unit,
    onWaste: () -> Unit,
    onDonate: () -> Unit,
    onKeep: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.dialog_what_happened, productName),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ResolveOptionButton(
                    icon = Icons.Filled.Check,
                    text = stringResource(R.string.option_consumed),
                    color = Emerald,
                    onClick = onConsume
                )
                ResolveOptionButton(
                    icon = Icons.Filled.Delete,
                    text = stringResource(R.string.option_wasted),
                    color = Coral,
                    onClick = onWaste
                )
                ResolveOptionButton(
                    icon = Icons.Filled.VolunteerActivism,
                    text = stringResource(R.string.option_donated),
                    color = Sky,
                    onClick = onDonate
                )
                ResolveOptionButton(
                    icon = Icons.Filled.Inventory2,
                    text = stringResource(R.string.option_keep),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onKeep
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
fun ResolveOptionButton(
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
    }
}

// ─── Snooze Dialog ─────────────────────────────────────────────────

@Composable
fun SnoozeDialog(
    productName: String,
    onSnooze: (days: Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.snooze_title, productName),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.snooze_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                listOf(1 to stringResource(R.string.snooze_1_day), 3 to stringResource(R.string.snooze_3_days), 7 to stringResource(R.string.snooze_7_days)).forEach { (days, label) ->
                    FilledTonalButton(
                        onClick = { onSnooze(days) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Snooze, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

// ─── Helper functions ──────────────────────────────────────────────

fun locationIcon(location: ProductLocation): ImageVector = when (location) {
    ProductLocation.PANTRY -> Icons.Filled.Kitchen
    ProductLocation.FRIDGE -> Icons.Filled.Kitchen
    ProductLocation.FREEZER -> Icons.Filled.AcUnit
    ProductLocation.MEDICINE_CABINET -> Icons.Filled.MedicalServices
    ProductLocation.CLEANING -> Icons.Filled.CleaningServices
    ProductLocation.PETS -> Icons.Filled.Pets
    ProductLocation.OTHER -> Icons.Filled.Inventory2
}

@Composable
fun locationDisplayName(location: ProductLocation): String = when (location) {
    ProductLocation.PANTRY -> stringResource(R.string.location_pantry)
    ProductLocation.FRIDGE -> stringResource(R.string.location_fridge)
    ProductLocation.FREEZER -> stringResource(R.string.location_freezer)
    ProductLocation.MEDICINE_CABINET -> stringResource(R.string.location_medicine_cabinet)
    ProductLocation.CLEANING -> stringResource(R.string.location_cleaning)
    ProductLocation.PETS -> stringResource(R.string.location_pets)
    ProductLocation.OTHER -> stringResource(R.string.location_other)
}

@Composable
fun categoryDisplayName(category: ProductCategory): String = when (category) {
    ProductCategory.PRODUCE -> stringResource(R.string.category_produce)
    ProductCategory.DAIRY_EGGS -> stringResource(R.string.category_dairy_eggs)
    ProductCategory.MEAT_SEAFOOD -> stringResource(R.string.category_meat_seafood)
    ProductCategory.BEVERAGES -> stringResource(R.string.category_beverages)
    ProductCategory.PANTRY -> stringResource(R.string.category_pantry)
    ProductCategory.BAKERY -> stringResource(R.string.category_bakery)
    ProductCategory.FROZEN -> stringResource(R.string.category_frozen)
    ProductCategory.MEDICINE -> stringResource(R.string.category_medicine)
    ProductCategory.CLEANING -> stringResource(R.string.category_cleaning)
    ProductCategory.PET_SUPPLIES -> stringResource(R.string.category_pet_supplies)
    ProductCategory.OTHER -> stringResource(R.string.category_other)
}
