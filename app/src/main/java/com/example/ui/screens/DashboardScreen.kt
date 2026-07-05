package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Product
import com.example.ui.viewmodel.PantryViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    val categories = listOf("Todos", "Frutas y Verduras", "Lácteos y Huevos", "Carnes y Pescados", "Bebidas", "Despensa / Granos", "Otros")

    val filteredProducts = activeProducts.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Todos" || product.category == selectedCategory
        matchesSearch && matchesCategory
    }.sortedBy { it.expirationDate } // Más urgente arriba

    val currencySymbol = settings?.currencySymbol ?: "$"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF10B981), // Verde Esmeralda
                contentColor = Color.White,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar Alimento")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header con Identidad
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Mi Despensa",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "No desperdicies comida. Ahorra dinero.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Barra de búsqueda y Filtros
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar alimentos...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Lista de Productos con Swipe
            if (filteredProducts.isEmpty()) {
                EmptyStateView(searchActive = searchQuery.isNotEmpty() || selectedCategory != "Todos") {
                    searchQuery = ""
                    selectedCategory = "Todos"
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        SwipeableProductItem(
                            product = product,
                            currencySymbol = currencySymbol,
                            onConsume = {
                                viewModel.markAsConsumed(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} consumido. ¡Ahorraste dinero!",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.updateProduct(product.id, product.name, product.category, product.price, product.quantity, product.unit, product.expirationDate)
                                    }
                                }
                            },
                            onWaste = {
                                viewModel.markAsWasted(product)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${product.name} descartado.",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.updateProduct(product.id, product.name, product.category, product.price, product.quantity, product.unit, product.expirationDate)
                                    }
                                }
                            },
                            onEdit = { productToEdit = product }
                        )
                    }
                }
            }
            AdBanner()
        }
    }

    if (showAddDialog) {
        AddEditProductDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, price, qty, unit, expDate ->
                viewModel.addProduct(name, cat, price, qty, unit, expDate)
                showAddDialog = false
            }
        )
    }

    if (productToEdit != null) {
        AddEditProductDialog(
            product = productToEdit,
            currencySymbol = currencySymbol,
            onDismiss = { productToEdit = null },
            onConfirm = { name, cat, price, qty, unit, expDate ->
                viewModel.updateProduct(productToEdit!!.id, name, cat, price, qty, unit, expDate)
                productToEdit = null
            }
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
    onEdit: () -> Unit
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
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF10B981) // Verde
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF4444) // Rojo
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

            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        },
        content = {
            ProductCard(product, currencySymbol, onEdit)
        }
    )
}

@Composable
fun ProductCard(
    product: Product,
    currencySymbol: String,
    onEdit: () -> Unit
) {
    val daysRemaining = ((product.expirationDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()

    val (statusColor, statusText) = when {
        daysRemaining < 0 -> Color(0xFF9CA3AF) to "Ya venció" // Gris
        daysRemaining == 0 -> Color(0xFFEF4444) to "Vence hoy" // Rojo
        daysRemaining <= 3 -> Color(0xFFF59E0B) to "Vence pronto" // Naranja/Amarillo
        else -> Color(0xFF10B981) to "Fresco" // Verde Esmeralda
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
            // Indicador de color circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(product.category) {
                        "Frutas y Verduras" -> Icons.Default.BakeryDining
                        "Lácteos y Huevos" -> Icons.Default.Egg
                        "Carnes y Pescados" -> Icons.Default.SetMeal
                        "Bebidas" -> Icons.Default.LocalDrink
                        else -> Icons.Default.Inventory2
                    },
                    contentDescription = null,
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
                    text = "${product.quantity} ${product.unit} • $statusText",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (daysRemaining <= 3 && daysRemaining >= 0) statusColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (daysRemaining <= 3) FontWeight.Bold else FontWeight.Normal
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "%s%.2f", currencySymbol, product.price * product.quantity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(product.expirationDate)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// ... Resto de componentes (EmptyStateView, AddEditProductDialog, AdBanner) se mantienen con ajustes menores de color
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
            text = if (searchActive) "No hay resultados" else "Tu despensa está vacía",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Empieza a ahorrar registrando tus alimentos hoy.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (searchActive) {
            Button(onClick = onClearFilters, modifier = Modifier.padding(top = 16.dp)) {
                Text("Limpiar filtros")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    product: Product? = null,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, price: Double, quantity: Double, unit: String, expirationDate: Long) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Frutas y Verduras") }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var quantityStr by remember { mutableStateOf(product?.quantity?.toString() ?: "") }
    var unit by remember { mutableStateOf(product?.unit ?: "uds") }
    
    val calendar = Calendar.getInstance()
    if (product != null) calendar.timeInMillis = product.expirationDate else calendar.add(Calendar.DAY_OF_YEAR, 7)

    var selectedDateInMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categories = listOf("Frutas y Verduras", "Lácteos y Huevos", "Carnes y Pescados", "Bebidas", "Despensa / Granos", "Otros")
    val units = listOf("uds", "kg", "g", "L", "ml")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Nuevo Alimento" else "Editar Alimento") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, category, priceStr.toDoubleOrNull() ?: 0.0, quantityStr.toDoubleOrNull() ?: 1.0, unit, selectedDateInMillis)
                },
                enabled = name.isNotBlank() && quantityStr.toDoubleOrNull() != null
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("¿Qué es?") }, modifier = Modifier.fillMaxWidth())
                
                // Selector de Fecha Simple
                OutlinedTextField(
                    value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateInMillis)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de Vencimiento") },
                    trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.CalendarToday, null) } },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = quantityStr, onValueChange = { quantityStr = it }, label = { Text("Cant.") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(0.8f))
                }
            }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateInMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateInMillis = datePickerState.selectedDateMillis ?: selectedDateInMillis
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun AdBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tip: Los tomates duran más fuera de la nevera si aún no están muy maduros.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
