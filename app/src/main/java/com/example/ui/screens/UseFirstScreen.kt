package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import com.example.ui.theme.Emerald
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.Amber
import com.example.ui.theme.Coral
import com.example.ui.theme.Sky
import com.example.ui.theme.TextOnDarkSecondary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PantryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    val urgentProducts = activeProducts.filter { product ->
        product.expirationDate != null &&
        product.expiryType != ExpiryType.NONE &&
        (product.snoozeUntil == null || product.snoozeUntil <= now)
    }.sortedBy { it.expirationDate }

    val expired = urgentProducts.filter { it.expirationDate!! < now }
    val expiringToday = urgentProducts.filter { it.expirationDate!! in now until todayEnd }
    val expiringTomorrow = urgentProducts.filter { it.expirationDate!! in todayEnd until tomorrowEnd }
    val expiringThisWeek = urgentProducts.filter { it.expirationDate!! in tomorrowEnd until weekEnd }

    val hasUrgentItems = expired.isNotEmpty() || expiringToday.isNotEmpty() ||
            expiringTomorrow.isNotEmpty() || expiringThisWeek.isNotEmpty()

    var productToResolve by remember { mutableStateOf<Product?>(null) }
    var productToSnooze by remember { mutableStateOf<Product?>(null) }
    var showQuickReview by remember { mutableStateOf(false) }
    var showRecipes by remember { mutableStateOf(false) }

    val recipeSuggestions by viewModel.recipeSuggestions.collectAsState()

    val currencySymbol = settings.currencySymbol.ifEmpty { "$" }

    val totalUrgent = expired.size + expiringToday.size + expiringTomorrow.size + expiringThisWeek.size
    val totalValueAtRisk = urgentProducts.sumOf { it.totalPrice }

    val view = LocalView.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (!hasUrgentItems) {
            AllClearView(
                totalActive = activeProducts.size,
                onRecipes = { showRecipes = true },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AmbientGradientBackground()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        UrgencyHeroCard(
                            totalUrgent = totalUrgent,
                            totalValueAtRisk = totalValueAtRisk,
                            currencySymbol = currencySymbol,
                            expiredCount = expired.size,
                            todayCount = expiringToday.size
                        )
                    }

                    if (hasUrgentItems) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = { showQuickReview = true },
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        stringResource(R.string.quick_review_trigger),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showRecipes = true },
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Recetas",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    if (expired.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_expired),
                                color = Coral,
                                count = expired.size
                            )
                        }
                        items(expired, key = { it.id }) { product ->
                            ProductCard(
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

                    if (expiringToday.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_today),
                                color = Coral,
                                count = expiringToday.size
                            )
                        }
                        items(expiringToday, key = { it.id }) { product ->
                            ProductCard(
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

                    if (expiringTomorrow.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_tomorrow),
                                color = Amber,
                                count = expiringTomorrow.size
                            )
                        }
                        items(expiringTomorrow, key = { it.id }) { product ->
                            ProductCard(
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

                    if (expiringThisWeek.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.section_this_week),
                                color = Amber,
                                count = expiringThisWeek.size
                            )
                        }
                        items(expiringThisWeek, key = { it.id }) { product ->
                            val daysLeft = ((product.expirationDate!! - now) / oneDayMs).toInt()
                            val label = when {
                                product.expiryType == ExpiryType.ESTIMATED -> "~$daysLeft ${stringResource(R.string.days)}"
                                product.expiryType == ExpiryType.BEST_BEFORE -> "$daysLeft ${stringResource(R.string.days)}"
                                else -> "$daysLeft ${stringResource(R.string.days)}"
                            }
                            ProductCard(
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

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

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
                productToResolve = null
            },
            onDismiss = { productToResolve = null }
        )
    }

    if (showQuickReview) {
        ReviewQuickSheet(
            urgentProducts = expired + expiringToday + expiringTomorrow + expiringThisWeek,
            currencySymbol = currencySymbol,
            viewModel = viewModel,
            onDismiss = { showQuickReview = false }
        )
    }
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

    if (showRecipes) {
        RecipeSuggestionsSheet(
            suggestions = recipeSuggestions,
            onDismiss = { showRecipes = false }
        )
    }
}

