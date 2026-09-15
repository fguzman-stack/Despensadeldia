package com.example

import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import com.example.utils.BackupHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackupHelperTest {
  @Test
  fun `parse versioned json backup with products`() {
    val json = """
      {
        "schemaVersion": 1,
        "products": [
          {
            "name": "Leche",
            "category": "DAIRY_EGGS",
            "location": "FRIDGE",
            "quantity": 2.0,
            "unit": "L",
            "minimumStock": 1.0
          }
        ]
      }
    """.trimIndent()

    val preview = BackupHelper.parseProductsJson(json)

    assertEquals(1, preview.products.size)
    assertEquals(0, preview.skipped)
    assertEquals("Leche", preview.products.first().name)
    assertEquals(ProductCategory.DAIRY_EGGS, preview.products.first().category)
    assertEquals(ProductLocation.FRIDGE, preview.products.first().location)
    assertEquals(1.0, preview.products.first().minimumStock ?: 0.0, 0.0)
  }

  @Test
  fun `parse legacy array backup and skip invalid rows`() {
    val json = """
      [
        { "name": "Pan", "category": "BAKERY" },
        { "category": "PRODUCE" }
      ]
    """.trimIndent()

    val preview = BackupHelper.parseProductsJson(json)

    assertEquals(1, preview.products.size)
    assertEquals(1, preview.skipped)
    assertEquals("Pan", preview.products.first().name)
  }
}
