package com.jiro4989.tkfm

import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import com.jiro4989.tkfm.data.Point
import com.jiro4989.tkfm.model.VersionModel
import com.jiro4989.tkfm.util.FMT
import com.jiro4989.tkfm.util.getPixels
import com.jiro4989.tkfm.util.getTrimmedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * 出力先の画像を管理するクラス
 */
class OutImageController {
    lateinit var tkoolVersion: VersionModel
    lateinit var mainController: MainController

    // 保存する画像のプレビューを描画するクラス
    @FXML
    private lateinit var outImageView: ImageView

    // 保存する画像に重ねるクリックイベントと外観を制御するクラス
    @FXML
    private lateinit var overLayerCanvas: Canvas

    // マウスクリック時の挙動定義
    private val setClickEvent: (Point, Image) -> Unit = { point, image ->
        setImage(point, image)
        swapImageIndexList.clear()
    }

    private val deleteClickEvent: (Point, Image) -> Unit = { point, _ ->
        delete(point)
        swapImageIndexList.clear()
    }

    private val swapClickEvent: (Point, Image) -> Unit = { point, _ ->
        swapImageIndexList += point.index
        if (2 <= swapImageIndexList.size) {
            val from = swapImageIndexList[0]
            val to = swapImageIndexList[1]
            swapPos(from, to)
            swapImageIndexList.clear()
        }
    }

    private val flipClickEvent: (Point, Image) -> Unit = { point, _ ->
        flip(point.index)
        swapImageIndexList.clear()
    }

    // マウスクリック時のイベント
    private var clickEventState = setClickEvent

    // 左右反転の対象のリスト
    private val swapImageIndexList = mutableListOf<Int>()

    @FXML
    private fun initialize() {
    }

    /**
     * クリックしたタイルの位置に画像を描画する。
     */
    fun outImageViewOnMouseClicked(mouseEvent: MouseEvent) {
        val images = mainController.getSelectedImages()
        onMouseClicked(mouseEvent, images)
    }

    fun overLayerCanvasOnMouseClicked(mouseEvent: MouseEvent) {
        outImageViewOnMouseClicked(mouseEvent)
    }

    /**
     * 画像出力の画面を再描画する
     */
    fun drawTile() {
        val w = tkoolVersion.getImageOneTileWidth().toDouble()
        val h = tkoolVersion.getImageOneTileHeight().toDouble()
        val graphics = overLayerCanvas.graphicsContext2D
        graphics.fill = Color.rgb(0, 0, 0, 1.0)
        graphics.textAlign = TextAlignment.CENTER
        graphics.textBaseline = VPos.CENTER
        graphics.font = Font(30.0)

        (0 until tkoolVersion.getMaxImageCount()).forEach {
            val point = Point(version = tkoolVersion).trim(it)
            val x = point.x
            val y = point.y
            graphics.strokeRect(x, y, w, h)

            val text = (it + 1).toString()
            val textX = x + tkoolVersion.getImageOneTileWidth() / 2
            val textY = y + tkoolVersion.getImageOneTileHeight() / 2
            graphics.fillText(text, textX, textY)
        }
    }

    /**
     * すべての画像をクリアする。
     */
    fun clear() {
        val width = tkoolVersion.getImageOneTileWidth()
        val height = tkoolVersion.getImageOneTileHeight()
        val wImg = WritableImage(width * tkoolVersion.getImageColumnCount(), height * tkoolVersion.getImageRowCount())
        outImageView.image = wImg
    }

    /**
     * 番号の位置の画像を削除する。
     */
    private fun delete(index: Int) = delete(Point(version = tkoolVersion).trim(index))

    /**
     * 番号の位置の画像を削除する。
     * たとえばpointが番号1の範囲 x = 0~144, y = 0~144の範囲に存在するときに
     * pointの位置を(0, 0)に修正し、番号1の範囲で削除する
     */
    private fun delete(point: Point) {
        val fixedPoint = point.trim()
        val x = fixedPoint.x.toInt()
        val y = fixedPoint.y.toInt()
        val w = tkoolVersion.getImageOneTileWidth()
        val h = tkoolVersion.getImageOneTileHeight()

        val reader = outImageView.image.pixelReader
        val wImg = WritableImage(reader, w * tkoolVersion.getImageColumnCount(), h * tkoolVersion.getImageRowCount())
        val writer = wImg.pixelWriter

        // 削除なんで空の配列
        val pixels = IntArray(w * h)
        writer.setPixels(x, y, w, h, FMT, pixels, 0, w)

        outImageView.image = wImg
    }

    /**
     * 番号の位置の画像を入れ替える
     */
    fun swapPos(from: Int, to: Int) {
        val fromPoint = Point(version = tkoolVersion).trim(from)
        val toPoint = Point(version = tkoolVersion).trim(to)
        swapPos(fromPoint, toPoint)
    }

