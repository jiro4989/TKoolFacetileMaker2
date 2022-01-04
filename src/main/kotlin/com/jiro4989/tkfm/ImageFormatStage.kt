package com.jiro4989.tkfm

import com.jiro4989.tkfm.controller.ImageFormatViewController
import com.jiro4989.tkfm.model.ImageFormatModel
import com.jiro4989.tkfm.util.warning
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

private const val PADDING_WIDTH = 50.0

class ImageFormatStage : Stage {
  private lateinit var controller: ImageFormatViewController

  constructor(windowX: Double, windowY: Double) {
    val loader = FXMLLoader(this.javaClass.getResource("fxml/image_format_view.fxml"))
    val root = loader.load() as VBox
    controller = loader.getController() as ImageFormatViewController

    // UI conficuration
    val scene = Scene(root)
    setTitle("画像フォーマットを追加")

    // アプリケーションウィンドウからの相対位置として
    // ウィンドウの左上頂点から右下方向に多少ずらした場所を初期位置とする
    setX(windowX + PADDING_WIDTH)
    setY(windowY + PADDING_WIDTH)

    setScene(scene)
    initStyle(StageStyle.UTILITY)
    initModality(Modality.APPLICATION_MODAL)
  }

  fun getImageFormat() = controller.getImageFormat()

  /** Returns ok button was pressed. */
  fun getOK() = controller.getOK()
}
