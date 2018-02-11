package jiro.app.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.shape.Rectangle
import jiro.app.data.Point
import jiro.app.util.IMAGE_HEIGHT
import jiro.app.util.IMAGE_WIDTH
import jiro.app.util.MAX_IMAGE_COUNT
import java.io.File

class TrimPosManageModel(
        private val imageView: ImageView
        , leftShadowRectangle: Rectangle
        , topShadowRectangle: Rectangle
        , rightShadowRectangle: Rectangle
        , bottomShadowRectangle: Rectangle
) {
    private var point = Point()

    private val leftShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val topShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val rightShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val bottomShadowRectangleWidthProperty = SimpleDoubleProperty()

    private val leftShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val topShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val rightShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val bottomShadowRectangleHeightProperty = SimpleDoubleProperty()

    init {
        leftShadowRectangle.widthProperty().bind(leftShadowRectangleWidthProperty)
        topShadowRectangle.widthProperty().bind(topShadowRectangleWidthProperty)
        rightShadowRectangle.widthProperty().bind(rightShadowRectangleWidthProperty)
        bottomShadowRectangle.widthProperty().bind(bottomShadowRectangleWidthProperty)

        leftShadowRectangle.heightProperty().bind(leftShadowRectangleHeightProperty)
        topShadowRectangle.heightProperty().bind(topShadowRectangleHeightProperty)
        rightShadowRectangle.heightProperty().bind(rightShadowRectangleHeightProperty)
        bottomShadowRectangle.heightProperty().bind(bottomShadowRectangleHeightProperty)
    }

    /**
     * 画像をセットする
     */
    fun setImageWith(filePath: String) {
        val img = Image("file:$filePath")
        val w = img.width
        val h = img.height
        imageView.prefWidth(w)
        imageView.prefHeight(h)
        imageView.fitWidth = w
        imageView.fitHeight = h
        imageView.image = img
    }

    /**
     * 座標の位置で画像をトリミングしたリストとして取得して返却する。
     */
    fun getTrimmedImages(files: List<File>): List<Image> {
        val max = if (files.size <= MAX_IMAGE_COUNT) files.size else MAX_IMAGE_COUNT
        val subFiles = files.subList(0, max)
        val x = point.x.toInt()
        val y = point.y.toInt()
        val w = 144
        val h = 144
        return subFiles
                .map { Image("file:${it.absolutePath}") }
                .map { WritableImage(it.pixelReader, x, y, w, h) }
    }

    /**
     * トリミングの始点をセットする。
     */
    fun setTrimPoint(point: Point) {
        val newPoint = Point(point.x - IMAGE_WIDTH / 2, point.y - IMAGE_HEIGHT / 2)
        this.point = newPoint

        val image = imageView.image
        val imageWidth = image.width
        val imageHeight = image.height

        val x = Math.min(Math.max(newPoint.x, 0.0), imageWidth - IMAGE_WIDTH)
        val y = Math.min(Math.max(newPoint.y, 0.0), imageHeight - IMAGE_HEIGHT)

        leftShadowRectangleWidthProperty.value = x
        leftShadowRectangleHeightProperty.value = imageHeight - y

        topShadowRectangleWidthProperty.value = x + IMAGE_WIDTH
        topShadowRectangleHeightProperty.value = y

        rightShadowRectangleWidthProperty.value = imageWidth - (x + IMAGE_WIDTH)
        rightShadowRectangleHeightProperty.value = y + IMAGE_HEIGHT

        bottomShadowRectangleWidthProperty.value = imageWidth - x
        bottomShadowRectangleHeightProperty.value = imageHeight - (y + IMAGE_HEIGHT)
    }
}


