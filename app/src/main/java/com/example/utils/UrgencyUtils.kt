package com.example.utils

import com.example.data.local.Product
import com.example.data.local.ProductStatus
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class UrgencyBucket {
    EXPIRED,
    TODAY,
    TOMORROW,
    THIS_WEEK,
    NONE
}

data class UrgentProduct(
    val product: Product,
    val bucket: UrgencyBucket
)

fun Product.urgencyBucket(
    nowMillis: Long = System.currentTimeMillis(),
    zoneId: ZoneId = ZoneId.systemDefault()
): UrgencyBucket {
    if (status != ProductStatus.ACTIVE) return UrgencyBucket.NONE
    val expiry = expirationDate ?: return UrgencyBucket.NONE

    val today = Instant.ofEpochMilli(nowMillis)
        .atZone(zoneId)
        .toLocalDate()

    val expiryDate = Instant.ofEpochMilli(expiry)
        .atZone(zoneId)
        .toLocalDate()

    return when {
        expiryDate.isBefore(today) -> UrgencyBucket.EXPIRED
        expiryDate == today -> UrgencyBucket.TODAY
        expiryDate == today.plusDays(1) -> UrgencyBucket.TOMORROW
        expiryDate <= today.plusDays(7) -> UrgencyBucket.THIS_WEEK
        else -> UrgencyBucket.NONE
    }
}

fun Product.isCurrentlySnoozed(
    nowMillis: Long = System.currentTimeMillis()
): Boolean = snoozeUntil?.let { it > nowMillis } == true

fun List<Product>.getUrgentProducts(
    nowMillis: Long = System.currentTimeMillis(),
    zoneId: ZoneId = ZoneId.systemDefault()
): List<UrgentProduct> =
    this.asSequence()
        .filter { it.status == ProductStatus.ACTIVE }
        .filterNot { it.isCurrentlySnoozed(nowMillis) }
        .map { product -> UrgentProduct(product, product.urgencyBucket(nowMillis, zoneId)) }
        .filter { it.bucket != UrgencyBucket.NONE }
        .sortedWith(
            compareBy<UrgentProduct> { it.bucket.ordinal }
                .thenBy { it.product.expirationDate ?: Long.MAX_VALUE }
        )
        .toList()

fun UrgencyBucket.getDisplayName(res: android.content.res.Resources): String = when (this) {
    UrgencyBucket.EXPIRED -> res.getString(com.example.R.string.section_expired)
    UrgencyBucket.TODAY -> res.getString(com.example.R.string.section_today)
    UrgencyBucket.TOMORROW -> res.getString(com.example.R.string.section_tomorrow)
    UrgencyBucket.THIS_WEEK -> res.getString(com.example.R.string.section_this_week)
    UrgencyBucket.NONE -> ""
}

fun UrgencyBucket.getColor(res: android.content.res.Resources): Int = when (this) {
    UrgencyBucket.EXPIRED -> com.example.R.color.urgency_expired
    UrgencyBucket.TODAY -> com.example.R.color.urgency_today
    UrgencyBucket.TOMORROW -> com.example.R.color.urgency_tomorrow
    UrgencyBucket.THIS_WEEK -> com.example.R.color.urgency_this_week
    UrgencyBucket.NONE -> com.example.R.color.urgency_none
}