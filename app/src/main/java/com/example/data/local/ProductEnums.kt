package com.example.data.local

/**
 * Status of a product in the inventory.
 * Stored as enum name in Room via TypeConverter.
 */
enum class ProductStatus {
    ACTIVE,
    CONSUMED,
    WASTED,
    DONATED
}

/**
 * Type of expiration date for a product.
 * FIXED: printed expiration date.
 * BEST_BEFORE: quality recommendation, not a safety date.
 * ESTIMATED: user's personal estimate (e.g., "tomato: ~5 days").
 * NONE: no expiration (e.g., salt, vinegar, canned goods).
 */
enum class ExpiryType {
    FIXED,
    BEST_BEFORE,
    ESTIMATED,
    NONE
}

/**
 * Physical location where a product is stored.
 * Stored as enum name in Room; display text comes from strings.xml.
 */
enum class ProductLocation {
    PANTRY,
    FRIDGE,
    FREEZER,
    MEDICINE_CABINET,
    CLEANING,
    PETS,
    OTHER
}

/**
 * Product category stored as enum name in Room.
 * Display text is translated via string resources.
 */
enum class ProductCategory {
    PRODUCE,
    DAIRY_EGGS,
    MEAT_SEAFOOD,
    PANTRY,
    BAKERY,
    FROZEN,
    BEVERAGES,
    MEDICINE,
    CLEANING,
    PET_SUPPLIES,
    OTHER
}
