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
class CroppingImageModel {
  /** トリミング対象の画像 */
  private val image: ObjectProperty<Image> = SimpleObjectProperty(createEmptyImage())
  /** トリミングされた結果のプレビュー画像 */
  private val croppedImage: ObjectProperty<Image> = SimpleObjectProperty(WritableImage(144, 144))
  /** トリミング座標 */
  private val cropPos = PositionModel(0.0, 0.0)
  /** トリミング画像の矩形 */
  private val cropRect: RectangleModel
  /** トリミング対象画像の横幅。JavaFXのUIとのプロパティバインド用 */
  private val imageWidth: DoubleProperty = SimpleDoubleProperty(288.0)
  /** トリミング対象画像の縦幅。JavaFXのUIとのプロパティバインド用 */
  private val imageHeight: DoubleProperty = SimpleDoubleProperty(288.0)
  /** 画像をトリミングする際の拡縮値。JavaFXのUIとのプロパティバインド用 */
  private val scale: DoubleProperty = SimpleDoubleProperty(100.0)

  /**
   * 単体テストで使う目的。通常プログラムでは使わない。
   *
   * @param image
   * @param pos
   * @param rect
   * @param scale
   */
  constructor(image: Image, pos: PositionModel, rect: RectangleModel, scale: Double) {
    this.image.set(image)
    this.cropPos.setX(pos.getX())
    this.cropPos.setY(pos.getY())
    this.cropRect = rect
    this.scale.set(scale)
  }

  constructor(rect: RectangleModel) {
    this.cropRect = rect
  }

  fun crop(): Image {
    // 画面上は百分率で表示しているため少数に変換
    val scale = this.scale.get() / 100

    // 座標と矩形にスケールをかけてトリミングサイズを調整
    var x = cropPos.x / scale
    var y = cropPos.y / scale
    val width = cropRect.width / scale
    val height = cropRect.height / scale

    // 0未満の座標はNGなので0で上書きして調整
    if (x < 0) x = 0.0
    if (y < 0) y = 0.0

    val img = image.get()

    // 画像サイズ0は通常起こり得ないはず
    if (img.width <= 0 || img.height <= 0) {
      return croppedImage.get()
    }

    // 座標に矩形幅を足した値が画像全体の幅より大きくなってはいけない
    if (img.width < x + width || img.height < y + height) {
      return croppedImage.get()
    }

    val pix = img.pixelReader
    return WritableImage(pix, x.toInt(), y.toInt(), width.toInt(), height.toInt())
  }

  fun cropByBufferedImage(): Image {
    val scale = this.scale.get() / 100
    var x = cropPos.x.toInt()
    var y = cropPos.y.toInt()
    var width = cropRect.width.toInt()
    var height = cropRect.height.toInt()

    val bImg = SwingFXUtils.fromFXImage(image.get(), null)
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
    val bImg = image.get()
    val s = scale.get() / 100
    val w = bImg.width
    val h = bImg.height
    val rectWidth = cropRect.width
    val rectHeight = cropRect.height

    var xx = x
    var yy = y

    if (w * s - rectWidth < xx) xx = w * s - rectWidth
    if (h * s - rectHeight < yy) yy = h * s - rectHeight
    if (xx < 0) xx = 0.0
    if (yy < 0) yy = 0.0

    cropPos.setX(xx)
    cropPos.setY(yy)
    croppedImage.set(crop())
  }

  fun move() {
    val x = cropPos.x
    val y = cropPos.y
    move(x, y)
  }

  fun moveUp(n: Double) {
    val x = cropPos.x
    val y = cropPos.y - n
    move(x, y)
  }

  fun moveRight(n: Double) {
    val x = cropPos.x + n
    val y = cropPos.y
    move(x, y)
  }

  fun moveDown(n: Double) {
    val x = cropPos.x
    val y = cropPos.y + n
    move(x, y)
  }

  fun moveLeft(n: Double) {
    val x = cropPos.x - n
    val y = cropPos.y
    move(x, y)
  }

  /** Centering */
  fun moveByMouse(x: Double, y: Double) {
    val w = cropRect.getWidth()
    val h = cropRect.getHeight()
    val xx = x - w / 2
    val yy = y - h / 2
    move(xx, yy)
  }

  fun clearImage() {
    setImage(createEmptyImage())
  }

  fun scaleUp(n: Double) {
    val s = scale.get()
    val scale = s + n
    setScale(scale)
  }

  fun scaleDown(n: Double) {
    val s = scale.get()
    val scale = s - n
    setScale(scale)
  }

  // property /////////////////////////////////////////////////////////////////

  fun imageProperty() = image
  fun croppedImageProperty() = croppedImage
  fun imageWidthProperty() = imageWidth
  fun imageHeightProperty() = imageHeight
  fun scaleProperty() = scale

  // getter ///////////////////////////////////////////////////////////////////

  fun getPosition() = cropPos
  fun getRectangle() = cropRect

  // setter ///////////////////////////////////////////////////////////////////

  fun setImage(image: Image) {
    this.image.set(image)
    this.imageWidth.set(image.getWidth())
    this.imageHeight.set(image.getHeight())
    croppedImage.set(crop())
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

    this.scale.set(scale2)
    move()
  }
}
