package com.example.data.recipe

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class RecipeRepository(private val context: Context) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Recipe::class.java)
    private val adapter = moshi.adapter<List<Recipe>>(listType)

    fun getRecipesByRegion(region: String): List<Recipe> {
        return try {
            val fileName = "recipes/$region.json"
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            adapter.fromJson(jsonString) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
