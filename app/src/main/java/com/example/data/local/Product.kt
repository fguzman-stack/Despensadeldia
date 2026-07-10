package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: ProductCategory = ProductCategory.OTHER,
    val totalPrice: Double = 0.0,
    val quantity: Double = 1.0,
    val unit: String = "uds",
    val location: ProductLocation = ProductLocation.PANTRY,
    val expiryType: ExpiryType = ExpiryType.FIXED,
    val expirationDate: Long? = null,
    val addedDate: Long = System.currentTimeMillis(),
    val status: ProductStatus = ProductStatus.ACTIVE,
    val resolvedDate: Long? = null,
    val barcode: String? = null,
    val notes: String? = null,
    val brand: String? = null,
    val snoozeUntil: Long? = null,
    val minimumStock: Double? = null
)
