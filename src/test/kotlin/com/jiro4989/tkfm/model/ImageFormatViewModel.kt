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
class ImageFormatViewModelTest {
  @ParameterizedTest
  @CsvSource(
      "true, My format, 2, 4, 144, 144",
      "false, '', 2, 4, 144, 144",
      "false, My format, '', 4, 144, 144",
      "false, My format, 2, '', 144, 144",
      "false, My format, 2, 4, '', 144",
      "false, My format, 2, 4, 144, ''")
  fun testValidate(
      want: Boolean, name: String, row: String, col: String, tileWidth: String, tileHeight: String
  ) {
    val model = ImageFormatViewModel(name, row, col, tileWidth, tileHeight)
    val got = model.validate()
    assertEquals(want, got)
  }

  @ParameterizedTest
  @CsvSource(
      "true, 1, false",
      "true, 10, false",
      "true, 999, false",
      "false, 0, false",
      "false, -1, false",
      "false, a, false",
      "false, 01, false",
      "false, 1a, false",
      "false, あ, false",
      "true, '', true",
      "false, '', false")
  fun testIsAvailableInteger(want: Boolean, value: String, emptyOK: Boolean) {
    var got = isAvailableInteger(value, emptyOK)
    assertEquals(want, got)
  }
}
