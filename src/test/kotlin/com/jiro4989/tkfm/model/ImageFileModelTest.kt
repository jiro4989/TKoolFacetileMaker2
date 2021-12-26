package com.jiro4989.tkfm.model

import java.io.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class ImageFileModelTest {
  @Test
  fun testToString() {
    val f = ImageFileModel(File("t.png"))
    assertEquals("t.png", f.toString())
  }

  @Test
  fun testReadImage() {
    val path = this.javaClass.getResource("/20x20.png").getPath()
    val file = File(path)
    val f = ImageFileModel(file)
    val got = f.readImage()
    val reader = got.getPixelReader()
    val argb = reader.getArgb(0, 0)
    assertEquals(-65536, argb)
  }
}
