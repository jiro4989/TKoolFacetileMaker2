package com.jiro4989.tkfm.model

import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage

@Suppress("MagicNumber")
private fun createEmptyImage() = WritableImage(100, 100)

private const val SCALE_MIN = 20.0

private const val SCALE_MAX = 200.0

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

/** 影レイヤの座標を計算して返却する。croppingX,Yは中央の矩形の左上の頂点の座標。 */
internal fun calcShadowLayerAxis(
    imageWidth: Double,
    imageHeight: Double,
    croppingWidth: Double,
    croppingHeight: Double,
    croppingX: Double,
    croppingY: Double,
    croppingScale: Double
): ShadowLayerAxis {
  // +---------+----+
  // |top      |righ|
  // |    1    |t   |
  // +----+----+2   |
  // |left|    |    |
  // |    |    |    |
  // |   3+----+----+
  // |    |    4    |
  // |    |bottom   |
  // +----+---------+
  val cs = croppingScale / 100.0
  val iw = imageWidth * cs
  val ih = imageHeight * cs
  val mx =
      if (croppingX < 0) 0.0
      else if (iw - croppingWidth < croppingX) iw - croppingWidth else croppingX
  val my =
      if (croppingY < 0) 0.0
      else if (ih - croppingHeight < croppingY) ih - croppingHeight else croppingY

  val x1 = mx
  val y1 = my
  val x2 = mx + croppingWidth
  val y2 = y1
  val x3 = x1
  val y3 = my + croppingHeight
  val y4 = y3

  val top = Rectangle(0.0, 0.0, x2, y2)
  val right = Rectangle(x2, 0.0, iw - x2, y4)
  val left = Rectangle(0.0, y1, x1, ih - y1)
  val bottom = Rectangle(x3, y3, iw - x3, ih - y3)
  val result = ShadowLayerAxis(top = top, right = right, left = left, bottom = bottom)
  return result
}

