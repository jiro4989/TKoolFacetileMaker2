package com.jiro4989.tkfm.model

import java.io.File
import javafx.beans.property.*
import javafx.scene.image.Image
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import org.testfx.framework.junit5.*

@ExtendWith(ApplicationExtension::class)
class CroppingImageModelTest {
  @ParameterizedTest
  @CsvSource(
      "50.0, 0.0, 0.0, 40.0, 40.0",
      "100.0, -5.0, 0.0, 20.0, 20.0",
      "100.0, 0.0, -5.0, 20.0, 20.0",
      "100.0, 0.0, 0.0, 20.0, 20.0")
  fun testCrop(scale: Double, x: Double, y: Double, wantWidth: Double, wantHeight: Double) {
    val path = resourcePath("/sample1.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(x, y)
    val rect = RectangleModel(20.0, 20.0)
    val c = crop(img, pos, rect, scale)
    val got = c.crop()

    assertEquals(wantWidth, got.width)
    assertEquals(wantHeight, got.height)
  }

  @ParameterizedTest
  @CsvSource(
      "50.0, 0.0, 0.0, 10.0, 10.0, 10.0, 10.0",
      "50.0, 5.0, 5.0, 10.0, 10.0, 5.0, 5.0",
      "100.0, 0.0, 0.0, 20.0, 20.0, 20.0, 20.0",
      "100.0, -10.0, 0.0, 20.0, 20.0, 20.0, 20.0",
      "100.0, 0.0, -10.0, 20.0, 20.0, 20.0, 20.0",
      "100.0, 10.0, 0.0, 20.0, 20.0, 10.0, 20.0",
      "100.0, 0.0, 10.0, 20.0, 20.0, 20.0, 10.0")
  fun testCropByBufferedImage(
      scale: Double,
      x: Double,
      y: Double,
      width: Double,
      height: Double,
      wantWidth: Double,
      wantHeight: Double
  ) {
    val path = resourcePath("/20x20.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(x, y)
    val rect = RectangleModel(width, height)
    val c = crop(img, pos, rect, scale)
    val got = c.cropByBufferedImage()

    assertEquals(wantWidth, got.width)
    assertEquals(wantHeight, got.height)
  }

  @ParameterizedTest
  @CsvSource(
      "up    , 0.0  , 10.0 , 10.0",
      "up    , 5.0  , 10.0 , 5.0",
      "up    , 10.0 , 10.0 , 0.0",
      "up    , 20.0 , 10.0 , 0.0",
      "right , 0.0  , 10.0 , 10.0",
      "right , 5.0  , 15.0 , 10.0",
      "right , 10.0 , 20.0 , 10.0",
      "right , 200.0, 130.0, 10.0",
      "down  , 0.0  , 10.0 , 10.0",
      "down  , 5.0  , 10.0 , 15.0",
      "down  , 10.0 , 10.0 , 20.0",
      "down  , 200.0, 10.0 , 36.0",
      "left  , 0.0  , 10.0 , 10.0",
      "left  , 5.0  , 5.0  , 10.0",
      "left  , 10.0 , 0.0  , 10.0",
      "left  , 20.0 , 0.0  , 10.0")
  fun testMovePosition(act: String, moveWidth: Double, wantX: Double, wantY: Double) {
    val path = resourcePath("/sample1.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    var pos = PositionModel(10.0, 10.0)
    val rect = RectangleModel(20.0, 30.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)

    when (act) {
      "up" -> c.moveUp(moveWidth)
      "right" -> c.moveRight(moveWidth)
      "down" -> c.moveDown(moveWidth)
      "left" -> c.moveLeft(moveWidth)
    }

    pos = c.position
    assertEquals(wantX, pos.x)
    assertEquals(wantY, pos.y)
    assertEquals(20.0, c.croppedImageProperty.get().width)
    assertEquals(30.0, c.croppedImageProperty.get().height)
  }

  @ParameterizedTest
  @CsvSource("0.0, 0.0, 0.0, 0.0", "40.0, 40.0, 30.0, 20.0", "1000.0, 1000.0, 130.0, 26.0")
  fun testMoveByMouse(x: Double, y: Double, wantX: Double, wantY: Double) {
    val path = resourcePath("/sample1.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    var pos = PositionModel(20.0, 30.0)
    val rect = RectangleModel(20.0, 40.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)
    c.moveByMouse(x, y)
    pos = c.position
    assertEquals(wantX, pos.x)
    assertEquals(wantY, pos.y)
  }

  @ParameterizedTest
  @CsvSource(
      "up, 0, 100.0",
      "up, 5, 105.0",
      "up, 1000, 200.0",
      "down, 0, 100.0",
      "down, 5, 95.0",
      "down, 1000, 20.0")
  fun testScaling(act: String, n: Double, wantScale: Double) {
    val path = resourcePath("/sample1.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(10.0, 10.0)
    val rect = RectangleModel(20.0, 30.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)

    when (act) {
      "up" -> c.scaleUp(n)
      "down" -> c.scaleDown(n)
    }

    assertEquals(wantScale, c.scaleProperty.get())
  }

  @Test
  fun testImageSize() {
    val path = resourcePath("/20x20.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(0.0, 0.0)
    val rect = RectangleModel(20.0, 20.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)

    assertEquals(20.0, c.imageProperty.get().width)
    assertEquals(20.0, c.imageProperty.get().height)
    assertEquals(288.0, c.imageWidthProperty.get())
    assertEquals(288.0, c.imageHeightProperty.get())
  }

  @Test
  fun testPosition() {
    val path = resourcePath("/20x20.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(30.0, 50.0)
    val rect = RectangleModel(20.0, 20.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)
    val p = c.position

    assertEquals(30.0, p.x)
    assertEquals(50.0, p.y)
  }

  @Test
  fun testRectangle() {
    val path = resourcePath("/20x20.png")
    val file = File(path)
    val img = Image(file.toURI().toString())
    val pos = PositionModel(30.0, 50.0)
    val rect = RectangleModel(20.0, 30.0)
    val scale = 100.0
    val c = crop(img, pos, rect, scale)
    val r = c.rectangle

    assertEquals(20.0, r.width)
    assertEquals(30.0, r.height)
  }

  private fun resourcePath(path: String) = this.javaClass.getResource(path).getPath()
  private fun crop(img: Image, pos: PositionModel, rect: RectangleModel, scale: Double) =
      CroppingImageModel(
          imageProperty = SimpleObjectProperty(img),
          position = pos,
          rectangle = rect,
          scaleProperty = SimpleDoubleProperty(scale))
}
