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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.CoralRed
import com.example.ui.theme.AmberWarning
import com.example.ui.viewmodel.PantryViewModel
import java.util.Locale

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

    val currencySymbol = settings?.currencySymbol ?: "$"
    val streakDays = settings?.streakDays ?: 0

    // Cálculos de Totales
    val totalSaved = consumedProducts.sumOf { it.price * it.quantity }
    val totalWasted = wastedProducts.sumOf { it.price * it.quantity }
    
    val now = System.currentTimeMillis()
    val limitExpiringSoon = now + (3L * 24 * 60 * 60 * 1000)

    val freshCount = activeProducts.count { it.expirationDate > limitExpiringSoon }
    val expiringSoonCount = activeProducts.count { it.expirationDate in now..limitExpiringSoon }
    val expiredCount = activeProducts.count { it.expirationDate < now }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Tu Impacto Económico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            // Tarjetas de Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Racha de Ahorro",
                    value = "$streakDays ${if (streakDays == 1) "día" else "días"}",
                    icon = Icons.Filled.Whatshot,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Eficiencia",
                    value = if (totalSaved + totalWasted > 0) 
                        String.format("%.0f%%", (totalSaved / (totalSaved + totalWasted)) * 100) 
                        else "100%",
                    icon = Icons.Filled.Verified,
                    tint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // Gráfico de Dona Principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Balance de Alimentos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    DonutChart(
                        savedValue = totalSaved.toFloat(),
                        wastedValue = totalWasted.toFloat(),
                        currencySymbol = currencySymbol
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        LegendItem(EmeraldGreen, "Dinero Ahorrado", String.format("%s%.2f", currencySymbol, totalSaved))
                        LegendItem(CoralRed, "Dinero Perdido", String.format("%s%.2f", currencySymbol, totalWasted))
                    }
                }
            }

            // Salud del Inventario
            Text(
                text = "Estado de tu Despensa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InventoryHealthRow(EmeraldGreen, "Productos Frescos", freshCount, activeProducts.size)
                    InventoryHealthRow(AmberWarning, "Próximos a Vencer", expiringSoonCount, activeProducts.size)
                    InventoryHealthRow(CoralRed, "Productos Vencidos", expiredCount, activeProducts.size)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DonutChart(savedValue: Float, wastedValue: Float, currencySymbol: String) {
    val total = savedValue + wastedValue
    val savedPercentage = if (total > 0) savedValue / total else 1f
    
    val savedSweep = animateFloatAsState(targetValue = savedPercentage * 360f, animationSpec = tween(1000), label = "")

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val stroke = 22.dp.toPx()
            // Fondo (Perdido/Rojo)
            drawArc(color = CoralRed, startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Round))
            // Progreso (Ahorrado/Verde)
            drawArc(color = EmeraldGreen, startAngle = -90f, sweepAngle = savedSweep.value, useCenter = false, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = String.format("%.0f%%", savedPercentage * 100), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
            Text(text = "Efectividad", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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
fun InventoryHealthRow(color: Color, label: String, count: Int, total: Int) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
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
