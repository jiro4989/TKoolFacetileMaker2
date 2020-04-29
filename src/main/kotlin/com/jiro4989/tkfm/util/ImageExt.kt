package jiro.app.util

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import jiro.app.data.Point
import jiro.app.data.Rectangle
import jiro.app.data.Size
import jiro.app.model.VersionModel

/**
 * 画像全体のpixelを返す
 */
fun Image.getPixels(version: VersionModel): IntArray {
    val point = Point(version = version)
    val size = Size(width, height)
    val rectangle = Rectangle(point, size)
    return this.getPixels(rectangle)
}

/**
 * 指定の矩形の範囲のpixelを返す
 */
fun Image.getPixels(rectangle: Rectangle): IntArray {
    val point = rectangle.point
    val size = rectangle.size

    val x = point.x.toInt()
    val y = point.y.toInt()
    val w = size.width.toInt()
    val h = size.height.toInt()
    val reader = this.pixelReader
    val pixels = IntArray(w * h)
    reader.getPixels(x, y, w, h, FMT, pixels, 0, w)
    return pixels
}

/**
 * 指定の位置から切り出した画像を返す。
 * 切り出し範囲は、指定の位置からIMAGE_WIDTH, IMAGE_HEIGHTの矩形となる。
 */
fun Image.getTrimmedImage(point:Point, version:VersionModel): Image {
    val size = Size(version.getImageOneTileWidth().toDouble(), version.getImageOneTileHeight().toDouble())
    val rectangle = Rectangle(point, size)
    return getTrimmedImage(rectangle)
}

/**
 * 指定の位置からの切り出した画像を返す
 */
fun Image.getTrimmedImage(rectangle: Rectangle): Image {
    val point = rectangle.point
    val size = rectangle.size
    val reader = this.pixelReader

    val x = point.x.toInt()
    val y = point.y.toInt()
    val w = size.width.toInt()
    val h = size.height.toInt()
    val pixels = IntArray(w * h)
    reader.getPixels(x, y, w, h, FMT, pixels, 0, w)

    val wImg = WritableImage(w, h)
    val writer = wImg.pixelWriter
    writer.setPixels(0, 0, w, h, FMT, pixels, 0, w)
    return wImg
}
