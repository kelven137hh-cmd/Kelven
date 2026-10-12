package com.example

import com.example.data.local.model.ProductEntity
import com.example.model.CartItem
import com.example.model.MozFormatters
import com.example.model.PlanType
import com.example.model.VisualTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZapBusinessLogicTest {

  @Test
  fun testMozFormatters_formatMt() {
    val formatted1 = MozFormatters.formatMt(800.0)
    assertTrue(formatted1.contains("800") && formatted1.contains("MT"))

    val formatted2 = MozFormatters.formatMt(2800.0)
    assertTrue(formatted2.contains("MT"))
  }

  @Test
  fun testMozFormatters_normalizeMozPhone() {
    // Plain 9-digit
    val phone1 = "841234567"
    assertEquals("258841234567", MozFormatters.normalizeMozPhone(phone1))

    // Formatted with country code
    val phone2 = "+258 84 123 4567"
    assertEquals("258841234567", MozFormatters.normalizeMozPhone(phone2))
  }

  @Test
  fun testPlanLimits() {
    assertEquals(10, PlanType.FREE.maxProducts)
    assertEquals(0, PlanType.FREE.priceMonthlyMt)

    assertEquals(100, PlanType.PRO.maxProducts)
    assertEquals(299, PlanType.PRO.priceMonthlyMt)
  }

  @Test
  fun testCartCalculation() {
    val product1 = ProductEntity(
      id = "p1",
      storeId = "s1",
      name = "Camisola Preta",
      price = 800.0,
      promotionalPrice = null
    )
    val item1 = CartItem(product = product1, quantity = 2)
    assertEquals(1600.0, item1.subtotal, 0.01)

    // With promo price
    val product2 = ProductEntity(
      id = "p2",
      storeId = "s1",
      name = "Calças Linho",
      price = 2000.0,
      promotionalPrice = 1500.0
    )
    val item2 = CartItem(product = product2, quantity = 1)
    assertEquals(1500.0, item2.subtotal, 0.01)
  }

  @Test
  fun testVisualTemplates() {
    assertEquals(VisualTemplate.FEMININE, VisualTemplate.fromId("FEMININE"))
    assertEquals(VisualTemplate.SHOES, VisualTemplate.fromId("SHOES"))
    assertEquals(VisualTemplate.PERFUME, VisualTemplate.fromId("PERFUME"))
  }
}
