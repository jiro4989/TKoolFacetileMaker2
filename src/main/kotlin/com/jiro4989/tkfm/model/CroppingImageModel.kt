package com.jiro4989.tkfm.model

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javafx.beans.property.*
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage

private fun createEmptyImage() = WritableImage(100, 100)

/** 拡大した画像を返す。 */
private fun scaledImage(image: BufferedImage, scale: Double): BufferedImage {
  val width = image.getWidth() * scale
  val height = image.getHeight() * scale
  val newImage = BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB)

  newImage.createGraphics().apply {
    setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    scale(scale, scale)
    drawImage(image, 0, 0, null)
    dispose()
  }

  return newImage
}

/** 画像をトリミングするロジックを管理する。 */
data class CroppingImageModel(
    // Properties

    /** トリミング対象の画像 */
    val imageProperty: ObjectProperty<Image> = SimpleObjectProperty(createEmptyImage()),
    /** トリミングされた結果のプレビュー画像 */
    val croppedImageProperty: ObjectProperty<Image> = SimpleObjectProperty(WritableImage(144, 144)),
    /** トリミング対象画像の横幅。JavaFXのUIとのプロパティバインド用 */
    val imageWidthProperty: DoubleProperty = SimpleDoubleProperty(288.0),
    /** トリミング対象画像の縦幅。JavaFXのUIとのプロパティバインド用 */
    val imageHeightProperty: DoubleProperty = SimpleDoubleProperty(288.0),
    /** 画像をトリミングする際の拡縮値。JavaFXのUIとのプロパティバインド用 */
    val scaleProperty: DoubleProperty = SimpleDoubleProperty(100.0),

    /** トリミング座標 */
    val position: PositionModel = PositionModel(0.0, 0.0),
    /** トリミング画像の矩形 */
    val rectangle: RectangleModel
) {

  fun crop(): Image {
    // 画面上は百分率で表示しているため少数に変換
    val scale = scaleProperty.get() / 100

    // 座標と矩形にスケールをかけてトリミングサイズを調整
    var x = position.x / scale
    var y = position.y / scale
    val width = rectangle.width / scale
    val height = rectangle.height / scale

    // 0未満の座標はNGなので0で上書きして調整
    if (x < 0) x = 0.0
    if (y < 0) y = 0.0

    val img = imageProperty.get()

    // 画像サイズ0は通常起こり得ないはず
    if (img.width <= 0 || img.height <= 0) {
      return croppedImageProperty.get()
    }

    // 座標に矩形幅を足した値が画像全体の幅より大きくなってはいけない
    if (img.width < x + width || img.height < y + height) {
      return croppedImageProperty.get()
    }

    val pix = img.pixelReader
    return WritableImage(pix, x.toInt(), y.toInt(), width.toInt(), height.toInt())
  }

  fun cropByBufferedImage(): Image {
    val scale = scaleProperty.get() / 100
    var x = position.x.toInt()
    var y = position.y.toInt()
    var width = rectangle.width.toInt()
    var height = rectangle.height.toInt()

    val bImg = SwingFXUtils.fromFXImage(imageProperty.get(), null)
    val scaledImg = scaledImage(bImg, scale)
    var w = scaledImg.width
    var h = scaledImg.height
    if (x < 0) x = 0
    if (y < 0) y = 0
    if (w < x + width) width = (width - ((x + width) - w)).toInt()
    if (h < y + height) height = (height - ((y + height) - h)).toInt()
    val subImg = scaledImg.getSubimage(x, y, width, height)

    val dstImg = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
    (dstImg.getGraphics() as Graphics2D).apply {
      setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
      drawImage(subImg, 0, 0, null)
      dispose()
    }

    val wImg: WritableImage = SwingFXUtils.toFXImage(dstImg, null)
    return wImg
  }

  fun move(x: Double, y: Double) {
    val bImg = imageProperty.get()
    val s = scaleProperty.get() / 100
    val w = bImg.width
    val h = bImg.height
    val rectWidth = rectangle.width
    val rectHeight = rectangle.height

    var xx = x
    var yy = y

    if (w * s - rectWidth < xx) xx = w * s - rectWidth
    if (h * s - rectHeight < yy) yy = h * s - rectHeight
    if (xx < 0) xx = 0.0
    if (yy < 0) yy = 0.0

    position.x = xx
    position.y = yy
    croppedImageProperty.set(crop())
  }

  fun move() {
    val x = position.x
    val y = position.y
    move(x, y)
  }

  fun moveUp(n: Double) {
    val x = position.x
    val y = position.y - n
    move(x, y)
  }

  fun moveRight(n: Double) {
    val x = position.x + n
    val y = position.y
    move(x, y)
  }

  fun moveDown(n: Double) {
    val x = position.x
    val y = position.y + n
    move(x, y)
  }

  fun moveLeft(n: Double) {
    val x = position.x - n
    val y = position.y
    move(x, y)
  }

  /** Centering */
  fun moveByMouse(x: Double, y: Double) {
    val w = rectangle.width
    val h = rectangle.height
    val xx = x - w / 2
    val yy = y - h / 2
    move(xx, yy)
  }

  fun clearImage() {
    setImage(createEmptyImage())
  }

  fun scaleUp(n: Double) {
    val s = scaleProperty.get()
    val scale = s + n
    setScale(scale)
  }

  fun scaleDown(n: Double) {
    val s = scaleProperty.get()
    val scale = s - n
    setScale(scale)
  }

  // setter ///////////////////////////////////////////////////////////////////

  fun setImage(image: Image) {
    imageProperty.set(image)
    imageWidthProperty.set(image.getWidth())
    imageHeightProperty.set(image.getHeight())
    croppedImageProperty.set(crop())
  }

  fun setScale(scale: Double) {
    val MIN_SCALE = 20.0
    val MAX_SCALE = 200.0
    var scale2 = scale

    if (scale2 < MIN_SCALE) {
      scale2 = MIN_SCALE
    } else if (MAX_SCALE < scale2) {
      scale2 = MAX_SCALE
    }

    scaleProperty.set(scale2)
    move()
  }
}
