package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Double = 1.0,
    val unit: String = "uds",
    val category: ProductCategory = ProductCategory.OTHER,
    val location: ProductLocation? = null,
    val preferredBrand: String? = null,
    val estimatedPrice: Double? = null,
    val isChecked: Boolean = false,
    val addedDate: Long = System.currentTimeMillis(),
    val sourceProductId: Int? = null
)
