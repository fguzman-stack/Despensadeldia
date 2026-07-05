package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // e.g., "Frutas y Verduras", "Lácteos", "Carnes", etc.
    val price: Double,
    val quantity: Double,
    val unit: String,     // e.g., "uds", "kg", "L"
    val expirationDate: Long, // timestamp
    val addedDate: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // "ACTIVE", "CONSUMED", "WASTED"
    val resolvedDate: Long? = null // timestamp when consumed or wasted
)
