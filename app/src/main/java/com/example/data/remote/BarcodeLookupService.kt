package com.example.data.remote

import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class BarcodeLookupResult(
    val name: String?,
    val brand: String?,
    val quantity: Double?,
    val unit: String?,
    val inferredCategory: ProductCategory? = null,
    val inferredLocation: ProductLocation? = null
)

object BarcodeLookupService {

    suspend fun lookup(barcode: String): BarcodeLookupResult? = withContext(Dispatchers.IO) {
        try {
            val response = URL("https://world.openfoodfacts.org/api/v2/product/$barcode.json")
                .readText()
            val json = JSONObject(response)
            if (json.optInt("status") != 1) return@withContext null
            val product = json.optJSONObject("product") ?: return@withContext null

            val name = product.optString("product_name").ifBlank { null }
            val brand = product.optString("brands").ifBlank { null }
            val (qty, unit) = parseQuantity(product.optString("quantity"))

            // Extract category tags for inference
            val categoryTags = mutableListOf<String>()
            val tagsArray = product.optJSONArray("categories_tags")
            if (tagsArray != null) {
                for (i in 0 until tagsArray.length()) {
                    categoryTags.add(tagsArray.optString(i, "").lowercase())
                }
            }

            val inferredCategory = inferCategory(categoryTags)
            val inferredLocation = inferLocation(inferredCategory)

            BarcodeLookupResult(
                name = name,
                brand = brand,
                quantity = qty,
                unit = unit,
                inferredCategory = inferredCategory,
                inferredLocation = inferredLocation
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Infers the app's ProductCategory from OpenFoodFacts category tags.
     * The tags follow the format "en:beverages", "en:dairies", etc.
     */
    private fun inferCategory(tags: List<String>): ProductCategory {
        for (tag in tags) {
            // Beverages
            if (tag.contains("beverage") || tag.contains("drink") || tag.contains("juice")
                || tag.contains("soda") || tag.contains("water") || tag.contains("coffee")
                || tag.contains("tea") || tag.contains("beer") || tag.contains("wine")
            ) return ProductCategory.BEVERAGES

            // Dairy & Eggs
            if (tag.contains("dairi") || tag.contains("dairy") || tag.contains("milk")
                || tag.contains("cheese") || tag.contains("yogurt") || tag.contains("egg")
                || tag.contains("butter") || tag.contains("cream")
            ) return ProductCategory.DAIRY_EGGS

            // Meat & Seafood
            if (tag.contains("meat") || tag.contains("seafood") || tag.contains("fish")
                || tag.contains("poultry") || tag.contains("chicken") || tag.contains("pork")
                || tag.contains("beef") || tag.contains("sausage") || tag.contains("ham")
            ) return ProductCategory.MEAT_SEAFOOD

            // Frozen
            if (tag.contains("frozen")
            ) return ProductCategory.FROZEN

            // Bakery
            if (tag.contains("bread") || tag.contains("pastri") || tag.contains("pastry")
                || tag.contains("bakery") || tag.contains("cake") || tag.contains("cookie")
                || tag.contains("biscuit")
            ) return ProductCategory.BAKERY

            // Produce (fruits & vegetables)
            if (tag.contains("fruit") || tag.contains("vegetable") || tag.contains("salad")
                || tag.contains("legume") || tag.contains("herb")
            ) return ProductCategory.PRODUCE

            // Pantry (snacks, cereals, canned, etc.)
            if (tag.contains("snack") || tag.contains("cereal") || tag.contains("canned")
                || tag.contains("pasta") || tag.contains("rice") || tag.contains("sauce")
                || tag.contains("oil") || tag.contains("spice") || tag.contains("condiment")
                || tag.contains("chocolate") || tag.contains("sweet") || tag.contains("candy")
                || tag.contains("confectioneri") || tag.contains("biscuit")
                || tag.contains("chip") || tag.contains("crisp")
            ) return ProductCategory.PANTRY
        }
        return ProductCategory.OTHER
    }

    /**
     * Suggests a storage location based on the inferred category.
     */
    private fun inferLocation(category: ProductCategory): ProductLocation = when (category) {
        ProductCategory.DAIRY_EGGS -> ProductLocation.FRIDGE
        ProductCategory.MEAT_SEAFOOD -> ProductLocation.FRIDGE
        ProductCategory.FROZEN -> ProductLocation.FREEZER
        ProductCategory.PRODUCE -> ProductLocation.FRIDGE
        ProductCategory.MEDICINE -> ProductLocation.MEDICINE_CABINET
        ProductCategory.CLEANING -> ProductLocation.CLEANING
        ProductCategory.PET_SUPPLIES -> ProductLocation.PETS
        else -> ProductLocation.PANTRY // BEVERAGES, PANTRY, BAKERY, OTHER
    }

    private fun parseQuantity(qtyStr: String?): Pair<Double?, String?> {
        if (qtyStr.isNullOrBlank()) return null to null
        val clean = qtyStr.trim().lowercase()
        val match = Regex("""([\d.,]+)\s*([a-záéíóúüñ]+(?:\s+[a-záéíóúüñ]+)*)""").find(clean)
        if (match != null) {
            val amount = match.groupValues[1].replace(",", ".").toDoubleOrNull()
            val rawUnit = match.groupValues[2].trim()
            val mapped = mapUnit(rawUnit)
            return amount to mapped
        }
        return null to null
    }

    private fun mapUnit(raw: String): String? = when (raw) {
        "ml", "milliliter", "millilitre", "mililitro" -> "ml"
        "cl", "centiliter", "centilitre" -> "cl"
        "l", "liter", "litre", "litro", "lt" -> "L"
        "g", "gram", "grams", "gramo", "gramos" -> "g"
        "kg", "kilogram", "kilograms", "kilo", "kilos", "kilogramo", "kilogramos" -> "kg"
        "oz", "ounce", "ounces", "onza", "onzas" -> "onza"
        "lb", "lbs", "pound", "pounds", "libra", "libras" -> "libra"
        "gal", "gallon", "gallons", "galón", "galones" -> "galón"
        "fl oz", "fl. oz.", "fluid ounce", "fluid ounces" -> "onza"
        else -> raw
    }
}
