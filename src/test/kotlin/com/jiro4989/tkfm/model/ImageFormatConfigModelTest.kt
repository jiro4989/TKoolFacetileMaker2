package com.jiro4989.tkfm.model

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.ArrayList
import org.junit.jupiter.api.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.testfx.framework.junit5.*
import org.xml.sax.SAXParseException

@ExtendWith(ApplicationExtension::class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageFormatConfigModelTest {
  @AfterAll
  fun afterAll() {
    // 再帰的にフォルダを削除
    Paths.get(".", "tmp").toFile().walkBottomUp().forEach { it.delete() }
  }

  @Test
  fun testConstructor() {
    ImageFormatConfigModel()
  }

  @ParameterizedTest
  @CsvSource("0, 144.0, 144.0", "1, 96.0, 96.0", "2, 30.0, 30.0")
  fun testSelect(selectIndex: Int, wantWidth: Double, wantHeight: Double) {
    val fmt = ImageFormatConfigModel(loadXML = false)
    fmt.addAdditionalImageFormat(ImageFormatModel("test", 3, 3, RectangleModel(30.0, 30.0)))
    fmt.select(selectIndex)

    val selected = fmt.selectedImageFormat
    val rect = selected.rectangle
    assertEquals(wantWidth, rect.width)
    assertEquals(wantHeight, rect.height)
  }

  @ParameterizedTest
  @CsvSource(
      "/image_format_3x3_100x100.xml, 2, 100.0, 100.0",
      "/image_format_3x3_100x100_2.xml, 3, 200.0, 300.0")
  fun testLoadXMLFile(filepath: String, selectIndex: Int, wantWidth: Double, wantHeight: Double) {
    val path = resourcePath(filepath)
    val fmt = ImageFormatConfigModel(loadXML = false)
    fmt.loadXMLFile(path)
    fmt.select(selectIndex)

    val selected = fmt.selectedImageFormat
    val rect = selected.rectangle
    assertEquals(wantWidth, rect.width)
    assertEquals(wantHeight, rect.height)
  }

  @Test
  fun testLoadXMLFileSkipped() {
    val path = Paths.get(".", "not_found", "not_found.xml")
    val fmt = ImageFormatConfigModel(loadXML = false)
    fmt.loadXMLFile(path)

    val got = fmt.getAdditionalImageFormatNames().size
    assertEquals(0, got)
  }

  @Test
  fun testLoadXMLFileSAXParseException() {
    val path = resourcePath("/image_format_illegal.xml")
    val fmt = ImageFormatConfigModel(loadXML = false)
    assertThrows<SAXParseException> { fmt.loadXMLFile(path) }
  }

  @Test
  fun testWriteXMLFile() {
    val fmt = ImageFormatConfigModel(loadXML = false)
    val path = Paths.get(".", "tmp", "sushi.xml")
    fmt.writeXMLFile(path)
    assertTrue(Files.exists(path))
    assertTrue(Files.isDirectory(Paths.get(".", "tmp")))
  }

  @Test
  fun testWriteXMLFileDefault() {
    val fmt = ImageFormatConfigModel(loadXML = false)
    fmt.writeXMLFile()
  }

  @Test
  fun testWriteXML() {
    val fmt = ImageFormatConfigModel(loadXML = false)
    val list = mutableListOf(ImageFormatModel("3x3", 3, 3, RectangleModel(100.0, 100.0)))
    StringWriter().use { w: Writer ->
      fmt.writeXML(w, list)
      val got = w.toString().trim()
      val path = resourcePath("/image_format_3x3_100x100.xml")
      val want = Files.readString(path).trim()
      assertEquals(want, got)
    }
  }

  @Test
  fun testWriteXML2Items() {
    val fmt = ImageFormatConfigModel(loadXML = false)
    val list =
        mutableListOf(
            ImageFormatModel("3x3", 3, 3, RectangleModel(100.0, 100.0)),
            ImageFormatModel("4x5", 4, 5, RectangleModel(200.0, 300.0)))

    StringWriter().use { w: Writer ->
      fmt.writeXML(w, list)
      val got = w.toString().trim()
      val path = resourcePath("/image_format_3x3_100x100_2.xml")
      val want = Files.readString(path).trim()
      assertEquals(want, got)
    }
  }

  @ParameterizedTest
  @CsvSource("true", "false")
  fun testExistsDeletableImageFormats(add: Boolean) {
    val fmt = ImageFormatConfigModel(loadXML = false)
    if (add)
        fmt.addAdditionalImageFormat(ImageFormatModel("3x3", 3, 3, RectangleModel(100.0, 100.0)))
    assertEquals(add, fmt.existsDeletableImageFormats())
  }

  @Test
  fun testGetAdditionalImageFormatNames() {
    val fmt = ImageFormatConfigModel(loadXML = false)
    fmt.addAdditionalImageFormat(ImageFormatModel("3x3", 3, 3, RectangleModel(100.0, 100.0)))
    fmt.addAdditionalImageFormat(ImageFormatModel("3x4", 3, 3, RectangleModel(100.0, 100.0)))

    val got = fmt.getAdditionalImageFormatNames()
    val want = listOf("1. 3x3", "2. 3x4")
    assertEquals(want, got)
  }

  @ParameterizedTest
  @CsvSource("0, 20.0, 30.0", "1, 100.0, 100.0")
  fun testDeleteAdditionalImageFormat(selectIndex: Int, wantWidth: Double, wantHeight: Double) {
    val fmt =
        ImageFormatConfigModel(loadXML = false).apply {
          addAdditionalImageFormat(ImageFormatModel("3x3", 3, 3, RectangleModel(100.0, 100.0)))
          addAdditionalImageFormat(ImageFormatModel("3x4", 3, 3, RectangleModel(20.0, 30.0)))
          deleteAdditionalImageFormat(selectIndex)
          select(2)
        }

    val got = fmt.selectedImageFormat
    val gotR = got.rectangle
    assertEquals(wantWidth, gotR.width)
    assertEquals(wantHeight, gotR.height)
  }

  private fun resourcePath(path: String) = Paths.get(this.javaClass.getResource(path).getPath())
}
