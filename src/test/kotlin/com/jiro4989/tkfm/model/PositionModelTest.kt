package com.jiro4989.tkfm.model

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class PositionModelTest {
  @Test
  fun testConstructor() {
    var pos = PositionModel()
    assertEquals(0.0, pos.x)
    assertEquals(0.0, pos.y)
  }

  @Test
  fun testProperty() {
    var pos = PositionModel(5.0, 7.0)
    assertEquals(5.0, pos.x)
    assertEquals(7.0, pos.y)

    pos.x = 10.0
    pos.y = 20.0
    assertEquals(10.0, pos.x)
    assertEquals(20.0, pos.y)
  }
}
