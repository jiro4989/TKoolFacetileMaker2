package com.jiro4989.tkfm.model

import javafx.beans.property.*
import javafx.scene.image.*

/** リストの画像データをタイル状に並べた1枚の画像ファイルとして出力するロジックを管理する。 */
class TileImageModel {
  /** 画像フォーマット */
  private val imageFormat: ImageFormatModel

  /** タイル画像のリスト */
  private val images: MutableList<MutableList<Image>> = mutableListOf()

  /** JavaFX用の画像プロパティ */
  private val image: ObjectProperty<Image>

  constructor(model: ImageFormatConfigModel) {
    this.imageFormat = model.getSelectedImageFormat()

    var img = outputTileImage()
    this.image = SimpleObjectProperty(img)
    resetImages()
  }

  fun clear() {
    val rowCount = imageFormat.rowProperty().get()
    val colCount = imageFormat.colProperty().get()
    (0 until rowCount).forEach { y ->
      (0 until colCount).forEach { x ->
        var img = tileImage()
        images.get(y).set(x, img)
      }
    }
    draw()
  }

  fun bulkInsert(images: List<Image>) {
    bulkInsert(images, 0)
  }

  fun bulkInsert(images: List<Image>, startIndex: Int) {
    val rowCount = imageFormat.rowProperty().get()
    val colCount = imageFormat.colProperty().get()
    val size = images.size
    (startIndex until startIndex + size).forEach(
        fun(i: Int) {
          if (rowCount * colCount <= i) {
            return
          }
          var x = i % colCount
          var y = i / colCount
          var img = images.get(i - startIndex)
          setImage(img, x, y)
        })
  }

  fun resetImage() {
    image.set(outputTileImage())
    resetImages()
  }

  fun setImageByAxis(img: Image, mx: Double, my: Double) {
    val rowCount = imageFormat.rowProperty().get()
    val colCount = imageFormat.colProperty().get()
    val i = image.get()
    val w = i.getWidth()
    val h = i.getHeight()
    var x = (mx / (w / colCount)).toInt()
    var y = (my / (h / rowCount)).toInt()
    setImage(img, x, y)
  }

  // property /////////////////////////////////////////////////////////////////

  fun imageProperty() = image

  // setter ///////////////////////////////////////////////////////////////////

  fun setImage(img: Image, x: Int, y: Int) {
    images.get(y).set(x, img)
    draw()
  }

  // private methods //////////////////////////////////////////////////////////

  private fun draw() {
    val rowCount = getRow()
    val colCount = getCol()
    val img = image.get()
    if (img is WritableImage) {
      val writer = img.getPixelWriter()
      (0 until rowCount).forEach { y ->
        (0 until colCount).forEach { x ->
          val image = images.get(y).get(x)
          val w = image.getWidth().toInt()
          val h = image.getHeight().toInt()
          val x2 = x * w
          val y2 = y * h
          val reader = image.getPixelReader()
          val fmt = PixelFormat.getIntArgbInstance()
          val buf = IntArray(w * h)
          val offset = 0
          val stride = w
          reader.getPixels(0, 0, w, h, fmt, buf, offset, stride)
          writer.setPixels(x2, y2, w, h, fmt, buf, offset, stride)
        }
      }
    }
  }

  private fun tileImage(): Image {
    val w = getWidth()
    val h = getHeight()
    return WritableImage(w, h)
  }

  private fun outputTileImage(): Image {
    val rowCount = getRow()
    val colCount = getCol()
    val w = getWidth()
    val h = getHeight()
    return WritableImage(colCount * w, rowCount * h)
  }

  private fun resetImages() {
    val rowCount = getRow()
    val colCount = getCol()
    images.clear()
    (0 until rowCount).forEach { images += (0 until colCount).map { tileImage() }.toMutableList() }
  }

  private fun getRow() = imageFormat.rowProperty().get()
  private fun getCol() = imageFormat.colProperty().get()
  private fun getWidth() = imageFormat.rectangle.width.toInt()
  private fun getHeight() = imageFormat.rectangle.height.toInt()
  internal fun getImages() = images
}
