package com.jiro4989.tkfm.controller

import com.jiro4989.tkfm.util.ValidationUtil
import javafx.fxml.FXML
import javafx.scene.control.TextField

class CropImageViewController {
  @FXML private lateinit var xInput: TextField
  @FXML private lateinit var yInput: TextField
  @FXML private lateinit var scaleInput: TextField
  private var ok = false

  @FXML
  private fun initialize() {
    setListener(xInput)
    setListener(yInput)
    setListener(scaleInput)
  }

  private fun setListener(input: TextField) {
    input.textProperty().addListener { _, oldValue, newValue ->
      if (!ValidationUtil.isInteger(newValue)) {
        input.setText(oldValue)
      }
    }
  }

  @FXML
  private fun okButtonOnClicked() {
    ok = true
    hideUI()
  }

  @FXML
  private fun cancelButtonOnClicked() {
    ok = false
    hideUI()
  }

  private fun hideUI() {
    xInput.scene.window.hide()
  }

  fun getX() = xInput.text.toDouble()
  fun getY() = yInput.text.toDouble()
  fun getScale() = scaleInput.text.toDouble()
  fun getOK() = ok

  private fun setter(input: TextField, value: Double) {
    input.text = value.toInt().toString()
  }

  fun setX(x: Double) = setter(xInput, x)
  fun setY(y: Double) = setter(yInput, y)
  fun setScale(scale: Double) = setter(scaleInput, scale)
}
