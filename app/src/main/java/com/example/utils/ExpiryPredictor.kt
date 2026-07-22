package com.example.utils

import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation

object ExpiryPredictor {

    private val shelfLifeDays = mapOf(
        ProductCategory.PRODUCE to mapOf(
            ProductLocation.FRIDGE to 5,
            ProductLocation.PANTRY to 3,
            ProductLocation.FREEZER to 90
        ),
        ProductCategory.DAIRY_EGGS to mapOf(
            ProductLocation.FRIDGE to 14,
            ProductLocation.FREEZER to 60
        ),
        ProductCategory.MEAT_SEAFOOD to mapOf(
            ProductLocation.FRIDGE to 3,
            ProductLocation.FREEZER to 180
        ),
        ProductCategory.BAKERY to mapOf(
            ProductLocation.PANTRY to 5,
            ProductLocation.FRIDGE to 7,
            ProductLocation.FREEZER to 60
        ),
        ProductCategory.PANTRY to mapOf(
            ProductLocation.PANTRY to 365,
            ProductLocation.FRIDGE to 30
        ),
        ProductCategory.FROZEN to mapOf(
            ProductLocation.FREEZER to 180,
            ProductLocation.FRIDGE to 7
        ),
        ProductCategory.BEVERAGES to mapOf(
            ProductLocation.FRIDGE to 30,
            ProductLocation.PANTRY to 180
        )
    )

    private val defaultShelfLife = 7

    fun estimateExpiryDays(category: ProductCategory, location: ProductLocation): Int {
        val byCategory = shelfLifeDays[category] ?: return defaultShelfLife
        return byCategory[location] ?: byCategory.entries.firstOrNull()?.value ?: defaultShelfLife
    }

    fun getHint(category: ProductCategory, location: ProductLocation): String {
        val days = estimateExpiryDays(category, location)
        return when {
            days <= 3 -> "Se estima que caduca en ~$days días. Revisa con frecuencia."
            days <= 14 -> "Se estima que caduca en ~$days días."
            days <= 90 -> "Se estima que caduca en ~$days días (~${days / 30} meses)."
            else -> "Se estima que caduca en ~${days / 30} meses."
        }
    }
}
