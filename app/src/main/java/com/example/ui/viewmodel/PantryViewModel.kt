package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Achievement
import com.example.data.local.AppSettings
import com.example.data.local.ExpiryType
import com.example.data.local.Product
import com.example.data.local.ProductCategory
import com.example.data.local.ProductFrequent
import com.example.data.local.ProductLocation
import com.example.data.local.ProductStatus
import com.example.data.recipe.RecipeCatalog
import com.example.data.ai.PantryChatbot
import com.example.data.remote.BarcodeLookupResult
import com.example.data.remote.BarcodeLookupService
import com.example.data.remote.FrankfurterService
import com.example.data.remote.GeminiConfig
import com.example.data.remote.GeminiService
import com.example.data.remote.MealDBRecipe
import com.example.data.remote.NutritionInfo
import com.example.data.remote.OpenFoodFactsService
import com.example.data.remote.TheMealDBService
import com.example.data.repository.PantryRepository
import com.example.utils.isCurrentlySnoozed
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class PantryViewModel(
    private val repository: PantryRepository
) : ViewModel() {

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

    val donatedProductsState: StateFlow<List<Product>> = repository.donatedProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ─── Last used values for quick entry ──────────────────────────

    val lastUsedLocation = MutableStateFlow(ProductLocation.PANTRY)
    val lastUsedCategory = MutableStateFlow(ProductCategory.PRODUCE)
    val lastUsedUnit = MutableStateFlow("uds")

    // ─── Frequent products ──────────────────────────────────────────

    val frequentProductsState: StateFlow<List<ProductFrequent>> = repository.allProducts
        .map { products ->
            products.groupBy {
                it.name.trim().lowercase(Locale.ROOT)
            }.map { (_, group) ->
                val latest = group.maxByOrNull { it.addedDate } ?: return@map null
                ProductFrequent(
                    name = latest.name,
                    category = latest.category,
                    location = latest.location,
                    unit = latest.unit,
                    brand = latest.brand,
                    lastPrice = latest.totalPrice,
                    count = group.size
                )
            }.filterNotNull()
                .sortedByDescending { it.count }
                .take(8)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ─── Recipe Suggestions ────────────────────────────────────────

    val recipeSuggestions: StateFlow<List<RecipeCatalog.RecipeMatch>> = activeProductsState
        .map { activeProducts ->
            val now = System.currentTimeMillis()
            val zone = java.time.ZoneId.systemDefault()
            val today = java.time.Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
            val urgentNames = activeProducts
                .filter { p ->
                    val expiry = p.expirationDate
                    expiry != null && java.time.Instant.ofEpochMilli(expiry).atZone(zone).toLocalDate() <= today.plusDays(7)
                }
                .filterNot { it.isCurrentlySnoozed(now) }
                .map { it.name.lowercase().trim() }
                .toSet()
            val ingredientNames = activeProducts.map { it.name.lowercase().trim() }.toSet()
            RecipeCatalog.findRecipesByIngredients(ingredientNames, urgentNames, minMatch = 1)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ─── Achievements ──────────────────────────────────────────────

    val earnedAchievements: StateFlow<Set<String>> = combine(
        activeProductsState, consumedProductsState, wastedProductsState,
        donatedProductsState, settingsState
    ) { active, consumed, wasted, donated, settings ->
        val rescued = consumed.count { it.expirationDate != null && it.resolvedDate != null &&
            it.resolvedDate < it.expirationDate }
        val allProducts = active + consumed + wasted + donated
        val now = System.currentTimeMillis()
        val weekAgo = now - 7 * 24 * 60 * 60 * 1000
        val weeklyWasted = wasted.count { it.resolvedDate != null && it.resolvedDate >= weekAgo }

        Achievement.compute(
            streakDays = settings.streakDays,
            totalConsumed = consumed.size,
            totalWasted = wasted.size,
            totalDonated = donated.size,
            rescuedCount = rescued,
            totalEverAdded = allProducts.size,
            weeklyWasted = weeklyWasted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun checkDailyStreak() {
        viewModelScope.launch {
            val current = repository.getSettingsDirect() ?: AppSettings()
            val today = Calendar.getInstance().let {
                it.set(Calendar.HOUR_OF_DAY, 0)
                it.set(Calendar.MINUTE, 0)
                it.set(Calendar.SECOND, 0)
                it.set(Calendar.MILLISECOND, 0)
                it.timeInMillis
            }
            if (current.lastCheckTimestamp < today) {
                val yesterday = today - 24 * 60 * 60 * 1000
                val newStreak = if (current.lastCheckTimestamp >= yesterday) {
                    current.streakDays + 1
                } else {
                    1
                }
                repository.saveSettings(current.copy(
                    streakDays = newStreak,
                    lastCheckTimestamp = today
                ))
            }
        }
    }

    // ─── Barcode Lookup ────────────────────────────────────────────

    private val _barcodeLookupResult = MutableSharedFlow<BarcodeLookupResult?>()
    val barcodeLookupResult: SharedFlow<BarcodeLookupResult?> = _barcodeLookupResult

    fun lookupByBarcode(barcode: String) {
        viewModelScope.launch {
            val local = repository.getProductByBarcode(barcode)
            if (local != null) {
                _barcodeLookupResult.emit(
                    BarcodeLookupResult(
                        name = local.name,
                        brand = local.brand,
                        quantity = local.quantity,
                        unit = local.unit
                    )
                )
                return@launch
            }
            val result = BarcodeLookupService.lookup(barcode)
            _barcodeLookupResult.emit(result)
        }
    }

    // ─── Frankfurter Currency ──────────────────────────────────────

    private val _conversionResult = MutableSharedFlow<Double?>()
    val conversionResult: SharedFlow<Double?> = _conversionResult

    fun convertCurrency(amount: Double, from: String, to: String) {
        viewModelScope.launch {
            val rate = FrankfurterService.convert(amount, from, to)
            _conversionResult.emit(rate?.let { kotlin.math.round(it * 100.0) / 100.0 })
        }
    }

    // ─── Nutrition Info ────────────────────────────────────────────

    private val _nutritionResult = MutableSharedFlow<NutritionInfo?>()
    val nutritionResult: SharedFlow<NutritionInfo?> = _nutritionResult

    fun lookupNutrition(barcode: String) {
        viewModelScope.launch {
            val info = OpenFoodFactsService.getNutrition(barcode)
            _nutritionResult.emit(info)
        }
    }

    // ─── TheMealDB Recipes ─────────────────────────────────────────

    private val _onlineRecipes = MutableStateFlow<List<MealDBRecipe>>(emptyList())
    val onlineRecipes: StateFlow<List<MealDBRecipe>> = _onlineRecipes

    private val _onlineRecipesLoading = MutableStateFlow(false)
    val onlineRecipesLoading: StateFlow<Boolean> = _onlineRecipesLoading

    fun searchOnlineRecipes(ingredients: List<String>) {
        viewModelScope.launch {
            _onlineRecipesLoading.value = true
            _onlineRecipes.value = emptyList() // clear previous
            _onlineRecipes.value = TheMealDBService.searchByIngredients(ingredients)
            _onlineRecipesLoading.value = false
        }
    }

    // ─── Gemini AI Assistant ───────────────────────────────────────

    private val _geminiResponse = MutableSharedFlow<String?>(replay = 1)
    val geminiResponse: SharedFlow<String?> = _geminiResponse

    private val _geminiLoading = MutableStateFlow(false)
    val geminiLoading: StateFlow<Boolean> = _geminiLoading

    fun askGemini(apiKey: String, productList: String, question: String) {
        viewModelScope.launch {
            _geminiLoading.value = true
            val context = """
Eres un asistente de despensa inteligente. 
Productos en la despensa: $productList
Instrucciones: Responde en español, sé conciso, sugiere recetas y consejos.
            """.trimIndent()
            val config = GeminiConfig(apiKey = apiKey)
            val reply = GeminiService.sendMessage(config, context, question)
            _geminiResponse.emit(reply)
            _geminiLoading.value = false
        }
    }

    // ─── Local Chatbot ────────────────────────────────────────

    private val _chatbotResponse = MutableSharedFlow<String?>(replay = 1)
    val chatbotResponse: SharedFlow<String?> = _chatbotResponse

    fun askChatbot(products: List<String>, question: String) {
        viewModelScope.launch {
            _geminiLoading.value = true
            val reply = PantryChatbot.ask(products, question)
            _chatbotResponse.emit(reply)
            _geminiLoading.value = false
        }
    }

    // ─── Settings ──────────────────────────────────────────────────

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

    // ─── Add Product ───────────────────────────────────────────────

    fun addProduct(
        name: String,
        category: ProductCategory,
        totalPrice: Double,
        quantity: Double,
        unit: String,
        location: ProductLocation,
        expiryType: ExpiryType,
        expirationDate: Long?,
        barcode: String? = null,
        notes: String? = null,
        brand: String? = null,
        minimumStock: Double? = null
    ) {
        viewModelScope.launch {
            repository.insertProduct(
                Product(
                    name = name,
                    category = category,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    unit = unit,
                    location = location,
                    expiryType = expiryType,
                    expirationDate = expirationDate
                        .takeIf { expiryType != ExpiryType.NONE },
                    barcode = barcode,
                    notes = notes,
                    brand = brand,
                    minimumStock = minimumStock
                )
            )
            // Remember last used values for quick entry
            lastUsedLocation.value = location
            lastUsedCategory.value = category
            lastUsedUnit.value = unit
        }
    }

    // ─── Update Product ────────────────────────────────────────────

    fun updateProduct(
        id: Int,
        name: String,
        category: ProductCategory,
        totalPrice: Double,
        quantity: Double,
        unit: String,
        location: ProductLocation,
        expiryType: ExpiryType,
        expirationDate: Long?,
        barcode: String? = null,
        notes: String? = null,
        brand: String? = null,
        minimumStock: Double? = null
    ) {
        viewModelScope.launch {
            val existing = repository.getProductById(id) ?: return@launch
            repository.updateProduct(
                existing.copy(
                    name = name,
                    category = category,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    unit = unit,
                    location = location,
                    expiryType = expiryType,
                    expirationDate = expirationDate
                        .takeIf { expiryType != ExpiryType.NONE },
                    status = ProductStatus.ACTIVE,
                    resolvedDate = null,
                    barcode = barcode,
                    notes = notes,
                    brand = brand,
                    minimumStock = minimumStock
                )
            )
        }
    }

    // ─── Resolve Product (lifecycle actions) ───────────────────────

    fun markAsConsumed(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    status = ProductStatus.CONSUMED,
                    resolvedDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun markAsWasted(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    status = ProductStatus.WASTED,
                    resolvedDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun markAsDonated(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    status = ProductStatus.DONATED,
                    resolvedDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun undoProductResolution(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(
                product.copy(
                    status = ProductStatus.ACTIVE,
                    resolvedDate = null
                )
            )
        }
    }

    // ─── Snooze ────────────────────────────────────────────────────

    fun snoozeProduct(product: Product, days: Int) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            require(days in setOf(1, 3, 7))
            val until = now + days * 24L * 60 * 60 * 1000
            repository.snoozeProduct(product.id, until)
        }
    }

    fun clearProductSnooze(product: Product) {
        viewModelScope.launch {
            repository.snoozeProduct(product.id, null)
        }
    }

    // ─── Delete ────────────────────────────────────────────────────

    fun deleteProduct(id: Int) {
        viewModelScope.launch { repository.deleteProductById(id) }
    }

    // ─── Stats helpers ─────────────────────────────────────────────

    suspend fun getResolvedProductsInRange(startOfMonth: Long, endOfMonth: Long): List<Product> {
        return repository.getResolvedProductsInRange(startOfMonth, endOfMonth)
    }

    suspend fun getAllProductsDirect(): List<Product> {
        return repository.getAllProductsDirect()
    }

    fun replaceAllProducts(products: List<Product>) {
        viewModelScope.launch {
            repository.deleteAllProducts()
            products.forEach { product ->
                repository.insertProduct(product.copy(id = 0))
            }
        }
    }

    // ─── Shopping List ─────────────────────────────────────────────

    val shoppingItemsState: StateFlow<List<com.example.data.local.ShoppingItem>> = repository.allShoppingItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addShoppingItem(
        name: String,
        category: ProductCategory,
        quantity: Double,
        unit: String,
        location: ProductLocation? = null,
        preferredBrand: String? = null,
        estimatedPrice: Double? = null,
        sourceProductId: Int? = null
    ) {
        viewModelScope.launch {
            val existing = repository.getShoppingItemByNameNormalized(name)
            if (existing != null) return@launch
            repository.insertShoppingItem(
                com.example.data.local.ShoppingItem(
                    name = name,
                    category = category,
                    quantity = quantity,
                    unit = unit,
                    location = location,
                    preferredBrand = preferredBrand,
                    estimatedPrice = estimatedPrice,
                    sourceProductId = sourceProductId
                )
            )
        }
    }

    fun addShoppingItemFromProduct(product: Product) {
        addShoppingItem(
            name = product.name,
            category = product.category,
            quantity = product.quantity,
            unit = product.unit,
            location = product.location,
            preferredBrand = product.brand,
            sourceProductId = product.id
        )
    }

    fun toggleShoppingItem(item: com.example.data.local.ShoppingItem, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isChecked = isChecked))
        }
    }

    fun deleteShoppingItem(item: com.example.data.local.ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun deleteCheckedShoppingItems() {
        viewModelScope.launch {
            repository.deleteCheckedShoppingItems()
        }
    }
}
