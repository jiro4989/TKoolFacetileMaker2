package jiro.app.model

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import jiro.app.data.Point
import jiro.app.util.MAX_IMAGE_COUNT
import java.io.File

class TrimPosManageModel(private val imageView: ImageView) {
    var point = Point()

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
}


