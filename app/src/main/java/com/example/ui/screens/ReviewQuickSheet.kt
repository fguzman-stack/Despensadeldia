package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.ui.theme.Emerald
import com.example.ui.theme.Coral
import com.example.ui.theme.Sky
import com.example.ui.theme.Amber
import com.example.ui.viewmodel.PantryViewModel
import java.util.*

private sealed interface QuickReviewResult {
    data class CONSUMED(override val value: Double) : QuickReviewResult, WithValue
    data class DONATED(override val value: Double) : QuickReviewResult, WithValue
    data class WASTED(override val value: Double) : QuickReviewResult, WithValue
    data object SKIPPED : QuickReviewResult

    interface WithValue {
        val value: Double
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewQuickSheet(
    urgentProducts: List<Product>,
    currencySymbol: String,
    viewModel: PantryViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentIndex by remember { mutableIntStateOf(0) }
    val results = remember { mutableListOf<QuickReviewResult>() }
    var showSummary by remember { mutableStateOf(false) }

    val totalProducts = urgentProducts.size

    val snackbarMsgConsumed = stringResource(R.string.snackbar_consumed)
    val snackbarMsgDonated = stringResource(R.string.snackbar_donated)
    val snackbarMsgWasted = stringResource(R.string.snackbar_wasted)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showSummary) {
                Text(
                    text = stringResource(R.string.quick_review_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.quick_review_progress, currentIndex + 1, totalProducts),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = showSummary,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInHorizontally { it / 4 } togetherWith
                    fadeOut(tween(200)) + slideOutHorizontally { -it / 4 }
                },
                label = "quick_review_content"
            ) { isSummary ->
                if (isSummary) {
                    QuickReviewSummary(
                        results = results,
                        currencySymbol = currencySymbol,
                        onClose = onDismiss
                    )
                } else if (totalProducts > 0 && currentIndex < totalProducts) {
                    val product = urgentProducts[currentIndex]
                    QuickProductCard(
                        product = product,
                        currencySymbol = currencySymbol,
                        onConsumed = {
                            viewModel.markAsConsumed(product)
                            results.add(QuickReviewResult.CONSUMED(product.totalPrice))
                            advanceOrSummary(totalProducts, currentIndex, showSummary = { showSummary = true }, nextIndex = { currentIndex++ })
                        },
                        onDonated = {
                            viewModel.markAsDonated(product)
                            results.add(QuickReviewResult.DONATED(product.totalPrice))
                            advanceOrSummary(totalProducts, currentIndex, showSummary = { showSummary = true }, nextIndex = { currentIndex++ })
                        },
                        onWasted = {
                            viewModel.markAsWasted(product)
                            results.add(QuickReviewResult.WASTED(product.totalPrice))
                            advanceOrSummary(totalProducts, currentIndex, showSummary = { showSummary = true }, nextIndex = { currentIndex++ })
                        },
                        onSkip = {
                            results.add(QuickReviewResult.SKIPPED)
                            advanceOrSummary(totalProducts, currentIndex, showSummary = { showSummary = true }, nextIndex = { currentIndex++ })
                        }
                    )
                }
            }
        }
    }
}

private fun advanceOrSummary(total: Int, current: Int, showSummary: () -> Unit, nextIndex: () -> Unit) {
    if (current + 1 >= total) {
        showSummary()
    } else {
        nextIndex()
    }
}

@Composable
private fun QuickProductCard(
    product: Product,
    currencySymbol: String,
    onConsumed: () -> Unit,
    onDonated: () -> Unit,
    onWasted: () -> Unit,
    onSkip: () -> Unit
) {
    val now = System.currentTimeMillis()
    val oneDayMs = 24L * 60 * 60 * 1000
    val isExpired = product.expirationDate != null && product.expirationDate < now
    val isToday = product.expirationDate != null && product.expirationDate in now until (now + oneDayMs)
    val isTomorrow = product.expirationDate != null && product.expirationDate in (now + oneDayMs) until (now + 2 * oneDayMs)

    val urgencyColor = when {
        isExpired || isToday -> Coral
        isTomorrow -> Amber
        else -> Amber
    }
    val urgencyLabel = when {
        isExpired -> stringResource(R.string.expired)
        isToday -> stringResource(R.string.today)
        isTomorrow -> stringResource(R.string.tomorrow)
        else -> stringResource(R.string.expires_soon)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(urgencyColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon(product.category),
                            contentDescription = null,
                            tint = urgencyColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = urgencyColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = urgencyLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = urgencyColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${product.quantity.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() }} ${product.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = locationDisplayName(product.location),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (product.totalPrice > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.value_label, currencySymbol, String.format(Locale.getDefault(), "%.0f", product.totalPrice)),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (product.expiryType != ExpiryType.FIXED) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val hintText = when (product.expiryType) {
                        ExpiryType.BEST_BEFORE -> stringResource(R.string.hint_best_before)
                        ExpiryType.ESTIMATED -> stringResource(R.string.hint_estimated)
                        else -> null
                    }
                    if (hintText != null) {
                        Text(
                            text = hintText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    text = stringResource(R.string.option_consumed),
                    icon = Icons.Filled.Check,
                    color = Emerald,
                    modifier = Modifier.weight(1f),
                    onClick = onConsumed
                )
                QuickActionButton(
                    text = stringResource(R.string.option_donated),
                    icon = Icons.Filled.VolunteerActivism,
                    color = Sky,
                    modifier = Modifier.weight(1f),
                    onClick = onDonated
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    text = stringResource(R.string.option_wasted),
                    icon = Icons.Filled.Delete,
                    color = Coral,
                    modifier = Modifier.weight(1f),
                    onClick = onWasted
                )
                QuickActionButton(
                    text = stringResource(R.string.quick_review_skip),
                    icon = Icons.Filled.SkipNext,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    onClick = onSkip
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun QuickReviewSummary(
    results: List<QuickReviewResult>,
    currencySymbol: String,
    onClose: () -> Unit
) {
    val totalReviewed = results.size
    val consumed = results.count { it is QuickReviewResult.CONSUMED }
    val donated = results.count { it is QuickReviewResult.DONATED }
    val wasted = results.count { it is QuickReviewResult.WASTED }
    val skipped = results.count { it is QuickReviewResult.SKIPPED }
    val resolved = consumed + donated + wasted
    val totalValue = results.sumOf { (it as? QuickReviewResult.WithValue)?.value ?: 0.0 }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Emerald.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Emerald,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = stringResource(R.string.quick_review_done_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.quick_review_done_body, resolved, totalReviewed),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (totalValue > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Emerald.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.quick_review_value_saved),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", totalValue)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Emerald
                    )
                }
            }
        }

        if (consumed > 0 || donated > 0 || wasted > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStat(
                    value = "$consumed",
                    label = stringResource(R.string.consumed),
                    color = Emerald
                )
                SummaryStat(
                    value = "$donated",
                    label = stringResource(R.string.donated),
                    color = Sky
                )
                SummaryStat(
                    value = "$wasted",
                    label = stringResource(R.string.wasted),
                    color = Coral
                )
            }
        }

        if (skipped > 0) {
            Text(
                text = stringResource(R.string.quick_review_skipped, skipped),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.quick_review_close))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SummaryStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
