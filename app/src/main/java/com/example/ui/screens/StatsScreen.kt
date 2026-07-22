package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.Achievement
import com.example.data.local.Product
import com.example.data.local.ProductStatus
import com.example.ui.theme.Emerald
import com.example.ui.theme.Coral
import com.example.ui.theme.Amber
import com.example.ui.theme.Sky
import com.example.ui.viewmodel.PantryViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settingsState.collectAsState()
    val activeProducts by viewModel.activeProductsState.collectAsState()
    val consumedProducts by viewModel.consumedProductsState.collectAsState()
    val wastedProducts by viewModel.wastedProductsState.collectAsState()
    val donatedProducts by viewModel.donatedProductsState.collectAsState()
    val earnedAchievements by viewModel.earnedAchievements.collectAsState()

    val currencySymbol = settings.currencySymbol.ifEmpty { "$" }

    // Monthly stats
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Filter resolved products for current month
    val startOfMonth = Calendar.getInstance().apply {
        set(currentYear, currentMonth, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfMonth = Calendar.getInstance().apply {
        set(currentYear, currentMonth, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val monthlyConsumed = consumedProducts.filter {
        it.resolvedDate != null && it.resolvedDate in startOfMonth..endOfMonth
    }
    val monthlyWasted = wastedProducts.filter {
        it.resolvedDate != null && it.resolvedDate in startOfMonth..endOfMonth
    }
    val monthlyDonated = donatedProducts.filter {
        it.resolvedDate != null && it.resolvedDate in startOfMonth..endOfMonth
    }

    // Value metrics (only if prices exist)
    val monthlyConsumedValue = monthlyConsumed.sumOf { it.totalPrice }
    val monthlyWastedValue = monthlyWasted.sumOf { it.totalPrice }
    val monthlyDonatedValue = monthlyDonated.sumOf { it.totalPrice }
    val hasMoneyData = monthlyConsumedValue > 0 || monthlyWastedValue > 0 || monthlyDonatedValue > 0

    // Count metrics (always available)
    val totalConsumedCount = monthlyConsumed.size
    val totalWastedCount = monthlyWasted.size
    val totalDonatedCount = monthlyDonated.size
    val totalResolvedCount = totalConsumedCount + totalWastedCount + totalDonatedCount

    // Rescued: consumed before expiry
    val rescuedCount = monthlyConsumed.count { product ->
        product.expirationDate != null && product.resolvedDate != null &&
        product.resolvedDate < product.expirationDate
    }

    // Consumption rate (excludes donated from denominator)
    val consumptionDenominator = totalConsumedCount + totalWastedCount
    val consumptionRate = if (consumptionDenominator > 0) {
        (totalConsumedCount.toFloat() / consumptionDenominator) * 100f
    } else 100f

    // Categories with most waste
    val wasteByCategory = monthlyWasted.groupBy { it.category }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    // Inventory health
    val now = System.currentTimeMillis()
    val limitExpiringSoon = now + (3L * 24 * 60 * 60 * 1000)

    val freshCount = activeProducts.count {
        it.expirationDate == null || it.expirationDate > limitExpiringSoon
    }
    val expiringSoonCount = activeProducts.count {
        it.expirationDate != null && it.expirationDate in now..limitExpiringSoon
    }
    val expiredCount = activeProducts.count {
        it.expirationDate != null && it.expirationDate < now
    }

    // Weekly stats
    val startOfWeek = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -6)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val weeklyConsumed = consumedProducts.filter {
        it.resolvedDate != null && it.resolvedDate >= startOfWeek
    }
    val weeklyWasted = wastedProducts.filter {
        it.resolvedDate != null && it.resolvedDate >= startOfWeek
    }
    val weeklyDonated = donatedProducts.filter {
        it.resolvedDate != null && it.resolvedDate >= startOfWeek
    }

    val weeklyConsumedCount = weeklyConsumed.size
    val weeklyWastedCount = weeklyWasted.size
    val weeklyDonatedCount = weeklyDonated.size
    val weeklyResolvedCount = weeklyConsumedCount + weeklyWastedCount + weeklyDonatedCount
    val weeklyValueSaved = weeklyConsumed.sumOf { it.totalPrice } + weeklyDonated.sumOf { it.totalPrice }

    val topWastedCategory = weeklyWasted.groupBy { it.category }
        .maxByOrNull { it.value.size }
        ?.key

    val monthName = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es")) ?: "Este mes"

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ─── Weekly summary card ───────────────────────────────
            if (weeklyResolvedCount > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Emerald.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Emerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.weekly_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald
                            )
                        }

                        Text(
                            text = stringResource(R.string.weekly_subtitle, weeklyResolvedCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (weeklyValueSaved > 0) {
                            Text(
                                text = "${stringResource(R.string.weekly_value_saved)} $currencySymbol${String.format(Locale.getDefault(), "%.0f", weeklyValueSaved)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (weeklyConsumedCount > 0) {
                                Text(
                                    text = "${stringResource(R.string.consumed)}: $weeklyConsumedCount",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (weeklyDonatedCount > 0) {
                                Text(
                                    text = "${stringResource(R.string.donated)}: $weeklyDonatedCount",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (topWastedCategory != null && weeklyWastedCount > 0) {
                                Text(
                                    text = "${stringResource(R.string.top_wasted)}: ${categoryDisplayName(topWastedCategory)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Coral
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // ─── Achievements / Badges ─────────────────────────
            if (earnedAchievements.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Amber.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logros",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Amber
                            )
                        }

                        val earnedList = Achievement.entries.filter { it.id in earnedAchievements }
                        val displayBadges = if (earnedList.size > 6) earnedList.take(6) else earnedList
                        val extraCount = earnedList.size - displayBadges.size

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displayBadges.chunked(3).forEach { row ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { badge ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                badge.icon,
                                                contentDescription = null,
                                                tint = badge.color,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = badge.title,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (extraCount > 0) {
                            Text(
                                text = "+$extraCount más",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = stringResource(R.string.stats_title, monthName.replaceFirstChar { it.uppercase() }),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            // ─── Count summary cards ───────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.consumed),
                    value = "$totalConsumedCount",
                    icon = Icons.Filled.Check,
                    tint = Emerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.wasted),
                    value = "$totalWastedCount",
                    icon = Icons.Filled.Delete,
                    tint = Coral,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.donated),
                    value = "$totalDonatedCount",
                    icon = Icons.Filled.VolunteerActivism,
                    tint = Sky,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.rescued),
                    value = "$rescuedCount",
                    subtitle = stringResource(R.string.rescued_hint),
                    icon = Icons.Filled.Shield,
                    tint = Emerald,
                    modifier = Modifier.weight(1f)
                )
            }

            // ─── Consumption rate donut ────────────────────────────
            if (consumptionDenominator > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.consumption_rate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        DonutChart(
                            consumedPercentage = consumptionRate / 100f,
                            label = String.format(Locale.getDefault(), "%.0f%%", consumptionRate),
                            sublabel = "aprovechado"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            LegendItem(Emerald, stringResource(R.string.consumed_label), "$totalConsumedCount productos")
                            LegendItem(Coral, stringResource(R.string.wasted_label), "$totalWastedCount productos")
                        }
                    }
                }
            }

            // ─── Money stats (only if prices exist) ────────────────
            if (hasMoneyData) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.economic_value),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (monthlyConsumedValue > 0) {
                            MoneyRow(
                                label = stringResource(R.string.value_consumed),
                                value = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", monthlyConsumedValue)}",
                                color = Emerald
                            )
                        }
                        if (monthlyWastedValue > 0) {
                            MoneyRow(
                                label = stringResource(R.string.value_wasted),
                                value = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", monthlyWastedValue)}",
                                color = Coral
                            )
                        }
                        if (monthlyDonatedValue > 0) {
                            MoneyRow(
                                label = stringResource(R.string.value_donated),
                                value = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", monthlyDonatedValue)}",
                                color = Sky
                            )
                        }
                    }
                }
            }

            // ─── Categories with most waste ────────────────────────
            if (wasteByCategory.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.waste_categories),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                wasteByCategory.forEach { (category, count) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Coral.copy(alpha = 0.06f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = categoryDisplayName(category),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (count == 1) stringResource(R.string.product_count_singular, count) else stringResource(R.string.products_count, count),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Coral
                            )
                        }
                    }
                }
            }

            // ─── Inventory health ──────────────────────────────────
            Text(
                text = stringResource(R.string.inventory_health),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InventoryHealthRow(Emerald, Icons.Filled.CheckCircle, stringResource(R.string.health_fresh), freshCount, activeProducts.size)
                    InventoryHealthRow(Amber, Icons.Filled.Warning, stringResource(R.string.health_expiring), expiringSoonCount, activeProducts.size)
                    InventoryHealthRow(Coral, Icons.Filled.ErrorOutline, stringResource(R.string.health_expired), expiredCount, activeProducts.size)
                }
            }

            // Info about stats accuracy
            if (totalResolvedCount == 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = stringResource(R.string.stats_empty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = tint)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun DonutChart(consumedPercentage: Float, label: String, sublabel: String) {
    val savedSweep = animateFloatAsState(
        targetValue = consumedPercentage * 360f,
        animationSpec = tween(1000),
        label = "donut_sweep"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
        Canvas(modifier = Modifier.size(150.dp)) {
            val stroke = 20.dp.toPx()
            drawArc(color = Coral, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(color = Emerald, startAngle = -90f, sweepAngle = savedSweep.value, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Emerald)
            Text(text = sublabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(12.dp, 4.dp).clip(CircleShape).background(color))
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MoneyRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(color)
            )
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun InventoryHealthRow(color: Color, icon: ImageVector, label: String, count: Int, total: Int) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
                Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            Text(text = "$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}
