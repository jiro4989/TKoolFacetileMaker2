package com.jiro4989.tkfm.controller

import com.jiro4989.tkfm.model.ImageFormatModel
import com.jiro4989.tkfm.model.ImageFormatViewModel
import com.jiro4989.tkfm.model.RectangleModel
import com.jiro4989.tkfm.model.isAvailableInteger
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField

// @FXMLアノテーション経由で呼び出されるメソッドが指摘されるのを無視する
@Suppress("UnusedPrivateMember")
class ImageFormatViewController {
  @FXML private lateinit var nameInput: TextField
  @FXML private lateinit var rowInput: TextField
  @FXML private lateinit var colInput: TextField
  @FXML private lateinit var tileWidthInput: TextField
  @FXML private lateinit var tileHeightInput: TextField
  @FXML private lateinit var okButton: Button
  private val imageFormatViewModel = ImageFormatViewModel()
  private var ok = false

  @FXML
  private fun initialize() {
    Bindings.bindBidirectional(nameInput.textProperty(), imageFormatViewModel.nameProperty)
    Bindings.bindBidirectional(rowInput.textProperty(), imageFormatViewModel.rowProperty)
    Bindings.bindBidirectional(colInput.textProperty(), imageFormatViewModel.colProperty)
    Bindings.bindBidirectional(
        tileWidthInput.textProperty(), imageFormatViewModel.tileWidthProperty)
    Bindings.bindBidirectional(
        tileHeightInput.textProperty(), imageFormatViewModel.tileHeightProperty)
    setIntegerChangeListener(rowInput)
    setIntegerChangeListener(colInput)
    setIntegerChangeListener(tileWidthInput)
    setIntegerChangeListener(tileHeightInput)
  }

  private fun setIntegerChangeListener(input: TextField) {
    input.textProperty().addListener { _, oldValue, newValue ->
      if (!isAvailableInteger(newValue, true)) {
        input.text = oldValue
      }
    }
  }

  /** OKボタンの押せる/押せないの状態を切り替える。 切り替えはTextFieldがすべて正常な値を設定しているかどうか */
  @FXML
  private fun changeStateOKButton() {
    okButton.setDisable(!imageFormatViewModel.validate())
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
