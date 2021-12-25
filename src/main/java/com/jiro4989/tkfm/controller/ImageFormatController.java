package com.jiro4989.tkfm.controller;

import com.jiro4989.tkfm.data.Rectangle;
import com.jiro4989.tkfm.model.ImageFormat;
import com.jiro4989.tkfm.util.Validator;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ImageFormatController {
  @FXML private TextField nameInput;
  @FXML private TextField rowInput;
  @FXML private TextField colInput;
  @FXML private TextField tileWidthInput;
  @FXML private TextField tileHeightInput;
  private boolean ok = false;

  @FXML
  private void initialize() {
    setListener(rowInput);
    setListener(colInput);
    setListener(tileWidthInput);
    setListener(tileHeightInput);
  }

  private void setListener(TextField input) {
    input
        .textProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!Validator.isInteger(newValue) || "0".equals(newValue)) {
                  input.setText(oldValue);
                }
              }
            });
  }

  @FXML
  private void okButtonOnClicked() {
    ok = true;
    hideUI();
  }

  @FXML
  private void cancelButtonOnClicked() {
    ok = false;
    hideUI();
  }

  private void hideUI() {
    rowInput.getScene().getWindow().hide();
  }

  /** Returns x */
  public ImageFormat getImageFormat() {
    var name = nameInput.getText().trim();
    var row = Integer.parseInt(rowInput.getText());
    var col = Integer.parseInt(colInput.getText());
    var width = Integer.parseInt(tileWidthInput.getText());
    var height = Integer.parseInt(tileHeightInput.getText());
    var rect = new Rectangle(width, height);
    var fmt = new ImageFormat(name, row, col, rect);
    return fmt;
  }

  /** Returns ok button was pressed. */
  public boolean getOK() {
    return ok;
  }
}