/** 画像をトリミングするロジックを管理する。 */
data class CroppingImageModel(
    // Properties

    /** トリミング対象の画像 */
    val imageProperty: ObjectProperty<Image> = SimpleObjectProperty(createEmptyImage()),
    /** トリミング対象画像の横幅。JavaFXのUIとのプロパティバインド用 */
    val imageWidthProperty: DoubleProperty = SimpleDoubleProperty(288.0),
    /** トリミング対象画像の縦幅。JavaFXのUIとのプロパティバインド用 */
    val imageHeightProperty: DoubleProperty = SimpleDoubleProperty(288.0),

    // シャドウレイヤは起動直後の初期化処理で位置調整が走るためデフォルト値は何でも良い

    // Top
    val shadowTopLayerXProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowTopLayerYProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowTopLayerWidthProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowTopLayerHeightProperty: DoubleProperty = SimpleDoubleProperty(),

    // Right
    val shadowRightLayerXProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowRightLayerYProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowRightLayerWidthProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowRightLayerHeightProperty: DoubleProperty = SimpleDoubleProperty(),

    // Left
    val shadowLeftLayerXProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowLeftLayerYProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowLeftLayerWidthProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowLeftLayerHeightProperty: DoubleProperty = SimpleDoubleProperty(),

    // Bottom
    val shadowBottomLayerXProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowBottomLayerYProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowBottomLayerWidthProperty: DoubleProperty = SimpleDoubleProperty(),
    val shadowBottomLayerHeightProperty: DoubleProperty = SimpleDoubleProperty(),

    /** 画像をトリミングする際の拡縮値。JavaFXのUIとのプロパティバインド用 */
    val scaleProperty: DoubleProperty = SimpleDoubleProperty(100.0),

    /** トリミング座標 */
    val croppingPosition: PositionModel = PositionModel(0.0, 0.0),
    /** トリミング画像の矩形 */
    val croppingRectangle: RectangleModel
) {
  init {
    setShadowLayerAxis(croppingX = 10.0, croppingY = 10.0)
  }

  fun cropByBufferedImage(): Image {
    val scale = scaleProperty.get() / 100
    var x = croppingPosition.x.toInt()
    var y = croppingPosition.y.toInt()
    var width = croppingRectangle.width.toInt()
    var height = croppingRectangle.height.toInt()

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

  /**
   * トリミング座標を x, y の座標に変更し、トリミング画像を更新する。 座標が不正な範囲外の場合は下限値、上限値に変更して設定するため、異常な範囲指定に対して安全である。
   * 少数の細かい値を許容しないため、単数を切り捨てるために整数に変換してから少数に戻す。
   */
  fun move(x: Double = croppingPosition.x, y: Double = croppingPosition.y) {
    val bImg = imageProperty.get()
    val s = scaleProperty.get() / 100
    val w = bImg.width
    val h = bImg.height
    val rectWidth = croppingRectangle.width
    val rectHeight = croppingRectangle.height

    var xx = x
    var yy = y

    if (w * s - rectWidth < xx) xx = w * s - rectWidth
    if (h * s - rectHeight < yy) yy = h * s - rectHeight
    if (xx < 0) xx = 0.0
    if (yy < 0) yy = 0.0

    // 単数切り捨て
    xx = xx.toInt().toDouble()
    yy = yy.toInt().toDouble()

    croppingPosition.x = xx
    croppingPosition.y = yy
    setShadowLayerAxis(croppingX = xx, croppingY = yy)
  }

  fun moveUp(n: Double) = move(y = croppingPosition.y - n)
  fun moveDown(n: Double) = move(y = croppingPosition.y + n)
  fun moveLeft(n: Double) = move(x = croppingPosition.x - n)
  fun moveRight(n: Double) = move(x = croppingPosition.x + n)

  /** トリミング座標を指定して、画像をトリミングする。 座標はマウスでの設定を想定しており、座標はトリミング矩形の中央として解釈する。 */
  fun moveByMouse(x: Double, y: Double) {
    val (width, height) = croppingRectangle / 2.0
    move(x - width, y - height)
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
    move()
  }

  fun setScale(scale: Double) {
    var scale2 = scale

    if (scale2 < SCALE_MIN) {
      scale2 = SCALE_MIN
    } else if (SCALE_MAX < scale2) {
      scale2 = SCALE_MAX
    }

    scaleProperty.set(scale2)
    move()
  }

  private fun setShadowLayerAxis(
      imageWidth: Double = imageWidthProperty.get(),
      imageHeight: Double = imageHeightProperty.get(),
      croppingWidth: Double = croppingRectangle.width,
      croppingHeight: Double = croppingRectangle.height,
      croppingX: Double = croppingPosition.x,
      croppingY: Double = croppingPosition.y,
      croppingScale: Double = scaleProperty.get()
  ) {
    val axis =
        calcShadowLayerAxis(
            imageWidth,
            imageHeight,
            croppingWidth,
            croppingHeight,
            croppingX,
            croppingY,
            croppingScale)

    shadowTopLayerXProperty.set(axis.top.x)
    shadowTopLayerYProperty.set(axis.top.y)
    shadowTopLayerWidthProperty.set(axis.top.width)
    shadowTopLayerHeightProperty.set(axis.top.height)

    shadowRightLayerXProperty.set(axis.right.x)
    shadowRightLayerYProperty.set(axis.right.y)
    shadowRightLayerWidthProperty.set(axis.right.width)
    shadowRightLayerHeightProperty.set(axis.right.height)

    shadowLeftLayerXProperty.set(axis.left.x)
    shadowLeftLayerYProperty.set(axis.left.y)
    shadowLeftLayerWidthProperty.set(axis.left.width)
    shadowLeftLayerHeightProperty.set(axis.left.height)

    shadowBottomLayerXProperty.set(axis.bottom.x)
    shadowBottomLayerYProperty.set(axis.bottom.y)
    shadowBottomLayerWidthProperty.set(axis.bottom.width)
    shadowBottomLayerHeightProperty.set(axis.bottom.height)
  }
}

internal data class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double)

internal data class ShadowLayerAxis(
    val top: Rectangle, val right: Rectangle, val left: Rectangle, val bottom: Rectangle)
