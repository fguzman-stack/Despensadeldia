package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ProductFrequent
import com.example.data.local.ProductLocation
import com.example.ui.ads.AdManager
import com.example.ui.ads.AdState
import com.example.ui.ads.NATIVE_AD_UNIT_ID
import com.example.ui.ads.NativeAdCard
import com.example.ui.theme.Emerald
import com.example.ui.theme.Amber
import com.example.ui.theme.Coral
import com.example.ui.viewmodel.PantryViewModel
import com.example.utils.ExpiryPredictor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private fun stringToCategory(s: String): ProductCategory =
    try { ProductCategory.valueOf(s) } catch (_: Exception) { ProductCategory.OTHER }

private val allCategoryNames = listOf(
    "PRODUCE", "DAIRY_EGGS", "MEAT_SEAFOOD", "BEVERAGES",
    "PANTRY", "BAKERY", "FROZEN", "MEDICINE", "CLEANING", "PET_SUPPLIES", "OTHER"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settingsState.collectAsState()
    val activeProducts by viewModel.activeProductsState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        AdManager.loadNativeAd(context, NATIVE_AD_UNIT_ID)
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedLocation by remember { mutableStateOf<ProductLocation?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    val categories = listOf(
        "Todos", "Frutas y Verduras", "Lácteos y Huevos", "Carnes y Pescados",
        "Bebidas", "Despensa / Granos", "Panadería", "Congelados",
        "Medicamentos", "Limpieza", "Mascotas", "Otros"
    )

    val filteredProducts = activeProducts.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
            (product.notes?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCategory = selectedCategory == null || product.category.name == selectedCategory
        val matchesLocation = selectedLocation == null || product.location == selectedLocation
        matchesSearch && matchesCategory && matchesLocation
    }.sortedWith(
        compareBy<Product> { it.expirationDate ?: Long.MAX_VALUE }
    )

    var showShoppingList by remember { mutableStateOf(false) }
    var showMealPlanner by remember { mutableStateOf(false) }

    val currencySymbol = settings.currencySymbol.ifEmpty { "$" }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_product))
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.inventory_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.inventory_count, activeProducts.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showMealPlanner = true },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Plan semanal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showShoppingList = true },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = stringResource(R.string.shopping_list),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Search & Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_products)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                // Location filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedLocation == null,
                            onClick = { selectedLocation = null },
                            label = { Text(stringResource(R.string.filter_all_locations)) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(ProductLocation.entries.toList()) { location ->
                        FilterChip(
                            selected = selectedLocation == location,
                            onClick = {
                                selectedLocation = if (selectedLocation == location) null else location
                            },
                            label = { Text(locationDisplayName(location)) },
                            leadingIcon = {
                                Icon(
                                    locationIcon(location),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Category filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(stringResource(R.string.category_all)) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(allCategoryNames) { catName ->
                        val cat = stringToCategory(catName)
                        FilterChip(
                            selected = selectedCategory == catName,
                            onClick = { selectedCategory = if (selectedCategory == catName) null else catName },
                            label = { Text(categoryDisplayName(cat)) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Product List
            if (filteredProducts.isEmpty()) {
                EmptyStateView(
                    searchActive = searchQuery.isNotEmpty() || selectedCategory != null || selectedLocation != null
                ) {
                    searchQuery = ""
                    selectedCategory = null
                    selectedLocation = null
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val adInsertIndex = if (filteredProducts.size >= 8) 8 else filteredProducts.size
                    val beforeAd = filteredProducts.take(adInsertIndex)
                    val afterAd = filteredProducts.drop(adInsertIndex)

                    items(beforeAd, key = { it.id }) { product ->
                        SwipeableProductItem(
                            product = product,
                            currencySymbol = currencySymbol,
                            onConsume = {
                                viewModel.markAsConsumed(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} consumido",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoProductResolution(product)
                                    }
                                }
                            },
                            onWaste = {
                                viewModel.markAsWasted(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} descartado",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoProductResolution(product)
                                    }
                                }
                            },
                            onEdit = { productToEdit = product },
                            onAddToShoppingList = {
                                viewModel.addShoppingItemFromProduct(product)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "${product.name} agregado a la lista",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }

                    if (filteredProducts.isNotEmpty()) {
                        item(key = "native_ad") {
                            val adState by AdManager.adState.collectAsState()
                            NativeAdCard(adState = adState)
                        }
                    }

                    items(afterAd, key = { it.id }) { product ->
                        SwipeableProductItem(
                            product = product,
                            currencySymbol = currencySymbol,
                            onConsume = {
                                viewModel.markAsConsumed(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} consumido",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoProductResolution(product)
                                    }
                                }
                            },
                            onWaste = {
                                viewModel.markAsWasted(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} descartado",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoProductResolution(product)
                                    }
                                }
                            },
                            onEdit = { productToEdit = product },
                            onAddToShoppingList = {
                                viewModel.addShoppingItemFromProduct(product)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "${product.name} agregado a la lista",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    val frequentProducts by viewModel.frequentProductsState.collectAsState()

    if (showAddDialog) {
        AddEditProductDialog(
            viewModel = viewModel,
            currencySymbol = currencySymbol,
            frequentProducts = frequentProducts,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, price, qty, unit, location, expiryType, expDate, barcode, notes, brand, minStock ->
                viewModel.addProduct(name, cat, price, qty, unit, location, expiryType, expDate, barcode, notes, brand)
                showAddDialog = false
            }
        )
    }

    if (productToEdit != null) {
        AddEditProductDialog(
            viewModel = viewModel,
            product = productToEdit,
            currencySymbol = currencySymbol,
            onDismiss = { productToEdit = null },
            onConfirm = { name, cat, price, qty, unit, location, expiryType, expDate, barcode, notes, brand, minStock ->
                viewModel.updateProduct(productToEdit!!.id, name, cat, price, qty, unit, location, expiryType, expDate, barcode, notes, brand)
                productToEdit = null
            }
        )
    }

    if (showShoppingList) {
        ShoppingListSheet(
            viewModel = viewModel,
            onDismiss = { showShoppingList = false }
        )
    }

    if (showMealPlanner) {
        MealPlannerSheet(
            viewModel = viewModel,
            onDismiss = { showMealPlanner = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableProductItem(
    product: Product,
    currencySymbol: String,
    onConsume: () -> Unit,
    onWaste: () -> Unit,
    onEdit: () -> Unit,
    onAddToShoppingList: (() -> Unit)? = null
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onConsume()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onWaste()
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Emerald
                SwipeToDismissBoxValue.EndToStart -> Coral
                else -> Color.Transparent
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> Icons.Default.Delete
            }
            val label = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> stringResource(R.string.swipe_consumed)
                SwipeToDismissBoxValue.EndToStart -> stringResource(R.string.swipe_wasted)
                else -> ""
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(label, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        content = {
            ProductCard(product, currencySymbol, onEdit, onAddToShoppingList)
        }
    )
}

@Composable
fun ProductCard(
    product: Product,
    currencySymbol: String,
    onEdit: () -> Unit,
    onAddToShoppingList: (() -> Unit)? = null
) {
    val now = System.currentTimeMillis()
    val daysRemaining = product.expirationDate?.let {
        ((it - now) / (24 * 60 * 60 * 1000)).toInt()
    }

    val (statusColor, statusText) = when {
        daysRemaining == null -> MaterialTheme.colorScheme.outline to stringResource(R.string.no_expiry)
        daysRemaining < 0 -> Coral to stringResource(R.string.expired)
        daysRemaining == 0 -> Coral to stringResource(R.string.expires_today)
        daysRemaining <= 3 -> Amber to stringResource(R.string.expires_soon)
        else -> Emerald to stringResource(R.string.status_fresh)
    }

    // Expiry type hint
    val expiryHint = when (product.expiryType) {
        ExpiryType.BEST_BEFORE -> stringResource(R.string.expiry_best_before)
        ExpiryType.ESTIMATED -> stringResource(R.string.expiry_estimated)
        ExpiryType.NONE -> ""
        ExpiryType.FIXED -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon with status color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = locationIcon(product.location),
                    contentDescription = locationDisplayName(product.location),
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
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
                Text(
                    text = buildString {
                        val qty = product.quantity
                        append(if (qty == qty.toLong().toDouble()) qty.toLong().toString() else qty.toString())
                        append(" ${product.unit}")
                        append(" • $statusText$expiryHint")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (daysRemaining != null && daysRemaining <= 3 && daysRemaining >= 0) statusColor
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (daysRemaining != null && daysRemaining <= 3) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = locationDisplayName(product.location),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (product.minimumStock != null && product.quantity <= product.minimumStock) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { onAddToShoppingList?.invoke() },
                        shape = RoundedCornerShape(6.dp),
                        color = Amber.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.low_stock),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Amber
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (product.totalPrice > 0) {
                    Text(
                        text = "$currencySymbol${String.format(Locale.getDefault(), "%.0f", product.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (product.expirationDate != null) {
                    Text(
                        text = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(product.expirationDate)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    searchActive: Boolean,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Kitchen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (searchActive) stringResource(R.string.inventory_no_results) else stringResource(R.string.inventory_empty),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.inventory_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (searchActive) {
            Button(onClick = onClearFilters, modifier = Modifier.padding(top = 16.dp)) {
                Text(stringResource(R.string.clear_filters))
            }
        }
    }
}

// ─── Add/Edit Product Dialog with Quick & Full modes ───────────────

// Comprehensive units list — every way food is sold
val allUnits = listOf(
    "uds", "kg", "g", "mg", "L", "ml", "cl",
    "paquete", "caja", "bolsa", "botella", "lata",
    "frasco", "tubo", "tarro", "pote", "bandeja",
    "manojo", "pieza", "rebanada", "rodaja", "trozo",
    "barra", "litro", "galón", "onza", "libra",
    "sobre", "sachet", "cápsula", "pastilla", "tableta",
    "envase", "unidad"
)

// Smart units per category — shows relevant units first, falls back to allUnits
val categoryUnits: Map<ProductCategory, List<String>> = mapOf(
    ProductCategory.PRODUCE to listOf("kg", "g", "uds", "manojo", "bolsa", "pieza", "bandeja"),
    ProductCategory.DAIRY_EGGS to listOf("uds", "L", "ml", "paquete", "caja", "kg", "pote", "barra", "frasco"),
    ProductCategory.MEAT_SEAFOOD to listOf("kg", "g", "uds", "bandeja", "pieza", "paquete"),
    ProductCategory.BEVERAGES to listOf("L", "ml", "uds", "caja", "botella", "lata", "litro", "cl"),
    ProductCategory.PANTRY to listOf("kg", "g", "uds", "paquete", "bolsa", "caja", "sobre", "frasco", "lata", "envase"),
    ProductCategory.BAKERY to listOf("uds", "kg", "g", "paquete", "rebanada", "barra", "pieza", "bolsa"),
    ProductCategory.FROZEN to listOf("uds", "kg", "g", "paquete", "caja", "bolsa", "bandeja"),
    ProductCategory.MEDICINE to listOf("uds", "caja", "tubo", "frasco", "pastilla", "tableta", "cápsula", "sachet"),
    ProductCategory.CLEANING to listOf("L", "ml", "uds", "botella", "frasco", "envase", "galón", "cl"),
    ProductCategory.PET_SUPPLIES to listOf("kg", "g", "uds", "paquete", "bolsa", "lata", "sobre", "sachet"),
    ProductCategory.OTHER to allUnits
)

fun unitsForCategory(category: ProductCategory): List<String> =
    categoryUnits[category] ?: allUnits

data class ProductTemplate(
    val name: String,
    val category: ProductCategory,
    val defaultLocation: ProductLocation = ProductLocation.FRIDGE,
    val defaultUnit: String = "uds"
)

val quickTemplates = listOf(
    ProductTemplate("Leche", ProductCategory.DAIRY_EGGS, ProductLocation.FRIDGE, "L"),
    ProductTemplate("Huevos", ProductCategory.DAIRY_EGGS, ProductLocation.FRIDGE, "uds"),
    ProductTemplate("Yogur", ProductCategory.DAIRY_EGGS, ProductLocation.FRIDGE, "uds"),
    ProductTemplate("Pollo", ProductCategory.MEAT_SEAFOOD, ProductLocation.FRIDGE, "kg"),
    ProductTemplate("Queso", ProductCategory.DAIRY_EGGS, ProductLocation.FRIDGE, "kg"),
    ProductTemplate("Pan", ProductCategory.BAKERY, ProductLocation.PANTRY, "uds"),
    ProductTemplate("Frutas", ProductCategory.PRODUCE, ProductLocation.FRIDGE, "kg"),
    ProductTemplate("Verduras", ProductCategory.PRODUCE, ProductLocation.FRIDGE, "kg"),
    ProductTemplate("Arroz", ProductCategory.PANTRY, ProductLocation.PANTRY, "kg"),
    ProductTemplate("Fideos", ProductCategory.PANTRY, ProductLocation.PANTRY, "paquete"),
    ProductTemplate("Alimento mascota", ProductCategory.PET_SUPPLIES, ProductLocation.PETS, "kg"),
    ProductTemplate("Atún", ProductCategory.PANTRY, ProductLocation.PANTRY, "lata"),
    ProductTemplate("Coca-Cola", ProductCategory.BEVERAGES, ProductLocation.PANTRY, "L"),
    ProductTemplate("Agua", ProductCategory.BEVERAGES, ProductLocation.PANTRY, "L"),
    ProductTemplate("Cerveza", ProductCategory.BEVERAGES, ProductLocation.FRIDGE, "uds"),
    ProductTemplate("Mantequilla", ProductCategory.DAIRY_EGGS, ProductLocation.FRIDGE, "barra"),
    ProductTemplate("Carne molida", ProductCategory.MEAT_SEAFOOD, ProductLocation.FREEZER, "kg"),
    ProductTemplate("Pescado", ProductCategory.MEAT_SEAFOOD, ProductLocation.FREEZER, "kg"),
    ProductTemplate("Pan tajado", ProductCategory.BAKERY, ProductLocation.PANTRY, "paquete"),
    ProductTemplate("Jabón", ProductCategory.CLEANING, ProductLocation.CLEANING, "uds"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitField(
    unit: String,
    onUnitChange: (String) -> Unit,
    category: ProductCategory,
    unitExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val unitFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary
    )
    val relevantUnits = remember(unit, category) {
        val all = unitsForCategory(category)
        // Include current unit at top even if custom
        if (unit.isNotBlank() && unit !in all) listOf(unit) + all else all
    }

    ExposedDropdownMenuBox(
        expanded = unitExpanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = unit,
            onValueChange = { onUnitChange(it); onExpandedChange(true) },
            label = { Text("Unidad") },
            placeholder = { Text("uds, kg, L…") },
            colors = unitFieldColors,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { onExpandedChange(false) }) {
            val displayUnits = if (unit.isNotBlank() && unit !in relevantUnits) {
                listOf(unit) + "— personalizado —" + relevantUnits
            } else relevantUnits
            displayUnits.forEach { u ->
                if (u.startsWith("—")) {
                    DropdownMenuItem(
                        text = { Text(u, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) },
                        onClick = { onExpandedChange(false) },
                        enabled = false
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text(u) },
                        onClick = { onUnitChange(u); onExpandedChange(false) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    viewModel: PantryViewModel,
    product: Product? = null,
    currencySymbol: String,
    frequentProducts: List<ProductFrequent> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (
        name: String, category: ProductCategory, totalPrice: Double, quantity: Double, unit: String,
        location: ProductLocation, expiryType: ExpiryType, expirationDate: Long?,
        barcode: String?, notes: String?, brand: String?, minimumStock: Double?
    ) -> Unit
) {
    val lastLocation by viewModel.lastUsedLocation.collectAsState()
    val lastCategory by viewModel.lastUsedCategory.collectAsState()
    val lastUnit by viewModel.lastUsedUnit.collectAsState()

    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: lastCategory) }
    var priceStr by remember { mutableStateOf(if (product != null && product.totalPrice > 0) product.totalPrice.toString() else "") }
    var quantityStr by remember { mutableStateOf(product?.quantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(product?.unit ?: lastUnit) }
    var location by remember { mutableStateOf(product?.location ?: lastLocation) }
    var expiryType by remember { mutableStateOf(product?.expiryType ?: ExpiryType.FIXED) }
    var notes by remember { mutableStateOf(product?.notes ?: "") }
    var brand by remember { mutableStateOf(product?.brand ?: "") }
    var minStockStr by remember { mutableStateOf(product?.minimumStock?.toString() ?: "") }

    val calendar = Calendar.getInstance()
    if (product?.expirationDate != null) {
        calendar.timeInMillis = product.expirationDate
    } else {
        calendar.add(Calendar.DAY_OF_YEAR, 7)
    }
    var selectedDateInMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }

    var showBarcodeScanner by remember { mutableStateOf(false) }
    var scannedBarcode by remember { mutableStateOf(product?.barcode ?: "") }

    var showFullMode by remember { mutableStateOf(product != null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary
    )

    // Validation: name required; date only required if expiryType != NONE
    val isValid = name.isNotBlank() && (expiryType == ExpiryType.NONE || selectedDateInMillis > 0)

    if (!showBarcodeScanner) {
        AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (product == null) stringResource(R.string.add_product) else stringResource(R.string.edit_product),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        name, category,
                        priceStr.toDoubleOrNull() ?: 0.0,
                        quantityStr.toDoubleOrNull() ?: 1.0,
                        unit, location, expiryType,
                        if (expiryType == ExpiryType.NONE) null else selectedDateInMillis,
                        scannedBarcode.ifBlank { null }, notes.ifBlank { null }, brand.ifBlank { null },
                        minStockStr.toDoubleOrNull()
                    )
                },
                enabled = isValid
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Frequent products (only for new products)
                if (product == null && frequentProducts.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.frequent_products),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(frequentProducts) { fp ->
                            AssistChip(
                                onClick = {
                                    name = fp.name
                                    category = fp.category
                                    location = fp.location
                                    unit = fp.unit
                                    if (fp.brand != null) brand = fp.brand
                                    if (fp.lastPrice > 0) priceStr = fp.lastPrice.toString()
                                },
                                label = {
                                    Text(
                                        fp.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        "${fp.count}×",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Quick templates (only for new products)
                if (product == null) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickTemplates) { template ->
                            AssistChip(
                onClick = {
                    name = template.name
                    category = template.category
                    location = template.defaultLocation
                    unit = template.defaultUnit
                },
                                label = { Text(template.name, style = MaterialTheme.typography.labelMedium) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.what_is_it)) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quantity + Unit (always visible)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text(stringResource(R.string.quantity)) },
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    // Smart unit selector — editable with category-smart dropdown
                    UnitField(
                        unit = unit,
                        onUnitChange = { unit = it },
                        category = category,
                        unitExpanded = unitExpanded,
                        onExpandedChange = { unitExpanded = it },
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Barcode scan button
                OutlinedButton(
                    onClick = { showBarcodeScanner = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (scannedBarcode.isNotEmpty()) "Código: $scannedBarcode"
                        else "Escanear código de barras",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Smart category selector — always visible now so units adapt
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoryDisplayName(category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        colors = fieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        allCategoryNames.forEach { catName ->
                            val cat = stringToCategory(catName)
                            DropdownMenuItem(
                                text = { Text(categoryDisplayName(cat)) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                    // Smart: switch unit to first suggestion of new category if current unit doesn't fit
                                    val newUnits = unitsForCategory(cat)
                                    if (unit !in newUnits && newUnits.isNotEmpty()) {
                                        unit = newUnits.first()
                                    }
                                }
                            )
                        }
                    }
                }

                // Location chips
                Text(stringResource(R.string.location), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ProductLocation.entries.toList()) { loc ->
                        FilterChip(
                            selected = location == loc,
                            onClick = { location = loc },
                            label = { Text(locationDisplayName(loc), style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = if (location == loc) {{
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            }} else null,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Expiry type
                Text(stringResource(R.string.expiry_type), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val expiryFixed = stringResource(R.string.expiry_fixed)
                val expiryBestBefore = stringResource(R.string.expiry_best_before)
                val expiryEstimated = stringResource(R.string.expiry_estimated)
                val expiryNone = stringResource(R.string.expiry_none)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val expiryOptions = listOf(
                        ExpiryType.FIXED to expiryFixed,
                        ExpiryType.BEST_BEFORE to expiryBestBefore,
                        ExpiryType.ESTIMATED to expiryEstimated,
                        ExpiryType.NONE to expiryNone
                    )
                    items(expiryOptions) { (type, label) ->
                        FilterChip(
                            selected = expiryType == type,
                            onClick = { expiryType = type },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Estimated expiry hint
                if (expiryType == ExpiryType.ESTIMATED) {
                    val estimatedDays = ExpiryPredictor.estimateExpiryDays(category, location)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Amber.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = ExpiryPredictor.getHint(category, location),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    // Auto-set date based on estimate
                    LaunchedEffect(expiryType, category, location) {
                        val autoDate = System.currentTimeMillis() + (estimatedDays * 24L * 60 * 60 * 1000)
                        selectedDateInMillis = autoDate
                    }
                }

                // Date picker (only if not NONE)
                if (expiryType != ExpiryType.NONE) {
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateInMillis)),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                when (expiryType) {
                                    ExpiryType.BEST_BEFORE -> stringResource(R.string.expiry_best_before)
                                    ExpiryType.ESTIMATED -> stringResource(R.string.expiry_estimated)
                                    else -> stringResource(R.string.expiry_fixed)
                                }
                            )
                        },
                        colors = fieldColors,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                    )
                }

                // Toggle full mode
                TextButton(
                    onClick = { showFullMode = !showFullMode },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        if (showFullMode) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showFullMode) stringResource(R.string.less_details) else stringResource(R.string.more_details))
                }

                // Full mode fields (price, brand, min stock, notes — category & qty are always visible now)
                if (showFullMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text(stringResource(R.string.price, currencySymbol)) },
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text(stringResource(R.string.brand_optional)) },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = { minStockStr = it },
                        label = { Text(stringResource(R.string.minimum_stock_label)) },
                        placeholder = { Text(stringResource(R.string.minimum_stock_placeholder)) },
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(stringResource(R.string.notes_optional)) },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        }
    )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateInMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateInMillis = datePickerState.selectedDateMillis ?: selectedDateInMillis
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showBarcodeScanner) {
        BarcodeScannerScreen(
            onBarcodeDetected = { barcode ->
                scannedBarcode = barcode
                showBarcodeScanner = false
            },
            onDismiss = { showBarcodeScanner = false }
        )
    }
}

