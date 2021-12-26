package com.jiro4989.tkfm.model

import java.io.IOException
import javafx.scene.image.Image
import javax.xml.parsers.ParserConfigurationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.testfx.framework.junit5.*
import org.xml.sax.SAXException

@ExtendWith(ApplicationExtension::class)
public class TileImageModelTest {
  @Test
  fun testConstructor() {
    TileImageModel(createImageFormatConfigModel()).let {
      val imgs = it.getImages()
      assertEquals(2, imgs.size)
      assertEquals(4, imgs[0].size)
      assertEquals(4, imgs[1].size)
    }
  }

  @ParameterizedTest
  @CsvSource(
      "0,-65536,-65536,-65536",
      "1,0,-65536,-65536",
      "2,0,-65536,-65536",
      "3,0,-65536,-65536",
      "4,0,-65536,-65536",
      "5,0,-65536,-65536",
      "6,0,-65536,-65536",
      "7,0,-65536,-65536",
      "8,0,0,0")
  fun testBulkInsert(index: Int, want1: Int, want2: Int, want3: Int) {
    val t = TileImageModel(createImageFormatConfigModel())
    var images = (0 until 8).map { Image("20x20.png") }

    when (index) {
      0 -> t.bulkInsert(images)
      else -> t.bulkInsert(images, index)
    }

    val colCount = 4
    val x = index % colCount
    val y = index / colCount

    var reader = t.getImages()[0][0].pixelReader
    var got = reader.getArgb(0, 0)
    assertEquals(want1, got)

    if (index < 8) {
      reader = t.getImages()[y][x].pixelReader
      got = reader.getArgb(0, 0)
      assertEquals(want2, got)
    }

    reader = t.getImages()[1][3].pixelReader
    got = reader.getArgb(0, 0)
    assertEquals(want3, got)
  }

  @Test
  fun testResetImage() {
    val fmt = createImageFormatConfigModel()
    val t = TileImageModel(fmt)
    var img = t.imageProperty().get()

    assertEquals(80.0, img.width)
    assertEquals(40.0, img.height)

    fmt.selectedImageFormat.rectangle.apply {
      width = 40.0
      height = 30.0
    }
    t.resetImage()

    img = t.imageProperty().get()
    assertEquals(160.0, img.width)
    assertEquals(60.0, img.height)
  }

  @Test
  fun testClearImage() {
    val images = (0 until 8).map { Image("20x20.png") }
    val t =
        TileImageModel(createImageFormatConfigModel()).apply {
          bulkInsert(images)
          clear()
        }

    val colCount = 4
    (0 until 8).forEach { i ->
      val x = i % colCount
      val y = i / colCount
      val reader = t.getImages()[y][x].pixelReader
      val got = reader.getArgb(0, 0)
      assertEquals(0, got)
    }
  }

  @ParameterizedTest
  @CsvSource("0,0,0", "10,10,0", "20,10,1", "70,10,3", "0,20,4", "20,20,5", "79,20,7", "79,39,7")
  fun testSetImageByAxis(x: Double, y: Double, wantIndex: Int) {
    val image = Image("20x20.png")
    var t = TileImageModel(createImageFormatConfigModel()).apply { setImageByAxis(image, x, y) }

    val colCount = 4
    val xx = wantIndex % colCount
    val yy = wantIndex / colCount
    val reader = t.getImages()[yy][xx].pixelReader
    val got = reader.getArgb(0, 0)
    assertEquals(-65536, got)
  }

  private fun createImageFormatConfigModel(): ImageFormatConfigModel {
    val rect = RectangleModel(20.0, 20.0)
    val fmt =
        ImageFormatConfigModel(loadXML = false).apply {
          addAdditionalImageFormat(ImageFormatModel("test", 2, 4, rect))
          select(2)
        }
    return fmt
  }
}
