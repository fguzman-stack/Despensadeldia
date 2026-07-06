package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppSettings
import com.example.data.local.Product
import com.example.data.repository.PantryRepository
import com.example.data.recipe.Recipe
import com.example.data.recipe.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PantryViewModel(
    private val repository: PantryRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    val recipeSuggestions = MutableStateFlow<List<Pair<Recipe, Int>>>(emptyList())

    val settingsState: StateFlow<AppSettings> = repository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val activeProductsState: StateFlow<List<Product>> = repository.activeProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val consumedProductsState: StateFlow<List<Product>> = repository.consumedProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wastedProductsState: StateFlow<List<Product>> = repository.wastedProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateRecipeSuggestions() {
        viewModelScope.launch {
            val settings = repository.getSettingsDirect() ?: return@launch
            val activeProducts = repository.getActiveProductsDirect().map { it.name.lowercase() }
            val recipes = recipeRepository.getRecipesByRegion(settings.countryName.take(2).lowercase())
            
            recipeSuggestions.value = recipes.map { recipe ->
                val matches = recipe.ingredients.count { ing -> activeProducts.any { prod -> prod.contains(ing) } }
                recipe to matches
            }.filter { it.second > 0 }.sortedByDescending { it.second }
        }
    }

    fun checkAndRefreshStreak() {
        viewModelScope.launch {
            val currentSettings = repository.getSettingsDirect() ?: return@launch
            val now = System.currentTimeMillis()
            repository.saveSettings(currentSettings.copy(lastCheckTimestamp = now))
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            val current = repository.getSettingsDirect() ?: AppSettings()
            repository.saveSettings(current.copy(onboardingCompleted = true))
        }
    }

    fun setupInitialPreferences(country: String, currencyCode: String, currencySymbol: String) {
        viewModelScope.launch {
            val current = repository.getSettingsDirect() ?: AppSettings()
            repository.saveSettings(
                current.copy(
                    countryName = country,
                    currencyCode = currencyCode,
                    currencySymbol = currencySymbol
                )
            )
        }
    }

    fun saveFullSettings(settings: AppSettings) {
        viewModelScope.launch { repository.saveSettings(settings) }
    }

    fun addProduct(name: String, category: String, price: Double, quantity: Double, unit: String, expirationDate: Long) {
        viewModelScope.launch {
            repository.insertProduct(Product(
                name = name, category = category, price = price,
                quantity = quantity, unit = unit, expirationDate = expirationDate
            ))
        }
    }

    fun updateProduct(id: Int, name: String, category: String, price: Double, quantity: Double, unit: String, expirationDate: Long) {
        viewModelScope.launch {
            val existing = repository.getProductById(id) ?: return@launch
            repository.updateProduct(existing.copy(
                name = name, category = category, price = price,
                quantity = quantity, unit = unit, expirationDate = expirationDate,
                status = "ACTIVE", resolvedDate = null
            ))
        }
    }

    fun markAsConsumed(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(status = "CONSUMED", resolvedDate = System.currentTimeMillis()))
            val settings = repository.getSettingsDirect()
            if (settings != null) repository.saveSettings(settings.copy(streakDays = settings.streakDays + 1))
        }
    }

    fun markAsWasted(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(status = "WASTED", resolvedDate = System.currentTimeMillis()))
            val settings = repository.getSettingsDirect()
            if (settings != null) repository.saveSettings(settings.copy(streakDays = 0))
        }
    }

    fun undoProductResolution(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(status = "ACTIVE", resolvedDate = null))
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch { repository.deleteProductById(id) }
    }
}
