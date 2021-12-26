package com.jiro4989.tkfm.model

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class RectangleModelTest {
  @Test
  fun testConstructor() {
    val rect = RectangleModel(20.0, 40.0)
    assertEquals(20.0, rect.width)
    assertEquals(40.0, rect.height)
  }

  @Test
  fun testProperty() {
    val rect = RectangleModel(20.0, 40.0)
    assertEquals(20.0, rect.width)
    assertEquals(40.0, rect.height)

    rect.width = 100.0
    rect.height = 200.0
    assertEquals(100.0, rect.width)
    assertEquals(200.0, rect.height)
  }
}
