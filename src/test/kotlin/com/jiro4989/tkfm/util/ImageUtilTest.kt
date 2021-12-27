package com.jiro4989.tkfm.util

import java.io.File
import java.io.IOException
import javafx.scene.image.Image
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class ImageUtilTest {
  @BeforeEach
  fun beforeEach() {
    val dir = File("tmp")
    dir.mkdirs()
  }

  @AfterEach
  fun afterEach() {
    val dir = File("tmp")
    dir.listFiles().forEach { it.delete() }
    dir.delete()
  }

  @Test
  fun testWriteFile() {
    val image = Image("20x20.png")
    val path = "tmp" + File.separator + "out.png"
    val file = File(path)
    assertFalse(file.exists())
    writeFile(image, file)
    assertTrue(File(path).exists())
  }
}
