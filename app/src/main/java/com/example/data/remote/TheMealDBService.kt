package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class MealDBRecipe(
    val id: String,
    val name: String,
    val thumbnail: String?,
    val ingredients: List<String>,
    val instructions: String?,
)

object TheMealDBService {

    private const val BASE = "https://www.themealdb.com/api/json/v1/1"

    suspend fun searchByIngredients(ingredients: List<String>): List<MealDBRecipe> = withContext(Dispatchers.IO) {
        if (ingredients.isEmpty()) return@withContext emptyList()
        val unique = ingredients.distinct().take(5)
        val mealIds = mutableSetOf<String>()
        val mealNames = mutableMapOf<String, String>()
        val mealThumbs = mutableMapOf<String, String?>()

        for (ing in unique) {
            try {
                val url = "$BASE/filter.php?i=${ing.lowercase().replace(" ", "_")}"
                val json = JSONObject(URL(url).readText())
                val meals = json.optJSONArray("meals") ?: continue
                for (i in 0 until meals.length()) {
                    val meal = meals.getJSONObject(i)
                    val id = meal.optString("idMeal", "")
                    if (id.isNotBlank() && id !in mealIds) {
                        mealIds.add(id)
                        mealNames[id] = meal.optString("strMeal", "")
                        mealThumbs[id] = meal.optString("strMealThumb", "").ifBlank { null }
                    }
                }
            } catch (_: Exception) { continue }
        }

        val detailed = coroutineScope {
            mealIds.take(10).map { id ->
                async { getMealDetails(id) }
            }.mapNotNull { it.await() }
        }

        detailed
    }

    private suspend fun getMealDetails(id: String): MealDBRecipe? = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(URL("$BASE/lookup.php?i=$id").readText())
            val meal = json.optJSONArray("meals")?.optJSONObject(0) ?: return@withContext null
            val ingredients = mutableListOf<String>()
            for (i in 1..20) {
                val name = meal.optString("strIngredient$i", "")
                val measure = meal.optString("strMeasure$i", "")
                if (name.isNotBlank()) {
                    ingredients.add("$name - $measure")
                }
            }
            MealDBRecipe(
                id = meal.optString("idMeal", id),
                name = meal.optString("strMeal", ""),
                thumbnail = meal.optString("strMealThumb", "").ifBlank { null },
                ingredients = ingredients,
                instructions = meal.optString("strInstructions", "").ifBlank { null }
            )
        } catch (_: Exception) { null }
    }
}
