package com.jiro4989.tkfm

import com.jiro4989.tkfm.controller.ImageFormatViewController
import com.jiro4989.tkfm.model.ImageFormatModel
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

class ImageFormatStage : Stage {
  private lateinit var controller: ImageFormatViewController

  constructor(windowX: Double, windowY: Double) {
    try {
      val loader = FXMLLoader(this.javaClass.getResource("fxml/image_format_view.fxml"))
      val root = loader.load() as VBox
      controller = loader.getController() as ImageFormatViewController

      // UI conficuration
      val scene = Scene(root)
      setTitle("画像フォーマットを追加")
      setX(windowX + 50.0)
      setY(windowY + 50.0)
      setScene(scene)
      initStyle(StageStyle.UTILITY)
      initModality(Modality.APPLICATION_MODAL)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun getImageFormat() = controller.getImageFormat()

  /** Returns ok button was pressed. */
  fun getOK() = controller.getOK()
}
