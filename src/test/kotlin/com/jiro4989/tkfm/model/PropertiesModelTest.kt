package com.jiro4989.tkfm.model

import java.io.File
import java.io.FileWriter
import java.io.IOException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class PropertiesModelTest {
  @Test
  fun testWindowConstructor() {
    val p = WindowPropertiesModel()
    assertEquals(100.0, p.x)
    assertEquals(100.0, p.y)
    assertEquals(1280.0, p.width)
    assertEquals(760.0, p.height)
  }

  @Test
  fun testLoadStoreWindow() {
    val p = WindowPropertiesModel(file = configFile("test_window"))
    p.x = 100.1
    p.y = 200.2
    p.width = 300.3
    p.height = 400.4
    p.store()
    p.load()

    assertEquals(100.1, p.x)
    assertEquals(200.2, p.y)
    assertEquals(300.3, p.width)
    assertEquals(400.4, p.height)
  }

  @Test
  fun testLoadEmptyProperties() {
    val dir = File("config")
    dir.mkdirs()

    val filename = "config/test_window_empty.properties"
    val file = File(filename)
    val fw = FileWriter(file)
    val body = "x=\ny=\nwidth=\nheight=\n"
    fw.write(body)
    fw.close()

    val p = WindowPropertiesModel(file = configFile("test_window_empty"))
    p.load()
    assertEquals(100.0, p.x)
    assertEquals(100.0, p.y)
    assertEquals(1280.0, p.width)
    assertEquals(760.0, p.height)
  }

  @Test
  fun testLoadEmptyProperties2() {
    val dir = File("config")
    dir.mkdirs()

    val filename = "config/test_window_empty.properties"
    val file = File(filename)
    val fw = FileWriter(file)
    val body = "x=90.0\ny=\nwidth=\nheight=\n"
    fw.write(body)
    fw.close()

    val p = WindowPropertiesModel(file = configFile("test_window_empty"))
    p.load()
    assertEquals(90.0, p.x)
    assertEquals(100.0, p.y)
    assertEquals(1280.0, p.width)
    assertEquals(760.0, p.height)
  }

  @Test
  fun testLoadEmptyFile() {
    val p = WindowPropertiesModel(file = configFile("test_window_not_exists"))
    p.load()
    assertEquals(100.0, p.x)
    assertEquals(100.0, p.y)
    assertEquals(1280.0, p.width)
    assertEquals(760.0, p.height)
  }

  @Test
  fun testChosedFileConstructor() {
    val p = ChoosedFilePropertiesModel()
    assertNull(p.openedFile)
    assertNull(p.savedFile)
    p.load()
  }

  @Test
  fun testLoadStoreChoosedFile() {
    val p = ChoosedFilePropertiesModel(file = configFile("test_choosed_file"))
    // 存在するファイルなら何でも良い
    p.openedFile = File("src/main/java/com/jiro4989/tkfm/Version.java")
    p.savedFile = File("src/main/kotlin/com/jiro4989/tkfm/Main.kt")
    p.store()

    p.load()
    assertNotNull(p.openedFile)
    p.openedFile?.let { assertEquals("Version.java", it.name) }
    assertNotNull(p.savedFile)
    p.savedFile?.let { assertEquals("Main.kt", it.name) }
  }

  @Test
  fun testLoadNotExistsChoosedFile() {
    val p = ChoosedFilePropertiesModel(file = configFile("test_not_exists_choosed_file"))
    p.load() // エラーが出ないことを検証
    assertNull(p.openedFile)
    assertNull(p.savedFile)
  }

  @Test
  fun testReadFileFromProperties() {
    val dir = File("config")
    dir.mkdirs()

    val filename = "config/test_choosed_file_no_file.properties"
    val file = File(filename)
    val fw = FileWriter(file)
    val body =
        "opened_file_dir=config\nopened_file_file=not_exists_file.png\nsaved_file_dir=sushi\nsaved_file_file=sushi.png"
    fw.write(body)
    fw.close()

    val p = ChoosedFilePropertiesModel(file = configFile("test_choosed_file_no_file"))
    p.load()
    assertNotNull(p.openedFile)
    assertNull(p.savedFile)
  }
}
