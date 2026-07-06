package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PantryViewModel

@Composable
fun RecipeScreen(
    viewModel: PantryViewModel,
    modifier: Modifier = Modifier
) {
    val suggestions by viewModel.recipeSuggestions.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateRecipeSuggestions()
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("El Chef de Despensa", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Recetas sugeridas basadas en lo que tienes:", style = MaterialTheme.typography.bodyMedium)
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
                items(suggestions) { (recipe, matches) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(recipe.name, style = MaterialTheme.typography.titleLarge)
                            Text("Coincidencias: $matches ingredientes", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
