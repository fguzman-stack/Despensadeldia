package com.example.data.recipe

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val prepTimeMinutes: Int,
    val difficulty: String, // "Fácil", "Medio", "Difícil"
    val region: String // Código de país
)
