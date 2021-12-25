package com.jiro4989.tkfm.controller;

import com.jiro4989.tkfm.util.Validator;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CropImageViewController {
  @FXML private TextField xInput;
  @FXML private TextField yInput;
  @FXML private TextField scaleInput;
  private boolean ok = false;

  @FXML
  private void initialize() {
    setListener(xInput);
    setListener(yInput);
    setListener(scaleInput);
  }

  private void setListener(TextField input) {
    input
        .textProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!Validator.isInteger(newValue)) {
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
    xInput.getScene().getWindow().hide();
  }

  /** Returns x */
  public double getX() {
    return Double.parseDouble(xInput.getText());
  }

  /** Returns y */
  public double getY() {
    return Double.parseDouble(yInput.getText());
  }

  /** Returns scale */
  public double getScale() {
    return Double.parseDouble(scaleInput.getText());
  }

  /** Returns ok button was pressed. */
  public boolean getOK() {
    return ok;
  }

  public void setX(double x) {
    xInput.setText("" + (int) x);
  }

  public void setY(double y) {
    yInput.setText("" + (int) y);
  }

  public void setScale(double scale) {
    scaleInput.setText("" + (int) scale);
  }
}