    /**
     * 2つの座標の画像を入れ替える
     * 座標の位置に該当するタイル上の画像をまるっと切り出して入れ替えるため、
     * 事前に切り出し開始位置の左上のpointを計算する必要はない。
     */
    fun swapPos(from: Point, to: Point) {
        val fixedFrom = from.trim()
        val fixedTo = to.trim()

        val img = outImageView.image
        val fromImg = img.getTrimmedImage(fixedFrom, tkoolVersion)
        val toImg = img.getTrimmedImage(fixedTo, tkoolVersion)
        setImage(fixedFrom, toImg)
        setImage(fixedTo, fromImg)
    }

    /**
     * 番号の位置の画像を左右反転する。
     */
    fun flip(index: Int) {
        val point = Point(version = tkoolVersion).trim(index)
        val srcImage = outImageView.image

        // 指定の位置からトリミングした画像を取得
        val trimmedImage = srcImage.getTrimmedImage(point, tkoolVersion)
        val trimmedPixels = trimmedImage.getPixels(version = tkoolVersion)
        val w = tkoolVersion.getImageOneTileWidth()
        val h = tkoolVersion.getImageOneTileHeight()

        // 切り出したpixelの左右反転したpixelを取得
        val newPixels = IntArray(trimmedPixels.size)
        trimmedPixels.forEachIndexed { i, pixel ->
            val a = (i + w) / w * w
            val b = i / w * w
            val flipedIndex = a + b - i - 1
            newPixels[flipedIndex] = pixel
        }

        // 変更元の画像から書き込み可能画像を生成
        val srcReader = srcImage.pixelReader
        val srcWidth = srcImage.width.toInt()
        val srcHeight = srcImage.height.toInt()
        val srcWritableImage = WritableImage(srcReader, srcWidth, srcHeight)
        val writer = srcWritableImage.pixelWriter
        val x = point.x.toInt()
        val y = point.y.toInt()
        // 変更元の画像の指定の位置に左右反転した画像をセット
        writer.setPixels(x, y, w, h, FMT, newPixels, 0, w)

        // 左右反転処理を施した新しい画像をセット
        outImageView.image = srcWritableImage
    }

    fun changeClickModeToSetImage() {
        clickEventState = setClickEvent
    }

    fun changeClickModeToDeleteImage() {
        clickEventState = deleteClickEvent
    }

    fun changeClickModeToSwapImage() {
        clickEventState = swapClickEvent
    }

    fun changeClickModeToFlipImage() {
        clickEventState = flipClickEvent
    }

    fun onMouseClicked(mouseEvent: MouseEvent, images: List<Image>) {
        val x = mouseEvent.x
        val y = mouseEvent.y
        val point = Point(x, y, version = tkoolVersion)
        clickEventState(point, images[0])
    }

    /**
     * 画像をposの位置に貼り付ける
     * @param point 画像の貼り付け開始位置
     * @param img 貼り付ける画像
     */
    private fun setImage(point: Point, img: Image) {
        delete(point)
        val fixedPoint = point.trim()

        val x = fixedPoint.x.toInt()
        val y = fixedPoint.y.toInt()
        val w = img.width.toInt()
        val h = img.height.toInt()
        val pixels = img.getPixels(version = tkoolVersion)

        val srcImage = outImageView.image
        val srcWidth = srcImage.width.toInt()
        val srcHeight = srcImage.height.toInt()
        val srcReader = srcImage.pixelReader
        val srcWritableImage = WritableImage(srcReader, srcWidth, srcHeight)
        val writer = srcWritableImage.pixelWriter
        writer.setPixels(x, y, w, h, FMT, pixels, 0, w)

        outImageView.image = srcWritableImage
    }

    /**
     * 指定の番号の位置から画像をセットする。
     */
    fun setImages(index: Int = 0, images: List<Image>) {
        images.forEachIndexed { i, image ->
            val point = Point(version = tkoolVersion).trim(i + index)
            setImage(point, image)
        }
    }

    /**
     * 表示中の画像を保存する。
     */
    @Throws(IOException::class)
    fun saveImage(file: File) {
        val srcImage = outImageView.image
        val convertedImage = SwingFXUtils.fromFXImage(srcImage, null)
        ImageIO.write(convertedImage, "PNG", file)
    }

    /**
     * 画像を初期化する
     */
    fun initImage() {
        val r = tkoolVersion.getImageRowCount()
        val c = tkoolVersion.getImageColumnCount()
        val w = tkoolVersion.getImageOneTileWidth()
        val h = tkoolVersion.getImageOneTileHeight()
        outImageView.image = WritableImage(c*w, r*h)
    }

}
