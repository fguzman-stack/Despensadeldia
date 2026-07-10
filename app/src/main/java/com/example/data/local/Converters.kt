package com.example.data.local

import androidx.room.TypeConverter

/**
 * Room TypeConverters for enum types.
 * Converts enums to/from their String name for Room storage.
 */
class Converters {

    // ProductStatus
    @TypeConverter
    fun fromProductStatus(status: ProductStatus): String = status.name

    @TypeConverter
    fun toProductStatus(value: String): ProductStatus =
        try { ProductStatus.valueOf(value) } catch (_: Exception) { ProductStatus.ACTIVE }

    // ExpiryType
    @TypeConverter
    fun fromExpiryType(type: ExpiryType): String = type.name

    @TypeConverter
    fun toExpiryType(value: String): ExpiryType =
        try { ExpiryType.valueOf(value) } catch (_: Exception) { ExpiryType.FIXED }

    // ProductLocation
    @TypeConverter
    fun fromProductLocation(location: ProductLocation): String = location.name

    @TypeConverter
    fun toProductLocation(value: String): ProductLocation =
        try { ProductLocation.valueOf(value) } catch (_: Exception) { ProductLocation.PANTRY }

    // ProductCategory
    @TypeConverter
    fun fromProductCategory(category: ProductCategory): String = category.name

    @TypeConverter
    fun toProductCategory(value: String): ProductCategory =
        try { ProductCategory.valueOf(value) } catch (_: Exception) { ProductCategory.OTHER }
}
