package com.jiro4989.tkfm;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.*;

public class CropImageController {
  @FXML private TextField xInput;
  @FXML private TextField yInput;
  @FXML private TextField scaleInput;

  @FXML
  private void initialize() {
    xInput.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
          xInput.setText(newValue.replaceAll("[^\\d]", ""));
        }
      }
    });
  }

  // TODO
  public double getX() {
    return 0;
  }

  // TODO
  public double getY() {
    return 0;
  }

  // TODO
  public double getScale() {
    return 0;
  }

}
