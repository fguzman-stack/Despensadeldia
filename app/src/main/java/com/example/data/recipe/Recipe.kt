package com.example.data.recipe

import com.example.data.local.ProductCategory

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val prepTimeMinutes: Int,
    val difficulty: String,
    val region: String,
    val category: ProductCategory = ProductCategory.OTHER
)
