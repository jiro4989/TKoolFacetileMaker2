package com.jiro4989.tkfm;

import com.jiro4989.tkfm.controller.CropImageViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class CropImageStage extends Stage {
  private CropImageViewController controller;

  public CropImageStage(double x, double y, double scale) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/crop_image_view.fxml"));
      VBox root = loader.load();
      controller = (CropImageViewController) loader.getController();

      // UI conficuration
      Scene scene = new Scene(root);
      setScene(scene);
      initStyle(StageStyle.UTILITY);
      initModality(Modality.APPLICATION_MODAL);

      // Set default value
      controller.setX(x);
      controller.setY(y);
      controller.setScale(scale);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Returns X that was set with controller. But method name getX was defined. */
  public double getParameterX() {
    return controller.getX();
  }

  /** Returns Y that was set with controller. But method name getX was defined. */
  public double getParameterY() {
    return controller.getY();
  }

  /** Returns Scale that was set with controller. But method name getX was defined. */
  public double getParameterScale() {
    return controller.getScale();
  }

  /** Returns ok button was pressed. */
  public boolean getOK() {
    return controller.getOK();
  }
}
