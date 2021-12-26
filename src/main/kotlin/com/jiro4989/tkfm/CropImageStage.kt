package com.jiro4989.tkfm

import com.jiro4989.tkfm.controller.CropImageViewController
import javafx.fxml.FXMLLoader
import javafx.scene.*
import javafx.scene.layout.*
import javafx.stage.*

class CropImageStage : Stage {
  private lateinit var controller: CropImageViewController

  constructor(x: Double, y: Double, scale: Double) {
    try {
      val loader = FXMLLoader(this.javaClass.getResource("fxml/crop_image_view.fxml"))
      val root = loader.load() as VBox

      // UI conficuration
      val scene = Scene(root)
      setScene(scene)
      initStyle(StageStyle.UTILITY)
      initModality(Modality.APPLICATION_MODAL)

      // Set default value
      controller =
          (loader.getController() as CropImageViewController).apply {
            setX(x)
            setY(y)
            setScale(scale)
          }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /** Returns X that was set with controller. But method name getX was defined. */
  fun getParameterX() = controller.getX()

  /** Returns Y that was set with controller. But method name getX was defined. */
  fun getParameterY() = controller.getY()

  /** Returns Scale that was set with controller. But method name getX was defined. */
  fun getParameterScale() = controller.getScale()

  /** Returns ok button was pressed. */
  fun getOK() = controller.getOK()
}
