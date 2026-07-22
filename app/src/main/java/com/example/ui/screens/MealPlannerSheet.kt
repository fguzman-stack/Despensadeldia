package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.recipe.Recipe
import com.example.data.recipe.RecipeCatalog
import com.example.ui.theme.Emerald
import com.example.ui.theme.Amber
import com.example.ui.theme.Coral
import com.example.ui.theme.Sky
import com.example.ui.viewmodel.PantryViewModel
import com.example.utils.MealPlanEntry
import com.example.utils.MealPlannerHelper
import java.util.*

private val dayNames = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
private val mealTypes = listOf("Desayuno", "Comida", "Cena")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerSheet(
    viewModel: PantryViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activeProducts by viewModel.activeProductsState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val calendar = Calendar.getInstance()
    // Get current week start (Monday)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val weekStart = calendar.timeInMillis

    var plans by remember { mutableStateOf(MealPlannerHelper.getPlans(context, weekStart)) }
    var showRecipePicker by remember { mutableStateOf<Pair<Int, String>?>(null) }

    val pantryNames = activeProducts.map { it.name }.toSet()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Plan Semanal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }
            }

            // Weekly grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        dayNames.forEachIndexed { dayIndex, dayName ->
                            val dayPlan = plans.filter { it.dayOfWeek == dayIndex }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(40.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    if (dayPlan.isEmpty()) {
                                        Text(
                                            text = "Sin plan",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    } else {
                                        dayPlan.forEach { plan ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = when (plan.mealType) {
                                                        "Desayuno" -> Amber.copy(alpha = 0.12f)
                                                        "Comida" -> Emerald.copy(alpha = 0.12f)
                                                        else -> Sky.copy(alpha = 0.12f)
                                                    }
                                                ) {
                                                    Text(
                                                        text = "${plan.mealType.take(3)}: ${plan.recipeName}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Medium,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        MealPlannerHelper.removePlan(
                                                            context, weekStart,
                                                            plan.dayOfWeek, plan.mealType
                                                        )
                                                        plans = MealPlannerHelper.getPlans(context, weekStart)
                                                    },
                                                    modifier = Modifier.size(18.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Close,
                                                        contentDescription = "Eliminar",
                                                        modifier = Modifier.size(12.dp),
                                                        tint = MaterialTheme.colorScheme.outline
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                mealTypes.forEach { type ->
                                    val hasPlan = plans.any { it.dayOfWeek == dayIndex && it.mealType == type }
                                    IconButton(
                                        onClick = { if (!hasPlan) showRecipePicker = Pair(dayIndex, type) },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .then(
                                                if (!hasPlan) Modifier
                                                else Modifier.background(
                                                    Emerald.copy(alpha = 0.1f),
                                                    CircleShape
                                                )
                                            )
                                    ) {
                                        Icon(
                                            imageVector = when (type) {
                                                "Desayuno" -> Icons.Filled.FreeBreakfast
                                                "Comida" -> Icons.Filled.LunchDining
                                                else -> Icons.Filled.Nightlife
                                            },
                                            contentDescription = "Añadir $type",
                                            tint = if (hasPlan) Emerald else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            if (dayIndex < 6) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }

            // Shopping list for missing ingredients
            val missingIngredients = MealPlannerHelper.getMissingIngredients(context, weekStart, pantryNames)
            if (missingIngredients.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Amber.copy(alpha = 0.06f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.ShoppingCart,
                                    contentDescription = null,
                                    tint = Amber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ingredientes faltantes",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            missingIngredients.take(10).forEach { ing ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = ing,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRecipePicker != null) {
        val (day, mealType) = showRecipePicker!!
        RecipePickerDialog(
            recipes = RecipeCatalog.recipes,
            onSelect = { recipe ->
                MealPlannerHelper.savePlan(
                    context, weekStart,
                    MealPlanEntry(
                        dayOfWeek = day,
                        mealType = mealType,
                        recipeId = recipe.id,
                        recipeName = recipe.name,
                        ingredientCount = recipe.ingredients.size
                    )
                )
                plans = MealPlannerHelper.getPlans(context, weekStart)
                showRecipePicker = null
            },
            onDismiss = { showRecipePicker = null }
        )
    }
}

@Composable
fun RecipePickerDialog(
    recipes: List<Recipe>,
    onSelect: (Recipe) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Elegir receta", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(recipes) { recipe ->
                    Surface(
                        onClick = { onSelect(recipe) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recipe.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${recipe.prepTimeMinutes} min • ${recipe.difficulty}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Icon(
                                Icons.Filled.AddCircleOutline,
                                contentDescription = "Añadir",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
