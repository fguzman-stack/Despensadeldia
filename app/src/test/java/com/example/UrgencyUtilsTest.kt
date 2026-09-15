package com.example

import com.example.data.local.Product
import com.example.data.local.ProductStatus
import com.example.utils.UrgencyBucket
import com.example.utils.getUrgentProducts
import com.example.utils.urgencyBucket
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class UrgencyUtilsTest {
  private val zone = ZoneId.of("UTC")
  private val today = LocalDate.of(2026, 9, 14)
  private val now = today.atStartOfDay(zone).toInstant().toEpochMilli()

  @Test
  fun `active product expiring tomorrow is classified as tomorrow`() {
    val product = Product(
      name = "Leche",
      expirationDate = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli(),
    )

    assertEquals(UrgencyBucket.TOMORROW, product.urgencyBucket(now, zone))
  }

  @Test
  fun `resolved and snoozed products are excluded from urgent list`() {
    val active = Product(
      name = "Yogur",
      expirationDate = today.atStartOfDay(zone).toInstant().toEpochMilli(),
    )
    val resolved = Product(
      name = "Pan",
      status = ProductStatus.CONSUMED,
      expirationDate = today.atStartOfDay(zone).toInstant().toEpochMilli(),
    )
    val snoozed = Product(
      name = "Queso",
      expirationDate = today.atStartOfDay(zone).toInstant().toEpochMilli(),
      snoozeUntil = now + 1_000,
    )

    assertEquals(listOf("Yogur"), listOf(active, resolved, snoozed).getUrgentProducts(now, zone).map { it.product.name })
  }
}
