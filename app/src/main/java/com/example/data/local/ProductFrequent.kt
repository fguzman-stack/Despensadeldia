package com.example.data.local

data class ProductFrequent(
    val name: String,
    val category: ProductCategory,
    val location: ProductLocation,
    val unit: String,
    val brand: String?,
    val lastPrice: Double,
    val count: Int
)
