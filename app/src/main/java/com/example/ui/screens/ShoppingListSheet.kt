package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ShoppingItem
import com.example.ui.theme.Emerald
import com.example.ui.theme.Amber
import com.example.ui.viewmodel.PantryViewModel
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListSheet(
    viewModel: PantryViewModel,
    onDismiss: () -> Unit
) {
    val items by viewModel.shoppingItemsState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showBeforeBuying by remember { mutableStateOf(false) }

    val activeProducts by viewModel.activeProductsState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.shopping_list),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = {
                        val textToShare = buildString {
                            appendLine("Shopping List")
                            appendLine()
                            val unchecked = items.filter { !it.isChecked }
                            if (unchecked.isEmpty()) {
                                appendLine("Nothing pending.")
                            } else {
                                unchecked.groupBy { it.category }.forEach { (category, categoryItems) ->
                                    if (category.name.isNotEmpty()) appendLine("*${category.name}*")
                                    categoryItems.forEach {
                                        val qty = if (it.quantity == it.quantity.toLong().toDouble()) it.quantity.toLong().toString() else it.quantity.toString()
                                        appendLine("- ${it.name} ($qty ${it.unit})")
                                    }
                                    appendLine()
                                }
                            }
                        }
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, textToShare)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share list"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.share_list))
                    }

                    if (items.any { it.isChecked }) {
                        IconButton(onClick = { viewModel.deleteCheckedShoppingItems() }) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = stringResource(R.string.clean_checked))
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Add new item inline button
            OutlinedButton(
                onClick = { showAddItemDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_to_list))
            }

            // Before buying check
            if (items.any { !it.isChecked }) {
                TextButton(
                    onClick = { showBeforeBuying = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.before_buying_button))
                }
            }

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.shopping_list_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val unchecked = items.filter { !it.isChecked }.groupBy { it.category }
                    val checked = items.filter { it.isChecked }

                    unchecked.forEach { (category, categoryItems) ->
                        if (category.name.isNotEmpty()) {
                            item {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }

                        items(categoryItems, key = { it.id }) { item ->
                            ShoppingItemRow(
                                item = item,
                                onCheckedChange = { checked -> viewModel.toggleShoppingItem(item, checked) },
                                onDelete = { viewModel.deleteShoppingItem(item) }
                            )
                        }
                    }

                    if (checked.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.checked_items),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                        }
                        items(checked, key = { it.id }) { item ->
                            ShoppingItemRow(
                                item = item,
                                onCheckedChange = { isChecked -> viewModel.toggleShoppingItem(item, isChecked) },
                                onDelete = { viewModel.deleteShoppingItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBeforeBuying) {
        BeforeBuyingSheet(
            shoppingItems = items.filter { !it.isChecked },
            activeProducts = activeProducts,
            onDismiss = { showBeforeBuying = false }
        )
    }

    if (showAddItemDialog) {
        var name by remember { mutableStateOf("") }
        var quantityStr by remember { mutableStateOf("1") }
        var unit by remember { mutableStateOf("uds") }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text(stringResource(R.string.add_item)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.product)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = { Text(stringResource(R.string.quantity)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text(stringResource(R.string.unit)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addShoppingItem(
                                name = name,
                                category = ProductCategory.OTHER,
                                quantity = quantityStr.toDoubleOrNull() ?: 1.0,
                                unit = unit
                            )
                            showAddItemDialog = false
                        }
                    },
                    enabled = name.isNotBlank()
                ) { Text(stringResource(R.string.add_item)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

private fun normalizeName(name: String): String {
    val normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFD)
    val withoutAccents = normalized.replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
    return withoutAccents.uppercase(Locale.ROOT).replace(Regex("\\s+"), " ")
}

private fun findMatch(
    shoppingName: String,
    activeProducts: List<Product>
): Product? {
    val normalized = normalizeName(shoppingName)
    return activeProducts.find { normalizeName(it.name) == normalized }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeforeBuyingSheet(
    shoppingItems: List<ShoppingItem>,
    activeProducts: List<Product>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val matches = remember(shoppingItems, activeProducts) {
        shoppingItems.map { item ->
            item to findMatch(item.name, activeProducts)
        }
    }

    val matchCount = matches.count { it.second != null }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.before_buying_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (matchCount > 0) {
                        Text(
                            text = stringResource(R.string.before_buying_subtitle, matchCount),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Emerald
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.before_buying_none),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(matches, key = { it.first.id }) { (item, product) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (product != null)
                                Amber.copy(alpha = 0.08f)
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (product != null) Icons.Filled.Inventory else Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                tint = if (product != null) Amber else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (product != null) {
                                    val qty = if (product.quantity == product.quantity.toLong().toDouble())
                                        product.quantity.toLong().toString()
                                    else product.quantity.toString()
                                    Text(
                                        text = stringResource(R.string.before_buying_already_have, "$qty ${product.unit}"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Amber,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.before_buying_not_found),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.understood))
            }
        }
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                             else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Emerald)
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    color = if (item.isChecked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                val qty = if (item.quantity == item.quantity.toLong().toDouble()) item.quantity.toLong().toString() else item.quantity.toString()
                Text(
                    text = "$qty ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cancel), tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
