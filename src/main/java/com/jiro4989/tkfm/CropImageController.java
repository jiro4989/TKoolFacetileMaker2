package com.jiro4989.tkfm;

import com.jiro4989.tkfm.util.Validator;
import javafx.beans.value.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CropImageController {
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
                if (!Validator.isInteger(newValue.trim())) {
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
  double getX() {
    return Double.parseDouble(xInput.getText());
  }

  /** Returns y */
  double getY() {
    return Double.parseDouble(yInput.getText());
  }

  /** Returns scale */
  double getScale() {
    return Double.parseDouble(scaleInput.getText());
  }

  /** Returns ok button was pressed. */
  boolean getOK() {
    return ok;
  }

  void setX(double x) {
    xInput.setText("" + x);
  }

  void setY(double y) {
    yInput.setText("" + y);
  }

  void setScale(double scale) {
    scaleInput.setText("" + scale);
  }
}
