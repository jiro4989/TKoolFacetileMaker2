package com.jiro4989.tkfm;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class CropImage extends Stage {
  private CropImageController controller;

  public CropImage(double x, double y, double scale) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("CropImage.fxml"));
      VBox root = loader.load();
      controller = (CropImageController) loader.getController();

      // UI conficuration
      final int WIDTH = 800;
      final int HEIGHT = 600;
      Scene scene = new Scene(root, WIDTH, HEIGHT);
      setScene(scene);
      setMinWidth(WIDTH);
      setMinHeight(HEIGHT);
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
}
