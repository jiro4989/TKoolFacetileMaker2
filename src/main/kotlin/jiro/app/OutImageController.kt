package jiro.app

import javafx.fxml.FXML
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import jiro.app.data.Point
import jiro.app.model.OutImagePreviewModel
import jiro.app.model.VersionModel

/**
 * 出力先の画像を管理するクラス
 */
class OutImageController {
    lateinit var tkoolVersion: VersionModel

    // 保存する画像のプレビューを描画するクラス
    @FXML
    private lateinit var outImageView: ImageView
    private lateinit var outImages: OutImagePreviewModel

    // 保存する画像に重ねるクリックイベントと外観を制御するクラス
    @FXML
    private lateinit var overLayerCanvas: Canvas

    @FXML
    private fun initialize() {

    }

    fun overLayerCanvasOnMouseClicked(mouseEvent: MouseEvent) {


    }

    fun outImageViewOnMouseClicked(mouseEvent: MouseEvent) {

    }

    /**
     * 画像出力の画面を再描画する
     */
    fun drawTile() {
        val graphics = overLayerCanvas.graphicsContext2D
        val w = tkoolVersion.getImageOneTileWidth().toDouble()
        val h = tkoolVersion.getImageOneTileHeight().toDouble()
        graphics.fill = Color.rgb(0, 0, 0, 1.0)
        graphics.textAlign = TextAlignment.CENTER
        graphics.textBaseline = VPos.CENTER
        graphics.font = Font(30.0)

        (0 until tkoolVersion.getMaxImageCount()).forEach {
            val point = Point().trim(it, tkoolVersion)
            val x = point.x
            val y = point.y
            graphics.strokeRect(x, y, w, h)

            val text = (it + 1).toString()
            val textX = x + tkoolVersion.getImageOneTileWidth() / 2
            val textY = y + tkoolVersion.getImageOneTileHeight() / 2
            graphics.fillText(text, textX, textY)
        }
    }

}
