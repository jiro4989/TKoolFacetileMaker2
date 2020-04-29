package jiro.app.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import jiro.app.data.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class TrimPosManageModel(
        private val imageView: ImageView
        , private val moveWidthComboBox: ComboBox<Double>
        , private val zoomRateSlider: Slider
        , private val shadowCanvas: Canvas
        , trimPosXLabel: Label
        , trimPosYLabel: Label
        , private var version: VersionModel
) {
    private var point = Point(version = version)
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
        val newPoint = Point(point.x - moveWidth, point.y, version)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を上に移動する
     */
    fun moveUpTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x, point.y - moveWidth, version)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を下に移動する
     */
    fun moveDownTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x, point.y + moveWidth, version)
        setTrimPoint(newPoint)
    }

    /**
     * トリミング位置を右に移動する
     */
    fun moveRightTrimPos() {
        val moveWidth = moveWidthComboBox.selectionModel.selectedItem
        val newPoint = Point(point.x + moveWidth, point.y, version)
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
            val nw = w / 2 - version.getImageOneTileWidth() / 2
            val nh = h / 2 - version.getImageOneTileHeight() / 2
            setTrimPoint(Point(nw, nh, version))
        }
    }

    /**
     * 座標の位置で画像をトリミングしたリストとして取得して返却する。
     */
    fun getTrimmedImages(files: List<File>): List<Image> {
        val zoomRate = zoomRateSlider.value / 100
        val max = if (files.size <= version.getMaxImageCount()) files.size else version.getMaxImageCount()
        val subFiles = files.subList(0, max)
        val x = point.x.toInt()
        val y = point.y.toInt()
        val w = version.getImageOneTileWidth()
        val h = version.getImageOneTileHeight()

        return subFiles
                .map { ImageIO.read(it) }
                .map {
                    // バイキュービック補間でスケーリング
                    // もっとスマートな方法ないもんかね...
                    val w = (it.width * zoomRate).toInt()
                    val h = (it.height * zoomRate).toInt()
                    val bImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
                    val g = bImg.createGraphics()
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
                    g.scale(zoomRate, zoomRate)
                    g.drawImage(it, 0, 0, null)
                    g.dispose()
                    bImg
                }
                .map { SwingFXUtils.toFXImage(it, null) }
                .map { WritableImage(it.pixelReader, x, y, w, h) }
    }

    private fun updateTrimPoint() = setTrimPoint(this.point)

    /**
     * トリミングの始点をセットする。
     */
    fun setTrimPoint(point: Point) {
        val imageWidth = imageWidthProperty.get()
        val imageHeight = imageHeightProperty.get()

        val x = Math.min(Math.max(point.x, 0.0), imageWidth - version.getImageOneTileWidth())
        val y = Math.min(Math.max(point.y, 0.0), imageHeight - version.getImageOneTileHeight())
        this.point = Point(x, y, version)

        updateCanvas()
        updatePointLabels()
    }

    /**
     * トリミングの始点をセットする。
     */
    fun setTrimPointOnMouseDragged(point: Point) {
        val imageWidth = imageWidthProperty.get()
        val imageHeight = imageHeightProperty.get()

        val x = Math.min(Math.max(point.x - version.getImageOneTileWidth() / 2, 0.0), imageWidth - version.getImageOneTileWidth())
        val y = Math.min(Math.max(point.y - version.getImageOneTileHeight() / 2, 0.0), imageHeight - version.getImageOneTileHeight())
        this.point = Point(x, y, version)

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
        val w = Math.max(image.width * zoomRate, version.getImageOneTileWidth().toDouble())
        val h = Math.max(image.height * zoomRate, version.getImageOneTileHeight().toDouble())
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
        graphics.clearRect(this.point.x, this.point.y, version.getImageOneTileWidth().toDouble(), version.getImageOneTileHeight().toDouble())
    }

    /**
     * ツクールのバージョン情報を更新し、画面を再描画する
     */
    fun updateTkoolVersion(tkoolVersion: VersionModel) {
        version = tkoolVersion
        updateCanvas()
    }
}


