package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class BarcodeLookupResult(
    val name: String?,
    val brand: String?,
    val quantity: Double?,
    val unit: String?
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

            BarcodeLookupResult(
                name = name,
                brand = brand,
                quantity = qty,
                unit = unit
            )
        } catch (_: Exception) {
            null
        }
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
