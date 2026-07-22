package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class NutritionInfo(
    val calories: Double?,
    val protein: Double?,
    val fat: Double?,
    val carbs: Double?,
    val fiber: Double?,
    val sugar: Double?,
    val salt: Double?,
    val servingSize: String?,
)

object OpenFoodFactsService {

    suspend fun getNutrition(barcode: String): NutritionInfo? = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(
                URL("https://world.openfoodfacts.org/api/v2/product/$barcode.json").readText()
            )
            if (json.optInt("status") != 1) return@withContext null
            val product = json.optJSONObject("product") ?: return@withContext null
            val nutriments = product.optJSONObject("nutriments") ?: return@withContext null

            NutritionInfo(
                calories = nutriments.optDouble("energy-kcal_100g", -1.0).takeIf { it >= 0 },
                protein = nutriments.optDouble("proteins_100g", -1.0).takeIf { it >= 0 },
                fat = nutriments.optDouble("fat_100g", -1.0).takeIf { it >= 0 },
                carbs = nutriments.optDouble("carbohydrates_100g", -1.0).takeIf { it >= 0 },
                fiber = nutriments.optDouble("fiber_100g", -1.0).takeIf { it >= 0 },
                sugar = nutriments.optDouble("sugars_100g", -1.0).takeIf { it >= 0 },
                salt = nutriments.optDouble("salt_100g", -1.0).takeIf { it >= 0 },
                servingSize = product.optString("serving_size").ifBlank { "100g" }
            )
        } catch (_: Exception) { null }
    }
}
