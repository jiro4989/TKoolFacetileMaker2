package com.jiro4989.tkfm;

import com.jiro4989.tkfm.model.ImageFormat;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class ImageFormatStage extends Stage {
  private ImageFormatController controller;

  public ImageFormatStage() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageFormat.fxml"));
      VBox root = loader.load();
      controller = (ImageFormatController) loader.getController();

      // UI conficuration
      Scene scene = new Scene(root);
      setScene(scene);
      initStyle(StageStyle.UTILITY);
      initModality(Modality.APPLICATION_MODAL);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ImageFormat getImageFormat() {
    return controller.getImageFormat();
  }

  /** Returns ok button was pressed. */
  public boolean getOK() {
    return controller.getOK();
  }
}
