package jiro.app

import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
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

    private lateinit var mainService: MainService

    fun overLayerCanvasOnMouseClicked(mouseEvent: MouseEvent) {


    }

    fun outImageViewOnMouseClicked(mouseEvent: MouseEvent) {

    }

}
