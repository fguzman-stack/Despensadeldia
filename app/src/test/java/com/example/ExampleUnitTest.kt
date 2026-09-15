package com.example

import com.example.data.local.ProductCategory
import com.example.data.local.ProductLocation
import com.example.utils.ExpiryPredictor
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun `expiry predictor estimates meat in fridge as urgent`() {
    assertEquals(
      3,
      ExpiryPredictor.estimateExpiryDays(
        ProductCategory.MEAT_SEAFOOD,
        ProductLocation.FRIDGE,
      ),
    )
  }
}
