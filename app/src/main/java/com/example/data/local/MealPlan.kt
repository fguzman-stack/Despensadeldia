package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,
    val mealType: String,
    val recipeId: String,
    val recipeName: String,
    val ingredients: List<String> = emptyList(),
    val weekStartDate: Long = System.currentTimeMillis()
)