@Composable
fun AmbientGradientBackground() {
    val bg = MaterialTheme.colorScheme.background
    val isAstral = bg == Color(0xFF080A18)
    val isDark = bg == Color(0xFF101814) || isAstral
    val colors = if (isAstral) {
        listOf(
            Color(0xFF080A18),
            Color(0xFF0C0F24),
            Color(0xFF0A0C1E)
        )
    } else if (isDark) {
        listOf(
            Color(0xFF101814),
            Color(0xFF0F1F16),
            Color(0xFF0E1A12)
        )
    } else {
        listOf(
            Color(0xFFF7F5EE),
            Color(0xFFF4F3EA),
            Color(0xFFF0EFE4)
        )
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.radialGradient(
                colors = colors,
                center = Offset(size.width * 0.85f, size.height * 0.1f),
                radius = size.height * 0.8f
            )
        )
    }
}

@Composable
fun UrgencyHeroCard(
    totalUrgent: Int,
    totalValueAtRisk: Double,
    currencySymbol: String,
    expiredCount: Int,
    todayCount: Int
) {
    val bg = MaterialTheme.colorScheme.background
    val isAstral = bg == Color(0xFF080A18)
    val isDark = bg == Color(0xFF101814) || isAstral
    val gradientStart = if (isAstral) Color(0xFF1A2040) else if (isDark) Color(0xFF1A3A2A) else Color(0xFFE8F5EE)
    val gradientEnd = if (isAstral) Color(0xFF141A33) else if (isDark) Color(0xFF1E3028) else Color(0xFFF5F0E0)

    val animatedCount by animateIntAsState(
        targetValue = totalUrgent,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 80f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(gradientStart, gradientEnd),
                        start = Offset.Zero,
                        end = Offset(1000f, 200f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Productos para usar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) TextOnDarkSecondary else TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$animatedCount",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) EmeraldLight else EmeraldDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "hoy",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) TextOnDarkSecondary else TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    if (totalValueAtRisk > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", totalValueAtRisk)} en riesgo",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (totalUrgent <= 2) Amber else Coral
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    UrgencyStat(
                        value = "$expiredCount",
                        label = "vencidos",
                        color = Coral
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    UrgencyStat(
                        value = "$todayCount",
                        label = "vence hoy",
                        color = Coral
                    )
                }
            }
        }
    }
}

@Composable
fun UrgencyStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextOnDarkSecondary
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    color: Color,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.12f)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    urgencyColor: Color,
    urgencyLabel: String,
    currencySymbol: String,
    showExpiryTypeHint: Boolean,
    onResolve: () -> Unit,
    onSnooze: () -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400, delayMillis = 50)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = animatedAlpha),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(urgencyColor)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(urgencyColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon(product.category),
                            contentDescription = null,
                            tint = urgencyColor,
                            modifier = Modifier.size(20.dp)
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
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${product.quantity.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() }} ${product.unit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "·",
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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = urgencyColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = urgencyLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = urgencyColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

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
                            modifier = Modifier.padding(top = 6.dp, start = 50.dp)
                        )
                    }
                }

                if (product.totalPrice > 0) {
                    Text(
                        text = stringResource(R.string.value_label, currencySymbol, String.format(Locale.getDefault(), "%.0f", product.totalPrice)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 2.dp, start = 50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
}

@Composable
fun AllClearView(
    totalActive: Int,
    onRecipes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRecipes,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver recetas con tu despensa")
        }
    }
}

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

fun locationIcon(location: ProductLocation): ImageVector = when (location) {
    ProductLocation.PANTRY -> Icons.Filled.Kitchen
    ProductLocation.FRIDGE -> Icons.Filled.Kitchen
    ProductLocation.FREEZER -> Icons.Filled.AcUnit
    ProductLocation.MEDICINE_CABINET -> Icons.Filled.MedicalServices
    ProductLocation.CLEANING -> Icons.Filled.CleaningServices
    ProductLocation.PETS -> Icons.Filled.Pets
    ProductLocation.OTHER -> Icons.Filled.Inventory2
}

fun categoryIcon(category: ProductCategory): ImageVector = when (category) {
    ProductCategory.PRODUCE -> Icons.Filled.Spa
    ProductCategory.DAIRY_EGGS -> Icons.Filled.BreakfastDining
    ProductCategory.MEAT_SEAFOOD -> Icons.Filled.SetMeal
    ProductCategory.BEVERAGES -> Icons.Filled.LocalCafe
    ProductCategory.PANTRY -> Icons.Filled.Kitchen
    ProductCategory.BAKERY -> Icons.Filled.BakeryDining
    ProductCategory.FROZEN -> Icons.Filled.AcUnit
    ProductCategory.MEDICINE -> Icons.Filled.MedicalServices
    ProductCategory.CLEANING -> Icons.Filled.CleaningServices
    ProductCategory.PET_SUPPLIES -> Icons.Filled.Pets
    ProductCategory.OTHER -> Icons.Filled.Inventory2
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
