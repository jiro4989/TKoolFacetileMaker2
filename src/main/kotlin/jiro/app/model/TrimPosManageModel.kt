package jiro.app.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.canvas.Canvas
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import jiro.app.data.Point
import jiro.app.util.IMAGE_HEIGHT
import jiro.app.util.IMAGE_WIDTH
import jiro.app.util.MAX_IMAGE_COUNT
import java.io.File

class TrimPosManageModel(
        private val imageView: ImageView
        , private val moveWidthComboBox: ComboBox<Double>
        , private val zoomRateSlider: Slider
        , private val shadowCanvas: Canvas
        , trimPosXLabel: Label
        , trimPosYLabel: Label
) {
    private var point = Point()
    private var zoomedPoint = point

    private val trimPosXProperty = SimpleStringProperty()
    private val trimPosYProperty = SimpleStringProperty()
    private val imageWidthProperty = SimpleDoubleProperty()
    private val imageHeightProperty = SimpleDoubleProperty()

    init {
        trimPosXLabel.textProperty().bind(trimPosXProperty)
        trimPosYLabel.textProperty().bind(trimPosYProperty)

        imageView.fitWidthProperty().bind(imageWidthProperty)
        imageView.fitHeightProperty().bind(imageHeightProperty)
        shadowCanvas.widthProperty().bind(imageWidthProperty)
        shadowCanvas.heightProperty().bind(imageHeightProperty)
    }

    /**
     * トリミング位置を左に移動する
     */
    fun moveLeftTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x - moveWidth, point.y)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を上に移動する
     */
    fun moveUpTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x, point.y - moveWidth)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を下に移動する
     */
    fun moveDownTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x, point.y + moveWidth)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を右に移動する
     */
    fun moveRightTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x + moveWidth, point.y)
        setTrimPoint(newPoint)
    }

    /**
     * 画像をセットする
     */
    fun setImage(filePath: String) {
        val img = Image("file:$filePath")
        val zoomRate = zoomRateSlider.value / 100
        val w = img.width * zoomRate
        val h = img.height * zoomRate

        imageWidthProperty.set(w)
        imageHeightProperty.set(h)

        // 初めて画像をセットするときはnullなので、そのときだけ実行
        val flg = imageView.image == null
        imageView.image = img
        if (flg) {
            // 画像の中央にフォーカスをセットしておく
            val nw = w / 2 - IMAGE_WIDTH / 2
            val nh = h / 2 - IMAGE_HEIGHT / 2
            setTrimPoint(Point(nw, nh))
        }
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

    private fun updateTrimPoint() = setTrimPoint(this.point)

    /**
     * トリミングの始点をセットする。
     */
    fun setTrimPoint(point: Point) {
        val imageWidth = imageWidthProperty.get()
        val imageHeight = imageHeightProperty.get()

        val x = Math.min(Math.max(point.x, 0.0), imageWidth - IMAGE_WIDTH)
        val y = Math.min(Math.max(point.y, 0.0), imageHeight - IMAGE_HEIGHT)
        this.point = Point(x, y)

        updateCanvas()
        updatePointLabels()
    }

    /**
     * トリミングの始点をセットする。
     */
    fun setTrimPointOnMouseDragged(point: Point) {
        val imageWidth = imageWidthProperty.get()
        val imageHeight = imageHeightProperty.get()

        val x = Math.min(Math.max(point.x - IMAGE_WIDTH / 2, 0.0), imageWidth - IMAGE_WIDTH)
        val y = Math.min(Math.max(point.y - IMAGE_HEIGHT / 2, 0.0), imageHeight - IMAGE_HEIGHT)
        this.point = Point(x, y)

        updateCanvas()
        updatePointLabels()
    }

    private fun updatePointLabels() {
        this.trimPosXProperty.set(this.point.x.toString())
        this.trimPosYProperty.set(this.point.y.toString())
    }

    /**
     * 拡大率の更新
     */
    fun updateZoomRate() {
        val zoomRate = zoomRateSlider.value / 100
        val image = imageView.image
        val w = Math.max(image.width * zoomRate, IMAGE_WIDTH.toDouble())
        val h = Math.max(image.height * zoomRate, IMAGE_HEIGHT.toDouble())
        imageWidthProperty.set(w)
        imageHeightProperty.set(h)
        updateCanvas()
        updateTrimPoint()
    }

    /**
     * 影キャンパスを再描画する
     */
    private fun updateCanvas() {
        val graphics = shadowCanvas.graphicsContext2D
        graphics.fill = Color.BLACK
        val w = shadowCanvas.width
        val h = shadowCanvas.height
        graphics.fillRect(0.0, 0.0, w, h)
        graphics.clearRect(this.point.x, this.point.y, IMAGE_WIDTH.toDouble(), IMAGE_HEIGHT.toDouble())
    }
}


