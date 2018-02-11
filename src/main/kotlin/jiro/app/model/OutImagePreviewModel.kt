package jiro.app.model

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import jiro.app.data.Point
import jiro.app.util.*

internal val FMT = PixelFormat.getIntArgbInstance()

class OutImagePreviewModel(private val imageView: ImageView) {
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

    init {
        val writableImage = WritableImage(IMAGE_WIDTH * TILE_COLUMN_COUNT, IMAGE_HEIGHT * TILE_ROW_COUNT)
        imageView.image = writableImage
    }

    /**
     * すべての画像をクリアする。
     */
    fun clear() {
        val width = IMAGE_WIDTH
        val height = IMAGE_HEIGHT
        val wImg = WritableImage(width * TILE_COLUMN_COUNT, height * TILE_ROW_COUNT)
        imageView.image = wImg
    }

    /**
     * 番号の位置の画像を削除する。
     */
    private fun delete(index: Int) = delete(Point().trim(index))

    /**
     * 番号の位置の画像を削除する。
     * たとえばpointが番号1の範囲 x = 0~144, y = 0~144の範囲に存在するときに
     * pointの位置を(0, 0)に修正し、番号1の範囲で削除する
     */
    private fun delete(point: Point) {
        val fixedPoint = point.trim()
        val x = fixedPoint.x.toInt()
        val y = fixedPoint.y.toInt()
        val w = IMAGE_WIDTH
        val h = IMAGE_HEIGHT

        val reader = imageView.image.pixelReader
        val wImg = WritableImage(reader, w * TILE_COLUMN_COUNT, h * TILE_ROW_COUNT)
        val writer = wImg.pixelWriter

        // 削除なんで空の配列
        val pixels = IntArray(w * h)
        writer.setPixels(x, y, w, h, FMT, pixels, 0, w)

        imageView.image = wImg
    }

    /**
     * 番号の位置の画像を入れ替える
     */
    fun swapPos(from: Int, to: Int) {
        val fromPoint = Point().trim(from)
        val toPoint = Point().trim(to)
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

        val img = imageView.image
        val fromImg = img.getTrimmedImage(fixedFrom)
        val toImg = img.getTrimmedImage(fixedTo)
        setImage(fixedFrom, toImg)
        setImage(fixedTo, fromImg)
    }

    /**
     * 番号の位置の画像を左右反転する。
     */
    fun flip(index: Int) {
        val point = Point().trim(index)
        val srcImage = imageView.image

        // 指定の位置からトリミングした画像を取得
        val trimmedImage = srcImage.getTrimmedImage(point)
        val trimmedPixels = trimmedImage.getPixels()
        val w = IMAGE_WIDTH
        val h = IMAGE_HEIGHT

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
        imageView.image = srcWritableImage
    }

//    fun image(): BufferedImage {
//        val firstImage = images.filter { it != null }
//                .first() ?: return BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB)
//        val w = firstImage.width
//        val h = firstImage.height
//        val col = 4
//        val bimg = BufferedImage(w * 4, h * 2, BufferedImage.TYPE_INT_ARGB)
//        val g = bimg.graphics as Graphics2D
//        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
//
//        // 画像を貼り付ける
//        // 画像がnullのときは、その範囲を無視させたいためあえてfilterしない
//        images.forEachIndexed { index, image ->
//            val x = index % col * w
//            val y = index / col * h
//            g.drawImage(image, x, y, null)
//        }
//        g.dispose()
//        return bimg
//    }

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
        val point = Point(x, y)
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
        val pixels = img.getPixels()

        val srcImage = imageView.image
        val srcWidth = srcImage.width.toInt()
        val srcHeight = srcImage.height.toInt()
        val srcReader = srcImage.pixelReader
        val srcWritableImage = WritableImage(srcReader, srcWidth, srcHeight)
        val writer = srcWritableImage.pixelWriter
        writer.setPixels(x, y, w, h, FMT, pixels, 0, w)

        imageView.image = srcWritableImage
    }

    /**
     * 指定の番号の位置から画像をセットする。
     */
    fun setImages(index: Int = 0, images: List<Image>) {
        images.forEachIndexed { i, image ->
            val point = Point().trim(i + index)
            setImage(point, image)
        }
    }
}
