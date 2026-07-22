package com.example.utils

import android.content.Context
import com.example.data.recipe.RecipeCatalog
import org.json.JSONArray
import org.json.JSONObject

data class MealPlanEntry(
    val dayOfWeek: Int,
    val mealType: String,
    val recipeId: String,
    val recipeName: String,
    val ingredientCount: Int = 0
)

object MealPlannerHelper {

    private const val PREFS_NAME = "meal_planner"
    private const val KEY_PLANS = "meal_plans"
    private const val KEY_WEEK_START = "week_start"

    fun getPlans(context: Context, weekStart: Long): List<MealPlanEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedWeekStart = prefs.getLong(KEY_WEEK_START, 0L)
        if (savedWeekStart != weekStart) return emptyList()

        val json = prefs.getString(KEY_PLANS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                MealPlanEntry(
                    dayOfWeek = obj.getInt("day"),
                    mealType = obj.getString("type"),
                    recipeId = obj.getString("recipeId"),
                    recipeName = obj.getString("recipeName"),
                    ingredientCount = obj.optInt("ingredientCount", 0)
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    fun savePlan(context: Context, weekStart: Long, entry: MealPlanEntry) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getPlans(context, weekStart).toMutableList()

        // Remove existing entry for same day/mealType
        existing.removeAll { it.dayOfWeek == entry.dayOfWeek && it.mealType == entry.mealType }
        existing.add(entry)

        val arr = JSONArray()
        existing.forEach { plan ->
            val obj = JSONObject()
            obj.put("day", plan.dayOfWeek)
            obj.put("type", plan.mealType)
            obj.put("recipeId", plan.recipeId)
            obj.put("recipeName", plan.recipeName)
            obj.put("ingredientCount", plan.ingredientCount)
            arr.put(obj)
        }

        prefs.edit()
            .putString(KEY_PLANS, arr.toString())
            .putLong(KEY_WEEK_START, weekStart)
            .apply()
    }

    fun removePlan(context: Context, weekStart: Long, dayOfWeek: Int, mealType: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = getPlans(context, weekStart).toMutableList()
        existing.removeAll { it.dayOfWeek == dayOfWeek && it.mealType == mealType }

        val arr = JSONArray()
        existing.forEach { plan ->
            val obj = JSONObject()
            obj.put("day", plan.dayOfWeek)
            obj.put("type", plan.mealType)
            obj.put("recipeId", plan.recipeId)
            obj.put("recipeName", plan.recipeName)
            obj.put("ingredientCount", plan.ingredientCount)
            arr.put(obj)
        }

        prefs.edit()
            .putString(KEY_PLANS, arr.toString())
            .putLong(KEY_WEEK_START, weekStart)
            .apply()
    }

    fun getMissingIngredients(
        context: Context,
        weekStart: Long,
        pantryProductNames: Set<String>
    ): List<String> {
        val plans = getPlans(context, weekStart)
        val allRecipeIngredients = plans.mapNotNull { plan ->
            RecipeCatalog.recipes.find { it.id == plan.recipeId }
        }.flatMap { it.ingredients.map { ing -> ing.lowercase().trim() } }
            .distinct()

        val normalizedPantry = pantryProductNames.map { it.lowercase().trim() }.toSet()
        return allRecipeIngredients.filter { it !in normalizedPantry }
    }
}
