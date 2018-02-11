package jiro.app.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
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
        , private val moveWidthComboBox: ComboBox<Double>
        , leftShadowRectangle: Rectangle
        , topShadowRectangle: Rectangle
        , rightShadowRectangle: Rectangle
        , bottomShadowRectangle: Rectangle
        , overLayerRectangle: Rectangle
        , trimPosXLabel: Label
        , trimPosYLabel: Label
) {
    private var point = Point()
    private val trimPosXProperty = SimpleStringProperty()
    private val trimPosYProperty = SimpleStringProperty()

    private val leftShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val topShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val rightShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val bottomShadowRectangleWidthProperty = SimpleDoubleProperty()
    private val overLayerRectangleWidthProperty = SimpleDoubleProperty()

    private val leftShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val topShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val rightShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val bottomShadowRectangleHeightProperty = SimpleDoubleProperty()
    private val overLayerRectangleHeightProperty = SimpleDoubleProperty()

    init {
        leftShadowRectangle.widthProperty().bind(leftShadowRectangleWidthProperty)
        topShadowRectangle.widthProperty().bind(topShadowRectangleWidthProperty)
        rightShadowRectangle.widthProperty().bind(rightShadowRectangleWidthProperty)
        bottomShadowRectangle.widthProperty().bind(bottomShadowRectangleWidthProperty)
        overLayerRectangle.widthProperty().bind(overLayerRectangleWidthProperty)

        leftShadowRectangle.heightProperty().bind(leftShadowRectangleHeightProperty)
        topShadowRectangle.heightProperty().bind(topShadowRectangleHeightProperty)
        rightShadowRectangle.heightProperty().bind(rightShadowRectangleHeightProperty)
        bottomShadowRectangle.heightProperty().bind(bottomShadowRectangleHeightProperty)
        overLayerRectangle.heightProperty().bind(overLayerRectangleHeightProperty)

        trimPosXLabel.textProperty().bind(trimPosXProperty)
        trimPosYLabel.textProperty().bind(trimPosYProperty)
    }

    /**
     * トリミング位置を左に移動する
     */
    fun moveLeftTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val revisionWidth = IMAGE_WIDTH / 2
        val revisionHeight = IMAGE_HEIGHT / 2
        val newPoint = Point(point.x - moveWidth + revisionWidth, point.y + revisionHeight)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を上に移動する
     */
    fun moveUpTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val revisionWidth = IMAGE_WIDTH / 2
        val revisionHeight = IMAGE_HEIGHT / 2
        val newPoint = Point(point.x + revisionWidth, point.y - moveWidth + revisionHeight)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を下に移動する
     */
    fun moveDownTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val revisionWidth = IMAGE_WIDTH / 2
        val revisionHeight = IMAGE_HEIGHT / 2
        val newPoint = Point(point.x + revisionWidth, point.y + moveWidth + revisionHeight)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を右に移動する
     */
    fun moveRightTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val revisionWidth = IMAGE_WIDTH / 2
        val revisionHeight = IMAGE_HEIGHT / 2
        val newPoint = Point(point.x + moveWidth + revisionWidth, point.y + revisionHeight)
        setTrimPoint(newPoint)
    }

    /**
     * 画像をセットする
     */
    fun setImage(filePath: String) {
        val img = Image("file:$filePath")
        val w = img.width
        val h = img.height
        imageView.prefWidth(w)
        imageView.prefHeight(h)
        imageView.fitWidth = w
        imageView.fitHeight = h

        // 初めて画像をセットするときはnullなので、そのときだけ実行
        val flg = imageView.image == null
        imageView.image = img
        if (flg) {
            // 画像の中央にフォーカスをセットしておく
            val nw = w / 2
            val nh = h / 2
            setTrimPoint(Point(nw, nh))
        }

        // マウスのクリック可能領域の更新
        overLayerRectangleWidthProperty.set(w)
        overLayerRectangleHeightProperty.set(h)
    }

    /**
     * 座標の位置で画像をトリミングしたリストとして取得して返却する。
     */
    fun getTrimmedImages(files: List<File>): List<Image> {
        val max = if (files.size <= MAX_IMAGE_COUNT) files.size else MAX_IMAGE_COUNT
        val subFiles = files.subList(0, max)
        val x = point.x.toInt()
        val y = point.y.toInt()
        val w = IMAGE_WIDTH
        val h = IMAGE_HEIGHT
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

        // ラベルのテキストも更新
        this.trimPosXProperty.set(x.toString())
        this.trimPosYProperty.set(y.toString())

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


