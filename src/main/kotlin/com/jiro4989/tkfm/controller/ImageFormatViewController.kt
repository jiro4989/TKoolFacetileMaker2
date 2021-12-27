package com.jiro4989.tkfm.controller

import com.jiro4989.tkfm.model.ImageFormatModel
import com.jiro4989.tkfm.model.RectangleModel
import com.jiro4989.tkfm.util.isInteger
import javafx.fxml.FXML
import javafx.scene.control.TextField

class ImageFormatViewController {
  @FXML private lateinit var nameInput: TextField
  @FXML private lateinit var rowInput: TextField
  @FXML private lateinit var colInput: TextField
  @FXML private lateinit var tileWidthInput: TextField
  @FXML private lateinit var tileHeightInput: TextField
  private var ok = false

  @FXML
  private fun initialize() {
    setListener(rowInput)
    setListener(colInput)
    setListener(tileWidthInput)
    setListener(tileHeightInput)
  }

  private fun setListener(input: TextField) {
    input.textProperty().addListener { _, oldValue, newValue ->
      if (!isInteger(newValue) || "0".equals(newValue)) {
        input.text = oldValue
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
    rowInput.scene.window.hide()
  }

  /** Returns image format. */
  fun getImageFormat(): ImageFormatModel {
    val name = nameInput.text.trim()
    val row = rowInput.text.toInt()
    val col = colInput.text.toInt()
    val width = tileWidthInput.text.toInt().toDouble()
    val height = tileHeightInput.text.toInt().toDouble()
    val rect = RectangleModel(width, height)
    val fmt = ImageFormatModel(name, row, col, rect)
    return fmt
  }

  /** Returns ok button was pressed. */
  fun getOK() = ok
}
